package arrow.raise.ktor.server.routing

import arrow.core.raise.Raise
import arrow.raise.ktor.server.response.Response
import arrow.raise.ktor.server.response.respondOrRaise
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*

public class RaiseRoutingContext<Call: ApplicationCall>(
  public val call: Call,
  raise: Raise<Response>
): Raise<Response> by raise

public typealias RespondingRaiseRoutingHandler<TResponse> = suspend RaiseRoutingContext<RoutingCall>.() -> TResponse

// due to compilation ambiguity between n-ary lambdas on function resolution, by using this SAM on the API it's resolved with a lower priority
// which mitigates the ambiguity of `handler { }` vs `handler { it -> }`
// equivalent of `suspend RaiseRoutingContext.(TRequest) -> TResponse`
public fun interface ReceivingRespondingRaiseRoutingHandler<TRequest, TResponse> {
  public suspend fun RaiseRoutingContext<RoutingCall>.handle(request: TRequest): TResponse
}

@PublishedApi
internal suspend inline fun <reified TResponse> RoutingContext.respondOrRaise(
  statusCode: HttpStatusCode? = null,
  body: RespondingRaiseRoutingHandler<TResponse>,
): Unit = call.respondOrRaise(statusCode, typeInfo<TResponse>()) { RaiseRoutingContext(call, this).body() }

@PublishedApi
internal suspend inline fun <reified TRequest : Any, reified TResponse> RoutingContext.respondOrRaise(
  statusCode: HttpStatusCode? = null,
  body: ReceivingRespondingRaiseRoutingHandler<TRequest, TResponse>,
): Unit = call.respondOrRaise(statusCode, typeInfo<TResponse>()) {
  val ctx = RaiseRoutingContext(call, this)
  with(body) { ctx.handle(call.receive<TRequest>()) }
}
