package eu.wise_iot.wanderlust.services;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * this class provides a thread pool for high priority
 * heavy load tasks on the UI if needed
 *
 * @author Alexander Weinbeck
 * @license MIT
 */
public class AsyncUITask {
    private static final String TAG = "AsyncUITask";
    private static AsyncUITask uiQueueHandler;
    private static ScheduledExecutorService executorService;
    private final static int POOL_SIZE = 5;

    private AsyncUITask(){
        executorService = Executors.newScheduledThreadPool(POOL_SIZE, new ThreadFactory() {
            final ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();
            @Override
            public Thread newThread(Runnable r) {
                Thread t = defaultThreadFactory.newThread(r);
                t.setPriority(Thread.MAX_PRIORITY);
                return t;
            }
        });
    }
    public static synchronized AsyncUITask getHandler(){
        if (uiQueueHandler == null)
            uiQueueHandler = new AsyncUITask();
        return uiQueueHandler;
    }

    public synchronized Future queueTask(Runnable r){
        return executorService.submit(r);
    }
}