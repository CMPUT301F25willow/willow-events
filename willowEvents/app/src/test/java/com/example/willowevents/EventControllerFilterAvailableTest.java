package com.example.willowevents;


import static org.junit.Assert.*;

import com.example.willowevents.model.Event;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Unit tests for EventController.filterAvailable(...) */
public class EventControllerFilterAvailableTest {

    private EventController controller;

    @Before
    public void setUp() {
        controller = new EventController();
    }

    @Test
    public void include_whenCapacityNull_unlimited() throws Exception {
        Event e = makeEvent("A", null, 5);
        List<Event> out = controller.filterAvailable(Collections.singletonList(e));
        assertEquals(1, out.size());
        assertEquals("A", out.get(0).getTitle());
    }

    @Test
    public void ignore_whenCapacityZero() throws Exception {
        Event e = makeEvent("B", 0, 0);
        List<Event> out = controller.filterAvailable(Collections.singletonList(e));
        assertTrue(out.isEmpty());
    }

    @Test
    public void include_whenInviteCountLessThanCapacity() throws Exception {
        Event e = makeEvent("C", 10, 3);
        List<Event> out = controller.filterAvailable(Collections.singletonList(e));
        assertEquals(1, out.size());
        assertEquals("C", out.get(0).getTitle());
    }

    @Test
    public void include_whenInviteCountEqualsCapacity() throws Exception {
        Event e = makeEvent("D", 3, 3);
        List<Event> out = controller.filterAvailable(Collections.singletonList(e));
        assertEquals(1, out.size());
        assertEquals("D", out.get(0).getTitle());
    }

    @Test
    public void exclude_whenInviteCountExceedsCapacity() throws Exception {
        Event e = makeEvent("E", 2, 3);
        List<Event> out = controller.filterAvailable(Collections.singletonList(e));
        assertTrue(out.isEmpty());
    }

    @Test
    public void works_withMultipleEvents_mixed() throws Exception {
        Event e1 = makeEvent("U", null, 0);  // unlimited => include
        Event e2 = makeEvent("Z", 0, 0);     // cap==0 => ignore
        Event e3 = makeEvent("L", 5, 5);     // equal => include
        Event e4 = makeEvent("X", 2, 3);     // exceed => exclude
        Event e5 = makeEvent("Y", 2, 1);     // less => include

        List<Event> input = Arrays.asList(e1, e2, e3, e4, e5);
        List<Event> out = controller.filterAvailable(input);

        List<String> titles = new ArrayList<>();
        for (Event e : out) titles.add(e.getTitle());
        assertEquals(Arrays.asList("U", "L", "Y"), titles);
    }

    // --- helpers ---

    private Event makeEvent(String title, Integer capacity, int inviteCount) throws Exception {
        Event e = new Event();
        setField(e, "title", title);
        setField(e, "capacity", capacity); // Event has private Integer capacity; getter is getInvitelistlimit()
        setField(e, "id", "id_" + title);

        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < inviteCount; i++) list.add("u" + i);
        setField(e, "inviteList", list);

        return e;
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }
}

