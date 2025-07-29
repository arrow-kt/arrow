package arrow.raise.ktor.server.response

import arrow.core.raise.Raise
import arrow.core.raise.RaiseDSL
import arrow.core.raise.context.withError
import arrow.core.raise.fold
import arrow.raise.ktor.server.response.Response.Companion.Response
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.reflect.*

public suspend inline fun <reified TResponse> ApplicationCall.respondOrRaise(
  statusCode: HttpStatusCode? = null,
  body: suspend context(Raise<Response>) () -> TResponse,
): Unit = respondOrRaise(statusCode, typeInfo<TResponse>(), body)

public suspend inline fun <Error, reified TResponse> ApplicationCall.respondOrRaise(
  errorResponse: (Error) -> Response,
  statusCode: HttpStatusCode? = null,
  body: suspend context(Raise<Error>) () -> TResponse,
): Unit = respondOrRaise(statusCode, typeInfo<TResponse>()) { withError(errorResponse) { body() } }

@PublishedApi
internal suspend inline fun <TResponse> ApplicationCall.respondOrRaise(
  statusCode: HttpStatusCode? = null,
  responseTypeInfo: TypeInfo,
  body: suspend context(Raise<Response>) () -> TResponse,
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

context(r: Raise<Response>)
@RaiseDSL
@Suppress("NOTHING_TO_INLINE")
public inline fun raise(outgoingContent: OutgoingContent): Nothing = r.raise(Response(outgoingContent))

context(r: Raise<Response>)
@RaiseDSL
@Suppress("NOTHING_TO_INLINE")
public inline fun raise(statusCode: HttpStatusCode): Nothing = r.raise(Response(statusCode))

context(r: Raise<Response>)
@RaiseDSL
public inline fun <reified T> raise(statusCode: HttpStatusCode, content: T): Nothing = r.raise(Response(statusCode, content))
