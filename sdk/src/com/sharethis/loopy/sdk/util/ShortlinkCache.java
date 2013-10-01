package com.sharethis.loopy.sdk.util;

import com.sharethis.loopy.sdk.Item;

import java.util.HashMap;

/**
 * @author Jason Polites
 */
public class ShortlinkCache {

    private final HashMap<Item, String> links = new HashMap<Item, String>();
    private final HashMap<String, Item> backLinks = new HashMap<String, Item>();

    public String getShortlink(Item item) {
        return links.get(item);
    }

    public void remove(String shortlink) {
        Item item = backLinks.get(shortlink);

        if(item != null) {
            links.remove(item);
        }

        backLinks.remove(shortlink);
    }

    public void add(String shortlink, Item item) {
        links.put(item, shortlink);
        backLinks.put(shortlink, item);
    }

    public void clear() {
        links.clear();
        backLinks.clear();
    }
}
