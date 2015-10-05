package com.bookmate.libs.traits;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

public class BusTest {

    private Bus bus;
    private int event1GotParam;
    private int event2GotParam;

    @Before
    public void setUp() throws Exception {
        bus = new Bus();
        event1GotParam = event2GotParam = -1;
    }

    @Test
    public void testEventListener() {
        bus.register(TestEvent.class, new Bus.EventListener<TestEvent>() {
            @Override
            public void process(TestEvent event) {
                event1GotParam = event.param;
            }
        });
        bus.register(TestEvent.class, new Bus.EventListener<TestEvent>() {
            @Override
            public void process(TestEvent event) {
                event2GotParam = event.param;
            }
        });
        final int eventParam = 5;
        bus.event(new TestEvent(eventParam));
        assertEquals(eventParam, event1GotParam);
        assertEquals(eventParam, event2GotParam);
    }

    @Test
    public void testUnregisterEventListener() {
        final Bus.EventListener<TestEvent> testEventEventListener = new Bus.EventListener<TestEvent>() {
            @Override
            public void process(TestEvent event) {
                event1GotParam = event.param;
            }
        };
        bus.register(TestEvent.class, testEventEventListener);
        bus.register(TestEvent.class, new Bus.EventListener<TestEvent>() {
            @Override
            public void process(TestEvent event) {
                event2GotParam = event.param;
            }
        });
        bus.unregister(TestEvent.class, testEventEventListener);

        final int eventParam = 5;
        bus.event(new TestEvent(eventParam));

        assertNotEquals(eventParam, event1GotParam);
        assertEquals(eventParam, event2GotParam);
    }

    @Test
    public void testRequestReturnsDefaultResult() {
        assertEquals(bus.requestData(new TestRequestDefaultResult()), TestRequestDefaultResult.RESULT);
    }

    @Test
    public void testRequestReturnsNull() {
        assertNull(bus.requestData(new TestRequest(0)));
    }

    @Test
    public void testRequestGotCorrectResult() {
        bus.register(TestRequest.class, new Bus.DataRequestListener<Integer, TestRequest>() {
            @Override
            public Integer process(TestRequest request) {
                return request.param;
            }
        });
        final Integer param = 5;
        assertEquals(bus.requestData(new TestRequest(param)), param);
    }

    @Test
    public void testUnregisterRequestListener() {
        bus.register(TestRequest.class, new Bus.DataRequestListener<Integer, TestRequest>() {
            @Override
            public Integer process(TestRequest request) {
                return request.param;
            }
        });
        bus.unregister(TestRequest.class);

        final Integer param = 5;
        assertNotEquals(bus.requestData(new TestRequest(param)), param);
    }

    private static class TestRequestDefaultResult extends Bus.DataRequest<Integer> {

        public static final Integer RESULT = 0;

        @Override
        protected Integer defaultResult() {
            return RESULT;
        }
    }

    private static class TestRequest extends Bus.DataRequest<Integer> {
        private final int param;

        private TestRequest(int param) {
            this.param = param;
        }
    }

    private static class TestEvent {
        public final int param;

        private TestEvent(int param) {
            this.param = param;
        }
    }
}