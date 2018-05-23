package eu.wise_iot.wanderlust.services;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

public class AsyncDownloadQueueTask {
    private static final String TAG = "AsyncUITask";
    private static AsyncDownloadQueueTask asyncDownloadQueueTask;
    private static ScheduledExecutorService executorService;

    private AsyncDownloadQueueTask(){
        executorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();

            @Override
            public Thread newThread(Runnable r) {
                Thread t = defaultThreadFactory.newThread(r);
                t.setPriority(Thread.MIN_PRIORITY);
                return t;
            }
        });

    }

    public static synchronized AsyncDownloadQueueTask getHandler(){
        if (asyncDownloadQueueTask == null)
            asyncDownloadQueueTask = new AsyncDownloadQueueTask();
        return asyncDownloadQueueTask;
    }

    public synchronized Future queueTask(Runnable r){
        return executorService.submit(r);
    }
}