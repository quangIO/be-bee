package quang.to.bee

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.json.jackson.DatabindCodec.mapper
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler

private const val BEE_API = "/bee"

class MainVerticle : AbstractVerticle() {
  override fun start(startPromise: Promise<Void>) {
    mapper().registerModules(JavaTimeModule())
    val router = Router.router(vertx)
    router.route().failureHandler {
      it.failure().printStackTrace()
      it.response().end(it.failure().message)
    }
    with(router) {
      route().handler(CorsHandler.create("*"))
      route().handler(BodyHandler.create(false))

      get(BEE_API).handler(BeeController::allBees)
      post(BEE_API).handler(BeeController::register)
      delete(BEE_API).handler(BeeController::delete)
      put(BEE_API).handler(BeeController::update)

      post("$BEE_API/nectar").handler(BeeController::takeNectar)
      post("$BEE_API/honey").handler(BeeController::makeHoney)

      get("$BEE_API/stats").handler(BeeController::aggregateStats)
      get("$BEE_API/events").handler(BeeController::getStatusEvents)

      get("/stats").handler(ProductController::aggregateStats)
      get("/honeyEvents").handler(ProductController::allHoneyEvent)
    }
    val port = System.getenv("PORT")?.toInt() ?: 8888
    vertx.createHttpServer().apply {
      requestHandler(router)
      websocketHandler(NotificationService::handler)
    }.listen(port) { result ->
      if (result.succeeded()) startPromise.complete() else startPromise.fail(result.cause())
    }
  }
}

fun main() {
  Vertx.vertx().deployVerticle(MainVerticle())
}
