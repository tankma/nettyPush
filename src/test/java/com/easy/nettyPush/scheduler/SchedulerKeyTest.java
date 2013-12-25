package com.easy.nettyPush.scheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.junit.Test;

import com.easy.nettyPush.scheduler.SchedulerKey.Type;


public class SchedulerKeyTest {

	@Test
	public void testEqual(){
		SchedulerKey s1 = new SchedulerKey(Type.CLOSE_TIMEOUT,"a");
		SchedulerKey s2 = new SchedulerKey(Type.CLOSE_TIMEOUT,"a1");
		Map<SchedulerKey, String> scheduledFutures = new ConcurrentHashMap<SchedulerKey, String>();
		scheduledFutures.put(s1, "value1");
		scheduledFutures.put(s2, "value2");
		System.out.println(scheduledFutures.size());
		System.out.println(s1==s2);
		System.out.println(s1.hashCode()+"==="+s2.hashCode());
		System.out.println(s1.equals(s2));
	}
}
