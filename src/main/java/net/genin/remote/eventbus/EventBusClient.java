package net.genin.remote.eventbus;

/**
 * Base class for client.
 */
abstract class EventBusClient implements RemoteEventBusClient {
    protected Integer port;
    protected String groupName;
    protected String host ;

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
