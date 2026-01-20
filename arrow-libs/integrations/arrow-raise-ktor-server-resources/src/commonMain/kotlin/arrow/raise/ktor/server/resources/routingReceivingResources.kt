@file:Suppress("API_NOT_AVAILABLE")

package arrow.raise.ktor.server.resources

import arrow.core.raise.Raise
import arrow.core.raise.RaiseDSL
import arrow.raise.ktor.server.response.Response
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.utils.io.KtorDsl

public typealias ResourcedReceivingRespondingRaiseRoutingHandler<TRoute, TBody, TResponse> = suspend context(Raise<Response>) RoutingContext.(route: TRoute, body: TBody) -> TResponse

@KtorDsl
@RaiseDSL
@IgnorableReturnValue
public inline fun <reified TRoute : Any, reified TRequest : Any, reified TResponse> Route.patchOrRaise(
  statusCode: HttpStatusCode? = null,
  crossinline body: ResourcedReceivingRespondingRaiseRoutingHandler<TRoute, TRequest, TResponse>,
): Route = patchOrRaise<TRoute, TResponse>(statusCode) { body(it, call.receive()) }

@KtorDsl
@RaiseDSL
@IgnorableReturnValue
public inline fun <reified TRoute : Any, reified TRequest : Any, reified TResponse> Route.postOrRaise(
  statusCode: HttpStatusCode? = null,
  crossinline body: ResourcedReceivingRespondingRaiseRoutingHandler<TRoute, TRequest, TResponse>,
): Route = postOrRaise<TRoute, TResponse>(statusCode) { body(it, call.receive()) }

@KtorDsl
@RaiseDSL
@IgnorableReturnValue
public inline fun <reified TRoute : Any, reified TRequest : Any, reified TResponse> Route.putOrRaise(
  statusCode: HttpStatusCode? = null,
  crossinline body: ResourcedReceivingRespondingRaiseRoutingHandler<TRoute, TRequest, TResponse>,
): Route = putOrRaise<TRoute, TResponse>(statusCode) { body(it, call.receive()) }
