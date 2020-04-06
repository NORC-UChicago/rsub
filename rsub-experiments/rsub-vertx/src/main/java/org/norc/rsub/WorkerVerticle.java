package org.norc.rsub;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import java.util.Random;

public class WorkerVerticle extends AbstractVerticle {
  private final Random rn = new Random();

  @Override
  public void start() throws Exception {
    vertx.eventBus().consumer("process.coffee.order", this::makeCoffee);
  }

  private void makeCoffee(Message<JsonObject> message) {
    JsonObject body = message.body();
    MainVerticle.logger.info("Processing coffee order {} for {}: {}, {}. instance: {}",
        body.getString("id"),
        body.getString("customer"),
        body.getString("coffee"),
        body.getString("size"),
        this);

    int seconds = (rn.nextInt(10) + 1);

    String status = null;
    try {
      ZeroConfigurator zero = new ZeroConfigurator();
      IOMAdapter adapter = new IOMAdapter(
          zero,
          new EventBusDestination(vertx.eventBus(), "service.ui-log"),
          new EventBusDestination(vertx.eventBus(), "service.ui-lst")
      );
      adapter.connect();
      StringBuilder sb = new StringBuilder();
      sb.append("data order;\n");
      sb.append(String.format("  id=\"%s\";\n", body.getString("id")));
      sb.append(String.format("  customer=\"%s\";\n", body.getString("customer")));
      sb.append(String.format("  coffee=\"%s\";\n", body.getString("coffee")));
      sb.append(String.format("  size=\"%s\";\n", body.getString("size")));
      sb.append(String.format("  rc=sleep(%d);\n", seconds));
      sb.append("proc print;\n");
      sb.append("run;\n");
      MainVerticle.logger.debug("Submitting program: {}", sb.toString());
      adapter.submit(sb.toString());
      while(!adapter.complete) {
        Thread.sleep(500);
      }
      adapter.close();
      MainVerticle.logger.info("Order {} complete in {} sec!  Calling {}, instance: {}",
          body.getString("id"),
          seconds,
          body.getString("customer"),
          this);
      status = "ready";
    } catch (Exception err) {
      MainVerticle.logger.error("Order {} failed! instance: {}",
          body.getString("id"),
          this,
          err);
      vertx.eventBus().send("complete.coffee.order",
          Json.encode(new JsonObject()
              .put("order", "failed")
              .put("id", body.getString("id"))
          )
      );
      status = "dropped on the floor";
    } finally {
      vertx.eventBus().send("complete.coffee.order",
          Json.encode(new JsonObject()
              .put("order", status)
              .put("id", body.getString("id"))
          )
      );
    }
  }
}
