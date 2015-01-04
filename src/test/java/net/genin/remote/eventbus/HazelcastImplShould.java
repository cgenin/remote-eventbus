package net.genin.remote.eventbus;

import com.google.common.eventbus.Subscribe;
import org.junit.Test;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Integration test.
 */
public class HazelcastImplShould {

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static class MyBean implements Serializable {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Listen {

        public int count = 0;

        @Subscribe
        public void test(final MyBean myBean) {
            assertNotNull(myBean);
            assertEquals("Result OK", myBean.getName());
            count++;
            countDownLatch.countDown();
        }
    }

    @Test
    public void connectBetweenServerAndClient() throws Exception {
        countDownLatch = new CountDownLatch(1);
        final RemoteEventBusServer server = new RemoteEventBusServer.Builder().groupName("TEST").useHazelcast().launch();
        final Listen listen = new Listen();
        server.register(listen);


        final RemoteEventBusClient client = new RemoteEventBusClient.Builder().port(server.getPort()).groupName(server.getGroupName()).useHazelcast().build();
        final MyBean msg = new MyBean();
        msg.setName("Result OK");

        client.post(msg);
        countDownLatch.await();
        assertEquals(1, listen.count);
        server.stop();
    }

    @Test(expected = Exception.class)
    public void notBeSendIfThePortMismatch() throws Exception {
        final RemoteEventBusServer server = new RemoteEventBusServer.Builder().groupName("TEST").useHazelcast().launch();
        final Listen listen = new Listen();
        server.register(listen);


        final RemoteEventBusClient client = new RemoteEventBusClient.Builder()
                .port(server.getPort() + 1).groupName(server.getGroupName()).useHazelcast().build();
        final MyBean msg = new MyBean();
        msg.setName("Result OK");

        client.post(msg);
        server.stop();
    }

    @Test(expected = Exception.class)
    public void notBeSendIfTheGroupMismatch() throws Exception {
        final RemoteEventBusServer server = new RemoteEventBusServer.Builder().groupName("TEST").useHazelcast().launch();
        final Listen listen = new Listen();
        server.register(listen);


        final RemoteEventBusClient client = new RemoteEventBusClient.Builder()
                .port(server.getPort()).groupName(server.getGroupName()+"0").useHazelcast().build();
        final MyBean msg = new MyBean();
        msg.setName("Result OK");

        client.post(msg);
        server.stop();
    }
}
