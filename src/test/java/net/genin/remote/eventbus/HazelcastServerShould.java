package net.genin.remote.eventbus;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test of HazelcastServer.
 */
public class HazelcastServerShould {

    @Test
    public void start() {
        final HazelcastImpl.Server hazelcastServer = new HazelcastImpl.Server();
        hazelcastServer.setGroupName("TEST");
        assertTrue(hazelcastServer.start());
        assertNotNull(hazelcastServer.getNodeID());
        assertNotNull(hazelcastServer.getPort());
        hazelcastServer.stop();
    }

    @Test
    public void mustBeNotRestart() {
        final HazelcastImpl.Server hazelcastServer = new HazelcastImpl.Server();
        hazelcastServer.setGroupName("TEST");
        assertTrue(hazelcastServer.start());
        final String nodeID = hazelcastServer.getNodeID();
        final Integer port = hazelcastServer.getPort();

        assertTrue(hazelcastServer.start());
        assertEquals(nodeID, hazelcastServer.getNodeID());
        assertEquals(port, hazelcastServer.getPort());

        hazelcastServer.stop();
    }

    @Test
    public void mustChangeNodeId() {
        final HazelcastImpl.Server hazelcastServer = new HazelcastImpl.Server();
        hazelcastServer.setGroupName("TEST");
        assertTrue(hazelcastServer.start());
        final String nodeID = hazelcastServer.getNodeID();
        hazelcastServer.stop();
        assertTrue(hazelcastServer.start());
        assertFalse(nodeID.equals(hazelcastServer.getNodeID()));

        hazelcastServer.stop();
    }

    @Test(expected = NullPointerException.class)
    public void notStartWithoutGroup() {
        final HazelcastImpl.Server hazelcastServer = new HazelcastImpl.Server();
        hazelcastServer.start();
    }
}
