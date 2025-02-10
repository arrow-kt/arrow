package arrow.raise.ktor.server

import io.ktor.http.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*

@PublishedApi
internal inline fun <reified T> respondOrRaise(wrapper: (RoutingHandler) -> Route, statusCode: HttpStatusCode = HttpStatusCode.OK, crossinline body: suspend RaiseRoutingContext.() -> T): Route =
  wrapper { respondOrRaise(statusCode) { body() } }

public suspend inline fun <reified T> RoutingContext.respondOrRaise(statusCode: HttpStatusCode = HttpStatusCode.OK, body: RaiseRoutingContext.() -> T): Unit =
  perform { call.respondSafely(statusCode, body(), typeInfo<T>()) }

@PublishedApi
internal suspend fun <T> RoutingCall.respondSafely(statusCode: HttpStatusCode, result: T, typeInfo: TypeInfo) {
  when (result) {
    is Unit -> respond(HttpStatusCodeContent(statusCode))
    is HttpStatusCode -> respond(HttpStatusCodeContent(result))
    is RoutingResponse -> respond(result)
    else -> respond(statusCode, result, typeInfo)
  }
}

// TODO: more of these:
public inline fun <reified T> Route.raisingGet(path: String, statusCode: HttpStatusCode = HttpStatusCode.OK, crossinline body: suspend RaiseRoutingContext.() -> T): Route =
  respondOrRaise({ get(path, it) }, statusCode, body)

// or alternatively a la get()...
public inline fun <reified T> Route.raisingGetAlt(path: String, statusCode: HttpStatusCode = HttpStatusCode.OK, crossinline body: suspend RaiseRoutingContext.() -> T): Route =
  route(path, HttpMethod.Get) { handleOrRaise { call.respondSafely(statusCode, body(), typeInfo<T>()) } }
