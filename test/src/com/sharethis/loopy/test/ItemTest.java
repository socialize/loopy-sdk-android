package com.sharethis.loopy.test;

import com.sharethis.loopy.sdk.Item;

/**
 * @author Jason Polites
 */
public class ItemTest extends LoopyActivityTestCase {


    public void testEquals() {

        Item item0 = new Item();
        item0.setTitle("a");
        item0.setUrl("b");

        Item item1 = new Item();
        item1.setUrl("a");

        Item item2 = new Item();
        item2.setTitle("a");
        item2.setUrl("b");

        Item item3 = new Item();
        item3.setTitle("c");

        Item item4 = new Item();
        item4.setTitle("c");

        assertFalse(item0.equals(item1));
        assertTrue(item0.equals(item2));
        assertFalse(item0.equals(item3));
        assertFalse(item0.equals(item4));
        assertTrue(item3.equals(item4));
    }
}
