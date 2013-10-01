package com.sharethis.loopy.test.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jason Polites
 */
public class Holder<T> {

	private T object;

    private List<T> objects = new ArrayList<T>();

	public Holder() {}

	public Holder(T object) {
		super();
		this.object = object;
	}

	public T get() {
		return object;
	}

	public void set(T object) {
		this.object = object;
	}

    public void add(T object) {
        objects.add(object);
    }

    public List<T> getObjects() {
        return objects;
    }
}
