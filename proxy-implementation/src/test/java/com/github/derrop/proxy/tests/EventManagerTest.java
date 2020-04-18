package com.github.derrop.proxy.tests;

import com.github.derrop.proxy.api.event.Event;
import com.github.derrop.proxy.api.event.EventManager;
import com.github.derrop.proxy.api.event.EventPriority;
import com.github.derrop.proxy.api.event.annotation.Listener;
import com.github.derrop.proxy.event.DefaultEventManager;
import org.junit.Assert;
import org.junit.Test;

public class EventManagerTest {

    private final EventManager eventManager = new DefaultEventManager();

    @Test
    public void testEventManager() {
        AnyListener listener = new AnyListener();
        this.eventManager.registerListener(null, listener);

        this.eventManager.callEvent(new AnyEvent());
    }

    public static class AnyListener {

        @Listener(priority = EventPriority.LAST)
        public void handleA(AnyEvent event) {
            System.out.println("3");
            Assert.assertEquals(1, event.i);
        }

        @Listener(priority = EventPriority.FIRST)
        public void handleZ(AnyEvent event) {
            Assert.assertEquals(0, event.i);
            event.i++;

            System.out.println("1");
        }

        @Listener(priority = EventPriority.NORMAL)
        public void handleB(AnyEvent event) {
            System.out.println("2");
            Assert.assertEquals(1, event.i);
        }
    }

    public static class AnyEvent extends Event {

        public int i = 0;

    }
}
