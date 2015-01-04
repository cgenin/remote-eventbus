package net.genin.remote.eventbus;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;

/**
 * EventBus Implementation.
 */
abstract class EventBusServer implements RemoteEventBusServer {

    public final EventBus eventBus;
    protected String groupName;
    protected Integer port;

    public EventBusServer() {
        eventBus = new EventBus();
    }

    protected void sendToBus(Object o){
        eventBus.post(o);
    }

    @Override
    public void register(Object object){
        Preconditions.checkNotNull(object);
        eventBus.register(object);
    }

    @Override
    public void unregister(Object object){
        Preconditions.checkNotNull(object);
        eventBus.unregister(object);
    }

    @Override
    public String getGroupName() {
        return groupName;
    }

    @Override
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public Integer getPort() {
        return port;
    }
}
