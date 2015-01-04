package net.genin.remote.eventbus;

import com.google.common.base.Preconditions;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.*;

/**
 * Hazelcast Implementation.
 */
final class HazelcastImpl {
    public static final String TOPIC_NAME = "RemoteEventBus";

    static class Client extends EventBusClient {


        public Client() {
        }

        @Override
        public void post(final Object msg) {
            Preconditions.checkNotNull(msg);
            final ClientConfig clientConfig = new ClientConfig();
            clientConfig.getGroupConfig().setName(groupName);

            final String adress = host + ":" + port;
            clientConfig.getNetworkConfig().addAddress(adress);

            final HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);

            final ITopic<Object> topic = client.getTopic(TOPIC_NAME);
            topic.publish(msg);
        }
    }

    static class Server extends EventBusServer implements MembershipListener, MessageListener<Object> {


        private HazelcastInstance hazelcast;
        private String nodeID;
        private boolean active = false;
        private String membershipListenerId;

        public Server() {
        }

        @Override
        public synchronized boolean start() {
            if (active)
                return true;

            Preconditions.checkNotNull(getGroupName());

            final Config config = new Config();
            config.getGroupConfig().setName(getGroupName());
            hazelcast = Hazelcast.newHazelcastInstance(config);
            nodeID = hazelcast.getCluster().getLocalMember().getUuid();
            membershipListenerId = hazelcast.getCluster().addMembershipListener(this);
            active = hazelcast.getLifecycleService().isRunning();


            if (active) {
                port = hazelcast.getCluster().getLocalMember().getSocketAddress().getPort();
                final ITopic<Object> topic = hazelcast.getTopic(TOPIC_NAME);
                topic.addMessageListener(this);
            }
            return active;
        }


        public String getNodeID() {
            return nodeID;
        }

        @Override
        public synchronized void stop() {
            if (!active) return;
            hazelcast.getCluster().removeMembershipListener(membershipListenerId);
            hazelcast.getLifecycleService().shutdown();
            active = false;
        }


        @Override
        public void onMessage(Message<Object> message) {
            final Object messageObject = message.getMessageObject();
            sendToBus(messageObject);
        }

        @Override
        public void memberAdded(MembershipEvent membershipEvent) {

        }

        @Override
        public void memberRemoved(MembershipEvent membershipEvent) {

        }

        @Override
        public void memberAttributeChanged(MemberAttributeEvent memberAttributeEvent) {

        }

    }
}
