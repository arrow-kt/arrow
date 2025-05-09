package arrow.raise.ktor.server

import arrow.raise.ktor.server.request.receiveNullableOrRaise
import io.ktor.http.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*

public typealias RespondingRaiseRoutingHandler<TResponse> = suspend RaiseRoutingContext.() -> TResponse

// due to compilation ambiguity between n-ary lambdas on function resolution, by using this SAM on the API it's resolved with a lower priority
// which mitigates the ambiguity of `handler { }` vs `handler { it -> }`
// equivalent of `suspend context(Raise<Response>) RoutingContext.(TRequest) -> TResponse`
public fun interface ReceivingRespondingRaiseRoutingHandler<TRequest, TResponse>{
  public suspend fun RaiseRoutingContext.handle(request: TRequest): TResponse
}

public suspend inline fun <reified TResponse> RoutingContext.respondOrRaise(
  statusCode: HttpStatusCode? = null,
  body: RespondingRaiseRoutingHandler<TResponse>,
): Unit = respondOrRaise(statusCode, typeInfo<TResponse>(), body)

@PublishedApi
internal suspend inline fun <TResponse> RoutingContext.respondOrRaise(
  statusCode: HttpStatusCode? = null,
  responseTypeInfo: TypeInfo,
  body: RespondingRaiseRoutingHandler<TResponse>,
): Unit = handleOrRaise {
  val result = body()
  call.respondSafely(statusCode, result, responseTypeInfo)
}

// TODO: should this be named `receiveRespondOrRaise`?
public suspend inline fun <reified TRequest, reified TResponse> RoutingContext.respondOrRaise(
  statusCode: HttpStatusCode? = null,
  body: ReceivingRespondingRaiseRoutingHandler<TRequest, TResponse>,
): Unit = respondOrRaise(statusCode, typeInfo<TRequest>(), typeInfo<TResponse>(), body)

@PublishedApi
internal suspend inline fun <TRequest, TResponse> RoutingContext.respondOrRaise(
  statusCode: HttpStatusCode? = null,
  requestTypeInfo: TypeInfo,
  responseTypeInfo: TypeInfo,
  body: ReceivingRespondingRaiseRoutingHandler<TRequest, TResponse>,
): Unit = handleOrRaise {
  val request: TRequest = raiseError {
    @Suppress("UNCHECKED_CAST") // TODO: if TRequest is nullable do we break things with this?
    receiveNullableOrRaise<Any>(call, requestTypeInfo) as TRequest
  }
  val result = with(body) { handle(request) }
  call.respondSafely(statusCode, result, responseTypeInfo)
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
