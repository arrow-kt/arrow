package arrow.raise.ktor.server.response

import arrow.core.raise.Raise
import arrow.core.raise.RaiseDSL
import arrow.core.raise.fold
import arrow.core.raise.withError
import arrow.raise.ktor.server.response.Response.Companion.Response
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.reflect.*

public suspend inline fun <reified TResponse> ApplicationCall.respondOrRaise(
  statusCode: HttpStatusCode? = null,
  body: suspend Raise<Response>.() -> TResponse,
): Unit = respondOrRaise(statusCode, typeInfo<TResponse>(), body)

public suspend inline fun <Error, reified TResponse> ApplicationCall.respondOrRaise(
  errorResponse: (Error) -> Response,
  statusCode: HttpStatusCode? = null,
  body: suspend Raise<Error>.() -> TResponse,
): Unit = respondOrRaise(statusCode, typeInfo<TResponse>()) { withError(errorResponse) { body(this) } }

@PublishedApi
internal suspend inline fun <TResponse> ApplicationCall.respondOrRaise(
  statusCode: HttpStatusCode? = null,
  responseTypeInfo: TypeInfo,
  body: suspend Raise<Response>.() -> TResponse,
): Unit = fold(
  block = { body(this) },
  recover = { it.respondTo(this) },
  transform = { respondSafely(statusCode, it, responseTypeInfo) }
)

@PublishedApi
internal suspend fun ApplicationCall.respondSafely(statusCode: HttpStatusCode?, result: Any?, typeInfo: TypeInfo) {
  when (result) {
    is Unit -> respond(statusCode ?: HttpStatusCode.NoContent)
    is HttpStatusCode -> respond(result)
    is Response -> result.respondTo(this)
    null -> respond(result, typeInfo)
    else -> when (statusCode) {
      null -> respond(result, typeInfo)
      else -> respond(statusCode, result, typeInfo)
    }
  }
}

@RaiseDSL
public fun Raise<Response>.raise(outgoingContent: OutgoingContent): Nothing = raise(Response(outgoingContent))

@RaiseDSL
public fun Raise<Response>.raise(statusCode: HttpStatusCode): Nothing = raise(Response(statusCode))

@RaiseDSL
public inline fun <reified T> Raise<Response>.raise(statusCode: HttpStatusCode, content: T): Nothing = raise(Response(statusCode, content))
