package eu.wise_iot.wanderlust.services;

import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import eu.wise_iot.wanderlust.BuildConfig;

/**
 * Provides a thread queue for low priority
 * load tasks in the background
 * for example downloads
 *
 * @author Alexander Weinbeck
 * @license GPL-3.0
 */
public class AsyncDownloadQueueTask {
    private static final String TAG = "AsyncUITask";
    private static AsyncDownloadQueueTask asyncDownloadQueueTask;
    private static ScheduledExecutorService executorService;
    private static int threadCount = 0;

    private AsyncDownloadQueueTask(){
        executorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            final ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();

            @Override
            public Thread newThread(Runnable r) {
                if (BuildConfig.DEBUG) Log.d(TAG, "creating new Thread");
                threadCount++;
                Thread t = defaultThreadFactory.newThread(r);
                t.setPriority(Thread.MIN_PRIORITY);
                t.setDaemon(true);
                return t;
            }
        });

    }
    private static int getThreadCount(){
       return threadCount;
    }
    public static synchronized AsyncDownloadQueueTask getHandler(){
        if (asyncDownloadQueueTask == null)
            asyncDownloadQueueTask = new AsyncDownloadQueueTask();
        if (BuildConfig.DEBUG) Log.d(TAG, "Getting handler " + "active threads: "+ getThreadCount() + " Application Wide: " + Thread.activeCount());
        return asyncDownloadQueueTask;
    }

    public synchronized Future queueTask(Callable r){
        return executorService.submit(r);
    }
    public synchronized Future queueTask(Runnable r){
        return executorService.submit(r);
    }
}
