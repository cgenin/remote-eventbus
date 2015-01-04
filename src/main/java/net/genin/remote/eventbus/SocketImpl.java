package net.genin.remote.eventbus;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Server implementation by socket.
 */
final class SocketImpl {


    private final static Gson gson = new Gson();

    private SocketImpl() {
    }

    public static class JsonObj {
        public String className;

        public String object;

        @SuppressWarnings("unchecked")
        public static <T> T deserialize(final String text) throws Exception {
            Preconditions.checkNotNull(text);
            final JsonObj jsonObj = gson.fromJson(text, JsonObj.class);
            final Class<T> clazz = (Class<T>) Class.forName(jsonObj.className);
            return gson.fromJson(jsonObj.object, clazz);
        }


        @SuppressWarnings("unchecked")
        public static String serialize(final Object obj) throws Exception {
            Preconditions.checkNotNull(obj);
            final JsonObj jsonObj = new JsonObj();
            jsonObj.object = gson.toJson(obj);
            jsonObj.className = obj.getClass().getName();
            return gson.toJson(jsonObj);
        }

    }


    public static class Client extends EventBusClient {

        @Override
        public void post(Object msg) {
            try {
                Preconditions.checkNotNull(msg);
                final String text = JsonObj.serialize(msg);
                try (final Socket socket = new Socket(host, port)) {
                    final OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(text.getBytes());
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (Exception e) {
                Throwables.propagate(e);
            }
        }
    }

    public static class Server extends EventBusServer implements Runnable {
        private CountDownLatch latch =  new CountDownLatch(1);
        private boolean isStopped = true;
        private Future<?> future;

        @Override
        public boolean start() throws Exception {
            if (!isStopped) return true;
            future = Executors.newSingleThreadExecutor().submit(this);
            latch.await();
            return true;
        }


        @Override
        public void stop() {
            isStopped = true;
            future.cancel(true);
        }

        private static int findFreePort() {
            ServerSocket socket = null;
            try {
                socket = new ServerSocket(0);
                socket.setReuseAddress(true);
                int port = socket.getLocalPort();

                return port;
            } catch (IOException e) {
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                    }
                }
            }
            throw new IllegalStateException("Could not find a free TCP/IP port to start embedded Jetty HTTP Server on");
        }

        @Override
        public void run() {
            try {
                port =findFreePort();
                final ServerSocket serverSocket = new ServerSocket(port);
                serverSocket.setReuseAddress(true);
                latch.countDown();
                isStopped = false;
                while (!isStopped) {
                    try (final java.net.Socket socket = serverSocket.accept()) {
                        final String text = CharStreams.toString(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                        final Object msg = JsonObj.deserialize(text);
                        sendToBus(msg);
                    }
                }
            } catch (final Exception e) {
                Throwables.propagate(e);
            }

        }

    }
}
