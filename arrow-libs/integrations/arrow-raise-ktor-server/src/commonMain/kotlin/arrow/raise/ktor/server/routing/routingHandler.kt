package arrow.raise.ktor.server.routing

import arrow.core.raise.Raise
import arrow.raise.ktor.server.response.Response
import arrow.raise.ktor.server.response.respondOrRaise
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*

public typealias RespondingRaiseRoutingHandler<TResponse> = suspend context(Raise<Response>) RoutingContext.() -> TResponse

// due to compilation ambiguity between n-ary lambdas on function resolution, by using this SAM on the API it's resolved with a lower priority
// which mitigates the ambiguity of `handler { }` vs `handler { it -> }`
// equivalent of `suspend context(Raise<Response>) RoutingContext.(TRequest) -> TResponse`
public fun interface ReceivingRespondingRaiseRoutingHandler<TRequest, TResponse> {
  context(_: Raise<Response>)
  public suspend fun RoutingContext.handle(request: TRequest): TResponse
}

@PublishedApi
internal suspend inline fun <reified TResponse> RoutingContext.respondOrRaise(
  statusCode: HttpStatusCode? = null,
  body: RespondingRaiseRoutingHandler<TResponse>,
): Unit = call.respondOrRaise(statusCode, typeInfo<TResponse>()) { body() }

@PublishedApi
internal suspend inline fun <reified TRequest : Any, reified TResponse> RoutingContext.respondOrRaise(
  statusCode: HttpStatusCode? = null,
  body: ReceivingRespondingRaiseRoutingHandler<TRequest, TResponse>,
): Unit = call.respondOrRaise(statusCode, typeInfo<TResponse>()) {
  with(body) { handle(call.receive<TRequest>()) }
}
