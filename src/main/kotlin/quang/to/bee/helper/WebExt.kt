package quang.to.bee.helper

import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import jooq.codegen.tables.pojos.BeeStatus
import jooq.codegen.tables.pojos.HoneyEvent
import jooq.codegen.tables.pojos.NectarEvent

fun <T> HttpServerResponse.endWithJson(e: T) =
  putHeader("content-type", "application/json").end(Json.encode(e))

enum class ActionType {
  UPDATE,
  DELETE,
  INSERT,
  NECTAR,
  HONEY,
  ALL
}

sealed class Action<out T>(val type: ActionType, val payload: T? = null) {
  class Update(payload: BeeStatus) : Action<BeeStatus>(ActionType.UPDATE, payload)
  class Delete(id: Int) : Action<Int>(ActionType.DELETE, id)
  class Insert(id: Int) : Action<Int>(ActionType.INSERT, id)
  class Nectar(nectar: NectarEvent) : Action<NectarEvent>(ActionType.NECTAR, nectar)
  class Honey(honey: HoneyEvent) : Action<HoneyEvent>(ActionType.HONEY, honey)
  object All : Action<Nothing?>(ActionType.ALL)
}

