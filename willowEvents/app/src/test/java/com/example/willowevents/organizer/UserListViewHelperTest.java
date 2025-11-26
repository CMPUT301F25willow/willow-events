package com.example.willowevents.organizer;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for small helper methods in UserListView:
 * - mapTypeToField: maps the "Type" string (waitlist/invited/...) to the Firestore field name.
 * - chunk: splits a list of IDs into batches for Firestore whereIn() calls. whereIn() can only take a max of 10 at a time
 * - safe: converts null strings to empty strings to avoid Null Pointer Exceptions in the UI.
 */

public class UserListViewHelperTest {

    // Given list type "waitlist", we get the firestore field "waitlist" when it is mapped
    @Test
    public void mapTypeToField_waitlist() {
        assertEquals("waitlist", UserListView.mapTypeToField("waitlist"));
    }

    // Same thing for type "invited"
    @Test
    public void mapTypeToField_invited() {
        assertEquals("inviteList", UserListView.mapTypeToField("invited"));
    }
    // Same thing for type "enrolled"
    @Test
    public void mapTypeToField_enrolled() {
        assertEquals("approvedList", UserListView.mapTypeToField("enrolled"));
    }
    //Same thing for type "cancelled"
    @Test
    public void mapTypeToField_cancelled() {
        assertEquals("cancelledList", UserListView.mapTypeToField("cancelled"));
    }
    // Unknown types(Strings) should not map to any field -> null
    @Test
    public void mapTypeToField_unknown_returnsNull() {
        assertNull(UserListView.mapTypeToField("something-weird"));
        assertNull(UserListView.mapTypeToField(null));
    }

    @Test
    public void chunk_splitsExactly_whenMultipleOfSize() {
        //Given 4 elements
        List<String> src = Arrays.asList("a", "b", "c", "d");
        //And chunk size of 2
        List<List<String>> chunks = UserListView.chunk(src, 2);
        //We should expect 2 chunks of size 2
        assertEquals(2, chunks.size());
        assertEquals(Arrays.asList("a", "b"), chunks.get(0));
        assertEquals(Arrays.asList("c", "d"), chunks.get(1));
    }

    @Test
    public void chunk_handlesRemainder_lastChunkShorter() {
        //Given 5 elements and chunk size = 2
        List<String> src = Arrays.asList("a", "b", "c", "d", "e");
        List<List<String>> chunks = UserListView.chunk(src, 2);
        // We expect 3 chunks like this
        assertEquals(3, chunks.size());
        assertEquals(Arrays.asList("a", "b"), chunks.get(0));
        assertEquals(Arrays.asList("c", "d"), chunks.get(1));
        assertEquals(Arrays.asList("e"), chunks.get(2));
    }

    @Test
    public void chunk_singleElement() {
        //Given one element and a large chunk size (10 is the maximum for whereIn() for firestore)
        List<String> src = Arrays.asList("only");
        List<List<String>> chunks = UserListView.chunk(src, 10);
        //We expect one chunk with that one element
        assertEquals(1, chunks.size());
        assertEquals(Arrays.asList("only"), chunks.get(0));
    }

    @Test
    public void chunk_emptyList() {
        //Given no elements
        List<String> src = Arrays.asList();
        List<List<String>> chunks = UserListView.chunk(src, 10);
        //We expect an empty list of chunks
        assertTrue(chunks.isEmpty());
    }

    @Test
    public void safe_returnsEmptyStringForNull() {
        //Given a null string we expect safe to return an empty string
        assertEquals("", UserListView.safe(null));
    }

    @Test
    public void safe_returnsSameStringWhenNotNull() {
        //Given a string (non-null), we expect the same string back
        assertEquals("hello", UserListView.safe("hello"));
    }
}