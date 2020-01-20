package quang.to.bee

import io.vertx.ext.web.RoutingContext
import jooq.codegen.Tables
import org.jooq.impl.DSL
import quang.to.bee.helper.DatabaseHelper
import quang.to.bee.helper.endWithJson
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

object ProductController {
  fun aggregateStats(event: RoutingContext) {
    data class AggregateStats(val nectar: Float, val honey: Float, val inDangerBees: Int)

    val timestamp = event.queryParam("from").first().toLong()
    val damageThreshold = event.queryParam("damage").firstOrNull()?.toDouble() ?: 70.0
    val from = LocalDateTime.ofInstant(
      Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId()
    )
    val stat = DatabaseHelper.dslContext().use { ctx ->
      val nectar = with(Tables.NECTAR_EVENT) {
        ctx.select(DSL.sum(AMOUNT)).from(this)
          .where(MODIFIED.ge(from)).fetchOneInto(BigDecimal::class.java)
      }
      val honey = with(Tables.HONEY_EVENT) {
        ctx.select(DSL.sum(AMOUNT)).from(this)
          .where(MODIFIED.ge(from)).fetchOneInto(BigDecimal::class.java)
      }
      val inDanger = with(Tables.BEE_STATUS) {
        ctx.select(DSL.count()).from(this)
          .where(DAMAGE.ge(damageThreshold)).fetchOneInto(Int::class.java)
      }
      AggregateStats(nectar?.toFloat() ?: 0.0f, honey?.toFloat() ?: 0.0f, inDanger)
    }
    event.response().endWithJson(stat)
  }

  fun allHoneyEvent(event: RoutingContext) {
    val timestamp = event.queryParam("from").first().toLong()
    val from = LocalDateTime.ofInstant(
      Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId()
    )
    val events = MappedDb.honeyDao.fetchRangeOfModified(from, LocalDateTime.MAX)
    event.response().endWithJson(events)
  }
}
