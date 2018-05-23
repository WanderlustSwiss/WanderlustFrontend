package eu.wise_iot.wanderlust.services;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

public class DownloadQueueHandler {
    private static DownloadQueueHandler downloadQueueHandler;
    private static ScheduledExecutorService executorService;

    private DownloadQueueHandler(){
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

    public static synchronized DownloadQueueHandler getInstance(){
        if (downloadQueueHandler == null)
            downloadQueueHandler = new DownloadQueueHandler();
        return downloadQueueHandler;
    }

    public synchronized Future queueTask(Runnable r){
        return executorService.submit(r);
    }
}