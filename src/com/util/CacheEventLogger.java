package com.util;

import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;

public class CacheEventLogger implements CacheEventListener<Object, Object> {

	@Override
	/**
	 *
	You can register a callable method that will be executed when a particular cache event is performed. It is something similar to triggers in the database.
	Ehcache allows below event for registration:
		·    CREATED: This even is fired when a new put is performed.
		·    UPDATED: When an element is updated, i.e. the value of the corresponding to a key is updated.
		·    REMOVED: When an element is removed from the cache.
		·    EXPIRED: When an element gets expired. You can configure the expiration either by defining timeToLive(TTL) or by timeToIdle (TTI).
		·    Time to Live: The max time for which an element can exist in the cache regardless of used or not and will no longer be returned from the cache. The default value is 0, which means no TTL eviction takes place (infinite lifetime).
		·    Time to Idle: The max time for which an element can exist in the cache without being accessed. The element expires at this limit and will no longer be returned from the cache. The default value is 0, which means no TTI eviction takes place (infinite lifetime).
		·    EVICTED: When the cash is full, and you try to add some more element which results to eviction of cache elements and the event is fired

    */
	public void onEvent(CacheEvent<? extends Object, ? extends Object> event) {
		// TODO Auto-generated method stub

		// System.out.println("Event fired: " + event.getType());
	}

}
