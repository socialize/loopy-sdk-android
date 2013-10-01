package com.sharethis.loopy.test.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

import static junit.framework.Assert.*;

/**
 * @author Jason Polites
 */
public final class JsonAssert {

    private JsonAssert() {}

    public static void assertJsonEquals(String name, JSONArray expected, JSONArray actual) throws Exception {
        if (expected.length() != actual.length()) {
            assertEquals("Arrays are not of equal length", expected.toString(), actual.toString());
        }

        for (int i = 0; i < expected.length(); ++i) {
            Object expectedValue = expected.opt(i);
            Object actualValue = actual.opt(i);
            assertValueEquals(name, expectedValue, actualValue);
        }
    }

    public static void assertJsonEquals(JSONObject expected, JSONObject actual) throws Exception {
        if (expected.length() != actual.length()) {

            StringBuilder builder = new StringBuilder();
            Iterator expectedKeys = expected.keys();
            Iterator actualKeys = actual.keys();

            builder.append("\n\nExpected has: \n\n");

            while (expectedKeys.hasNext()) {
                builder.append(expectedKeys.next().toString());
                builder.append("\n");
            }

            builder.append("\nActual has: \n\n");

            while (actualKeys.hasNext()) {
                builder.append(actualKeys.next().toString());
                builder.append("\n");
            }

            fail("Objects are not of equal size." + builder.toString());
        }

        // Both are empty so skip
        if (expected.names() == null && actual.names() == null) {
            return;
        }

        JSONArray names = expected.names();
        List<String> nameList = new ArrayList<String>(names.length());

        for (int i = 0; i < names.length(); i++) {
            nameList.add(names.getString(i));
        }

        for (String name : nameList) {
            Object expectedValue = expected.opt(name);
            Object actualValue = actual.opt(name);
            assertValueEquals(name, expectedValue, actualValue);
        }
    }

    static void assertValueEquals(String name, Object expectedValue, Object actualValue) throws Exception {
        if (expectedValue != null) {
            assertNotNull("Value for field " + name + " was null but should not have been", actualValue);

            if (expectedValue instanceof JSONObject) {
                assertJsonEquals((JSONObject) expectedValue, (JSONObject) actualValue);
            }
            else if (expectedValue instanceof JSONArray) {
                assertJsonEquals(name, (JSONArray) expectedValue, (JSONArray) actualValue);
            }
            else {
                if(Number.class.isAssignableFrom(expectedValue.getClass())) {
                    assertTrue("The actual class of " + name + " (" + actualValue.getClass() + ") is not is not a (" + Number.class + ")", Number.class.isAssignableFrom(actualValue.getClass()));
                }
                else {
                    assertTrue("The actual class of " + name + " (" + actualValue.getClass() + ") is not assignable from (" + expectedValue.getClass() + ")", actualValue.getClass().isAssignableFrom(expectedValue.getClass()));
                }

                assertEquals(("Value for " + name + " was " + actualValue + " (" + actualValue.getClass() + ") but should have been " + expectedValue + " (" + expectedValue.getClass() + ")"), expectedValue.toString(), actualValue.toString());
            }
        }
        else {
            assertNull("Actual value (" + actualValue + ") was not expected.  Expected value for " + name + " is null", actualValue);
        }
    }

    public static void assertHasValueAtLocation(JSONObject obj, String path) {
        String[] segments = path.split("/");
        Stack<String> stack = new Stack<String>();
        List<String> tmp = new ArrayList<String>(Arrays.asList(segments));
        Collections.reverse(tmp);
        stack.addAll(tmp);

        while(!stack.isEmpty()) {
            String pop = stack.pop();
            assertTrue("Object does not contain key " + pop, obj.has(pop));
            if(!stack.isEmpty()) {
                try {
                    obj = obj.getJSONObject(pop);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    fail(e.getMessage());
                }
            }
        }
    }
}
