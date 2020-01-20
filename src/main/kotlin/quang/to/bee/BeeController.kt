package quang.to.bee

import io.vertx.ext.web.RoutingContext
import jooq.codegen.Tables.*
import jooq.codegen.tables.pojos.BeeStatus
import jooq.codegen.tables.pojos.HoneyEvent
import jooq.codegen.tables.pojos.NectarEvent
import jooq.codegen.tables.pojos.StatusEvent
import org.jooq.DSLContext
import org.jooq.impl.DSL.sum
import quang.to.bee.helper.Action
import quang.to.bee.helper.DatabaseHelper
import quang.to.bee.helper.endWithJson
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

private fun DSLContext.deleteBee(vararg id: Int) =
  deleteFrom(BEE_STATUS).where(BEE_STATUS.BEE_ID.`in`(*id.toTypedArray()))

object BeeController {
  fun allBees(event: RoutingContext) {
    val bees = MappedDb.statusDao.findAll()
    event.response().endWithJson(bees)
  }

  fun register(event: RoutingContext) {
    event.queryParam("id").map(String::toInt).forEach { id ->
      DatabaseHelper.dslContext().use { context ->
        context.batch(
          context.deleteBee(id),
          context.insertInto(BEE_STATUS, BEE_STATUS.BEE_ID).values(id)
        ).execute()
      }
      NotificationService.broadcast(Action.Insert(id))
    }
    event.response().end()
  }

  fun delete(event: RoutingContext) {
    val ids = event.queryParam("id").map(String::toInt)
    DatabaseHelper.dslContext().use { context ->
      context.deleteBee(*ids.toIntArray()).execute()
    }
    ids.forEach { id -> NotificationService.broadcast(Action.Delete(id)) }
    event.response().end()
  }

  fun update(event: RoutingContext) {
    val status = event.bodyAsJson.mapTo(BeeStatus::class.java)
    MappedDb.statusDao.update(status)
    NotificationService.broadcast(Action.Update(status))
    event.response().end()
  }

  fun takeNectar(event: RoutingContext) {
    val nectar = event.bodyAsJson.mapTo(NectarEvent::class.java)
    MappedDb.nectarDao.insert(nectar)
    NotificationService.broadcast(Action.Nectar(nectar))
    event.response().end()
  }

  fun makeHoney(event: RoutingContext) {
    val honey = event.bodyAsJson.mapTo(HoneyEvent::class.java)
    MappedDb.honeyDao.insert(honey)
    NotificationService.broadcast(Action.Honey(honey))
    event.response().end()
  }

  fun aggregateStats(event: RoutingContext) {
    data class AggregateStats(val nectar: Float, val honey: Float)
    val timestamp = event.queryParam("from").first().toLong()
    val id = event.queryParam("id").first().toInt()
    val from = LocalDateTime.ofInstant(
      Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId()
    )
    val stat = DatabaseHelper.dslContext().use { ctx ->
      val nectar = with(NECTAR_EVENT) {
        ctx.select(sum(AMOUNT)).from(this)
          .where(BEE_ID.eq(id).and(MODIFIED.ge(from))).fetchOneInto(BigDecimal::class.java)
      }
      val honey = with(HONEY_EVENT) {
        ctx.select(sum(AMOUNT)).from(this)
          .where(BEE_ID.eq(id).and(MODIFIED.ge(from))).fetchOneInto(BigDecimal::class.java)
      }
      AggregateStats(nectar.toFloat(), honey.toFloat())
    }
    event.response().endWithJson(stat)
  }

  fun getStatusEvents(event: RoutingContext) {
    val timestamp = event.queryParam("from").first().toLong()
    val id = event.queryParam("id").first().toInt()
    val from = LocalDateTime.ofInstant(
      Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId()
    )
    val events = DatabaseHelper.dslContext().use { ctx ->
      with(STATUS_EVENT) {
        ctx.select().from(this).where(BEE_ID.eq(id).and(MODIFIED.ge(from))).fetchInto(StatusEvent::class.java)
      }
    }
    event.response().endWithJson(events)
  }
}
