package org.norc.rsub;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorkerPool {
  public static final Logger logger = LogManager.getLogger(WorkerPool.class);

  /**
   * Run 10 jobs with two concurrent threads.  The queue size is set
   * intentionally low to demonstrate the rejectedHandler.
   *
   * @param args no arguments are processed by this class.
   */
  public static void main(String[] args) {
    ThreadFactory threadFactory = Executors.defaultThreadFactory();
    ThreadPoolExecutor executorPool =
        new ThreadPoolExecutor(
            2,
            4,
            10,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(5),
            Executors.defaultThreadFactory(),
            (r, executor) -> {
              logger.error("{} was rejected.", r);
            });
    MonitorThread monitor = new MonitorThread(executorPool, 3);
    Thread monitorThread = new Thread(monitor);
    monitorThread.start();

    Random rn = new Random();
    for (int i = 0; i < 10; i++) {
      executorPool.execute(new WorkerThread(rn.nextInt(10) + 1));
    }

    try {
    Thread.sleep(30000);
    executorPool.shutdown();
    Thread.sleep(5000);
    monitor.shutdown();
    } catch (InterruptedException err) {
      logger.error(err);
    }
  }
}
