package com.sharethis.loopy.sdk;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Jason Polites
 */
public class Event {

    private String type;
    private Map<String, String> meta;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
    }

    public synchronized void addMeta(String key, String value) {
        if(meta == null) {
            meta = new LinkedHashMap<String, String>();
        }
        meta.put(key, value);
    }

    @Override
    public String toString() {
        return "Event{" +
                "type='" + type + '\'' +
                ", meta=" + meta +
                '}';
    }
}
