
package com.easy.nettyPush.scheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.easy.nettyPush.common.Configuration;

public class CancelableScheduler {

    private final static Map<SchedulerKey, Future<?>> scheduledFutures = new ConcurrentHashMap<SchedulerKey, Future<?>>();
    private final static ScheduledExecutorService executorService =Executors.newScheduledThreadPool(Configuration.HEARTBEAT_THREADPOOL_SIZE);


    public static void cancel(SchedulerKey key) {
        Future<?> future = scheduledFutures.remove(key);
        if (future != null) {
            future.cancel(false);
        }
    }

    public static void schedule(final SchedulerKey key, final Runnable runnable, long delay, TimeUnit unit) {
        Future<?> future = executorService.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {
                	scheduledFutures.remove(key);
                }
            }
        }, delay, unit);
        scheduledFutures.put(key, future);
    }
}
