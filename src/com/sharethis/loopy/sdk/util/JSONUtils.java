package com.sharethis.loopy.sdk.util;

import com.sharethis.loopy.sdk.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Map;

/**
 * @author Jason Polites
 */
public class JSONUtils {

    public static boolean put(JSONObject object, String key, Object value) throws JSONException {
        return put(object, key, value, null);
    }

    public static boolean put(JSONObject object, String key, Object value, Object defaultValue) throws JSONException {
        if(value != null) {

            if(value instanceof Map) {
                JSONObject mapObject = new JSONObject();
                Map<?,?> map = (Map<?,?>) value;
                for (Object o : map.keySet()) {
                    put(mapObject, o.toString(), map.get(o));
                }

                object.put(key, mapObject);
            }
            else if(value instanceof Collection) {
                JSONArray arrayObject = new JSONArray();
                Collection<?> collection = (Collection<?>) value;
                for (Object o : collection) {
                    // TODO: This is not very robust.  Needs to infer the type
                    arrayObject.put(o.toString());
                }
                object.put(key, arrayObject);
            }
            else {
                object.put(key, value);
            }

            return true;
        }
        else if(defaultValue != null) {
            object.put(key, defaultValue);
            return true;
        }
        return false;
    }

    public static boolean isNull(JSONObject object, String key) {
        return !object.has(key) || object.isNull(key);
    }

    public static String getString(JSONObject object, String key) {
        if(!isNull(object, key)) {
            try {
                return object.getString(key);
            } catch (JSONException e) {
                Logger.e(e);
            }
        }
        return null;
    }
}
