package com.easy.nettyPush;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapTest {

	public static void main(String[] args) {
		UUID sessionId = UUID.randomUUID();
		ConcurrentHashMap<UUID,String> map = new ConcurrentHashMap<UUID,String>();
		map.put(sessionId, "aa");
		System.out.println(map.containsKey(sessionId));
	}
}
