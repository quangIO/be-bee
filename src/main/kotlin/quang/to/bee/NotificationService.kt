package quang.to.bee

import io.vertx.core.http.ServerWebSocket
import io.vertx.core.json.Json
import quang.to.bee.helper.Action
import java.util.concurrent.CopyOnWriteArraySet

object NotificationService {
  private val sessions = CopyOnWriteArraySet<ServerWebSocket>()
  fun handler(socket: ServerWebSocket) {
    sessions.add(socket)
    socket.closeHandler {
      sessions.remove(socket)
    }
  }

  fun <T> broadcast(action: Action<T>) {
    val json = Json.encode(action)
    sessions.forEach { s ->
      try {
        s.writeTextMessage(json) {
          if (it.failed()) {
            s.close()
          }
        }
      } catch (e: IllegalStateException) {
        e.printStackTrace()
      }
    }
  }
}
