package net.genin.remote.eventbus;

import com.google.common.base.Preconditions;

/**
 * Client Interface.
 */
public interface RemoteEventBusClient {


    void setHost(String host);

    void setPort(Integer port);

    void setGroupName(String groupName);

    void post(Object msg);


    public static class Builder {

        private Integer port;
        private String host = "127.0.0.1";
        private String groupName;
        private RemoteEventBusClient client;

        public Builder() {
        }

        public Builder port(Integer port) {
            Preconditions.checkNotNull(port);
            this.port = port;
            return this;
        }

        public Builder host(String host) {
            Preconditions.checkNotNull(host);
            this.host = host;
            return this;
        }

        public Builder groupName(String groupName) {
            Preconditions.checkNotNull(groupName);
            this.groupName = groupName;
            return this;
        }

        public Builder useHazelcast() {
            client = new HazelcastImpl.Client();
            return this;
        }
        public Builder useSocket() {
            client = new SocketImpl.Client();
            return this;
        }


        public RemoteEventBusClient build() {
            Preconditions.checkNotNull(groupName, "The group name is required.");
            Preconditions.checkNotNull(host, "The host is required.");
            Preconditions.checkNotNull(port, "The port is required.");
            Preconditions.checkNotNull(client, "You must specify an implementation.");

            client.setGroupName(groupName);
            client.setHost(host);
            client.setPort(port);
            return client;
        }
    }
}
