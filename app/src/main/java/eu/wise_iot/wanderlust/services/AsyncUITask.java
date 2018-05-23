package eu.wise_iot.wanderlust.services;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

public class AsyncUITask {
    private static final String TAG = "AsyncUITask";
    private static AsyncUITask uiQueueHandler;
    private static ScheduledExecutorService executorService;

    private AsyncUITask(){
        executorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();
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