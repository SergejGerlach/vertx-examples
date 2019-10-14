package de.sergejgerlach.vertx_examples;

import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class MainVerticle extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(MainVerticle.class.getName());
    }

    @Override
    public void start(Promise<Void> startPromise) {
        startWebApp(http -> completeStartup(http, startPromise));
    }

    private void completeStartup(AsyncResult<HttpServer> http, Promise<Void> startPromise) {
        if (http.succeeded()) {
            startPromise.complete();
            System.out.println("HTTP server started on port " + http.result().actualPort());
        } else {
            startPromise.fail(http.cause());
        }
    }

    private void startWebApp(Handler<AsyncResult<HttpServer>> next) {
        // Create a router object.
        Router router = Router.router(vertx);

        router.route("/assets/*").handler(StaticHandler.create("assets"));
        router.route("/api/*").handler(BodyHandler.create());

        router.route("/").handler(this::handleRoot);
        router.get("/api/timer").handler(this::timer);
        router.post("/api/c").handler(this::_create);
        router.get("/api/r/:id").handler(this::_read);
        router.put("/api/u/:id").handler(this::_update);
        router.delete("/api/d/:id").handler(this::_delete);

        // Create the HTTP server and pass the "accept" method to the request handler.
        vertx.createHttpServer().requestHandler(router).listen(8888, next);
    }

    private void handleRoot(RoutingContext routingContext) {
        ok(routingContext.response())
                .end("<h1>Hello from my first Vert.x 3 application</h1>");
    }

    private void timer(RoutingContext routingContext) {
        vertx.setTimer(3000, id -> {
            ok(routingContext.response())
                    .end("Hello from Vert.x! - " + System.currentTimeMillis());
        });
    }

    private void _create(RoutingContext routingContext) {
        String data = routingContext.getBodyAsString();
        status(json(routingContext.response()), 201).end(data);
    }

    private void _read(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        if (id == null) {
            status(routingContext.response(), 400).end();
        } else {
            ok(json(routingContext.response())).end(String.format("{id: %s}", id));
        }
    }

    private void _update(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        JsonObject json = routingContext.getBodyAsJson();
        if (id == null || json == null) {
            status(routingContext.response(), 400).end();
        } else {
            ok(json(routingContext.response())).end(json.encodePrettily());
        }
    }

    private void _delete(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        if (id == null) {
            status(routingContext.response(), 400).end();
        } else {
            status(routingContext.response(), 204).end(id);
        }
    }

    private HttpServerResponse status(HttpServerResponse response, int statusCode) {
        return response.setStatusCode(statusCode);
    }

    private HttpServerResponse ok(HttpServerResponse response) {
        return status(response, 200);
    }

    private HttpServerResponse json(HttpServerResponse response) {
        return response.putHeader("content-type", "application/json;charset=UTF-8");
    }

}
