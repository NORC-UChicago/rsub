package org.norc.rsub;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.ext.web.impl.RouterImpl;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainVerticle extends AbstractVerticle {
  public final static Logger logger = LogManager.getLogger(MainVerticle.class);

  @Override
  public void start() throws Exception {
    vertx.deployVerticle("java:org.norc.rsub.WorkerVerticle", new DeploymentOptions().setWorker(true).setWorkerPoolName("coffee-making-pool").setWorkerPoolSize(5).setInstances(5));
    Router router = new RouterImpl(vertx);

    router.route().handler(BodyHandler.create());
    router.post("/coffee")
        .consumes("application/x-www-form-urlencoded")
        .produces("application/json")
        .handler(this::coffeeHandler);

    router.route("/webroot/*").handler(
        StaticHandler.create()
            .setCachingEnabled(true)
            .setWebRoot("webroot")
    );
    router.get("/").handler(ctx -> {
      ctx.reroute("/webroot/index.html");
    });
    router.get("/favicon.ico").handler(ctx -> {
      ctx.reroute("/webroot/favicon.ico");
    });

    SockJSHandlerOptions options = new SockJSHandlerOptions().setHeartbeatInterval(2000);
    SockJSHandler sockJSHandler = SockJSHandler.create(vertx, options);
    BridgeOptions bridgeOptions = new BridgeOptions()
        .addInboundPermitted(new PermittedOptions().setAddress("/client.register"))
        .addOutboundPermitted(new PermittedOptions().setAddress("service.ui-log"))
        .addOutboundPermitted(new PermittedOptions().setAddress("service.ui-lst"));
    sockJSHandler.bridge(bridgeOptions, event -> {
      logger.info("sockJSHandler: websocket event occurred: {}; {}",
          event.type(),
          event.getRawMessage()
      );
      event.complete(true);
    });
    router.route("/client.register/*").handler(sockJSHandler);

    vertx.createHttpServer().requestHandler(router).listen(8080);
  }

  private void coffeeHandler(RoutingContext ctx) {
    String id = UUID.randomUUID().toString();

    String customer = ctx.request().getFormAttribute("customer");
    String coffee = ctx.request().getFormAttribute("coffee");
    String size = ctx.request().getFormAttribute("size");

    /*
    JsonObject requestBody = ctx.getBodyAsJson();
    logger.info("Coffee request: {}", requestBody);
    String customer = requestBody.getString("customer");
    String coffee = requestBody.getString("coffee");
    String size = requestBody.getString("size");
    */

    JsonObject message = new JsonObject()
        .put("id", id)
        .put("customer", customer)
        .put("coffee", coffee)
        .put("size", size);
    ctx.vertx().eventBus().send("process.coffee.order", message);

    ctx.response()
        .setChunked(true)
        .putHeader("Content-Type", "application/json")
        .end(Json.encode(new JsonObject()
            .put("order", "ok")
            .put("id", id)
        ));
  }
}
