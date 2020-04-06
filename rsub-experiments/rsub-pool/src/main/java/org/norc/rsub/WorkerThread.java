package org.norc.rsub;

public class WorkerThread implements Runnable {
  private int seconds;

  public WorkerThread(int seconds) {
    this.seconds = seconds;
  }

  @Override
  public void run() {
    WorkerPool.logger.info("{} Start.", Thread.currentThread().getName());
    processCommand();
    WorkerPool.logger.info("{} End.", Thread.currentThread().getName());
  }

  private void processCommand() {
    try {
      IOMAdapter adapter = new IOMAdapter(new ZeroConfigurator());
      adapter.connect();
      adapter.submit(String.format("data _null_; rc=sleep(%d,1); run;", seconds));
      do {
        Thread.sleep(100);
      } while (!adapter.complete);
      adapter.close();
    } catch (Exception err) {
      WorkerPool.logger.error(err);
    }

  }
}
