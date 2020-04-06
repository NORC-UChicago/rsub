package org.norc.rsub;

import java.util.concurrent.ThreadPoolExecutor;

public class MonitorThread implements Runnable {
  private ThreadPoolExecutor executor;
  private int delay;

  private boolean active = true;

  public MonitorThread(ThreadPoolExecutor executor, int delay) {
    this.executor = executor;
    this.delay = delay;
  }

  public void shutdown() {
    this.active = false;
  }

  @Override
  public void run() {
    while(active) {
      WorkerPool.logger.info("[monitor] [{}/{}] Active: {}, Complete {}, Task: {}, isShutdown {}, isTerminated: {}",
          executor.getPoolSize(),
          executor.getCorePoolSize(),
          executor.getActiveCount(),
          executor.getCompletedTaskCount(),
          executor.getTaskCount(),
          executor.isShutdown(),
          executor.isTerminated());
      try {
        Thread.sleep(delay * 1000);
      } catch (InterruptedException err) {
        WorkerPool.logger.error(err);
      }
    }
  }
}
