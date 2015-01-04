package net.genin.remote.eventbus;

import com.google.common.base.Preconditions;

/**
 * Remote Event Bus Server interface.
 */
public interface RemoteEventBusServer {


    String getGroupName();

    void setGroupName(String groupName);

    boolean start() throws Exception;

    Integer getPort();


    void stop();

    void register(Object object);

    void unregister(Object object);

    public static class Builder {

        private String groupName;
        private RemoteEventBusServer server;

        public Builder() {
        }


        public Builder groupName(String groupName) {
            Preconditions.checkNotNull(groupName);
            this.groupName = groupName;
            return this;
        }

        public Builder useHazelcast() {
            server = new HazelcastImpl.Server();
            return this;
        }

        public Builder useSocket() {
            server = new SocketImpl.Server();
            return this;
        }


        public RemoteEventBusServer launch() throws Exception {
            Preconditions.checkNotNull(groupName, "The group name is required.");
            Preconditions.checkNotNull(server, "You must specify an implementation.");
            server.setGroupName(groupName);
            server.start();
            return server;
        }
    }


}
