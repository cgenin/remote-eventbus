package net.genin.remote.eventbus;

import com.google.gson.Gson;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Created by skarb on 04/01/2015.
 */
public class GsonTest {

    public static class Result {
        boolean test;
    }

    @Test
    public void test() throws Exception {
        final String text = "{\"className\":\"net.genin.remote.eventbus.GsonTest$Result\",\"object\":\"{\\\"test\\\":true}\"}";
        final Result msg = SocketImpl.JsonObj.deserialize(text);
        assertNotNull(msg);
        assertTrue(msg.test);
    }

    @Test
    public void serialize() throws Exception {
        final Result result = new Result();
        result.test = true;
        assertEquals("{\"className\":\"net.genin.remote.eventbus.GsonTest$Result\",\"object\":\"{\\\"test\\\":true}\"}",
                SocketImpl.JsonObj.serialize(result));
    }
}
