package arrow.raise.ktor.server

import io.ktor.http.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*

public suspend inline fun <reified T> RoutingContext.respondOrRaise(
  statusCode: HttpStatusCode? = null,
  body: suspend RaiseRoutingContext.() -> T,
): Unit = respondOrRaise(statusCode, typeInfo<T>(), body)

@PublishedApi
internal suspend inline fun <T> RoutingContext.respondOrRaise(
  statusCode: HttpStatusCode? = null,
  typeInfo: TypeInfo,
  body: suspend RaiseRoutingContext.() -> T,
): Unit = handleOrRaise {
  val result = body()
  call.respondSafely(statusCode, result, typeInfo)
}

@PublishedApi
internal suspend fun RoutingCall.respondSafely(statusCode: HttpStatusCode?, result: Any?, typeInfo: TypeInfo) {
  when (result) {
    is Unit -> respond(HttpStatusCodeContent(statusCode ?: HttpStatusCode.NoContent))
    is HttpStatusCode -> respond(HttpStatusCodeContent(result))
    is Response -> result.respondTo(this)
    else -> when (statusCode) {
      null -> respond(result, typeInfo)
      else -> respond(statusCode, result, typeInfo)
    }
  }
}
