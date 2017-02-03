package stud.opencv.server.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Light on 03.02.2017.
 */
public class Scheduler {

    private static final List<ExecutorService> executors = new ArrayList<>();

    public static void runWithDelay(Runnable r, long delay, TimeUnit unit) {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleWithFixedDelay(r, 0, delay, unit);
        executors.add(executorService);
    }

    public static void stop() {
        for (ExecutorService executor : executors) {
            executor.shutdown();
        }
    }

}
