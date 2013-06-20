package com.adobe.aem.init.dogmatix.util;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * An abstract object pool.
 * 
 * Reusable pool of objects of type T. New objects are created on demand and if
 * all objects in the pool are currently in use. Objects lying idle in the pool
 * are subject to expire and hence can be garbage collected to save memory.
 * 
 * @author http://sourcemaking.com/design_patterns/object_pool/java
 * 
 * @param <T>
 * 
 *            Generic parameter of the type of objects in the pool
 */

public abstract class ObjectPool<T> {
	private long expirationTime;

	private Hashtable<T, Long> locked, unlocked;

	public ObjectPool() {
		expirationTime = 30000; // 30 seconds
		locked = new Hashtable<T, Long>();
		unlocked = new Hashtable<T, Long>();
	}

	protected abstract T create() throws Exception;

	public abstract boolean validate(T o);

	public abstract void expire(T o);

	public synchronized T checkOut() throws Exception {
		long now = System.currentTimeMillis();
		T t;
		if (unlocked.size() > 0) {
			Enumeration<T> e = unlocked.keys();
			while (e.hasMoreElements()) {
				t = e.nextElement();
				if ((now - unlocked.get(t)) > expirationTime) {
					// object has expired
					unlocked.remove(t);
					expire(t);
					t = null;
				} else {
					if (validate(t)) {
						unlocked.remove(t);
						locked.put(t, now);
						return (t);
					} else {
						// object failed validation
						unlocked.remove(t);
						expire(t);
						t = null;
					}
				}
			}
		}
		// no objects available, create a new one
		t = create();
		locked.put(t, now);
		return (t);
	}

	public synchronized void checkIn(T t) {
		locked.remove(t);
		unlocked.put(t, System.currentTimeMillis());
	}
}