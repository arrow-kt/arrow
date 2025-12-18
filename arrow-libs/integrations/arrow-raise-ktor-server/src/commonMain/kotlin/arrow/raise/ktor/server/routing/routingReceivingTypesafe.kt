@file:Suppress("API_NOT_AVAILABLE")

package arrow.raise.ktor.server.routing

import arrow.core.raise.Raise
import arrow.core.raise.RaiseDSL
import arrow.raise.ktor.server.response.Response
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Route
import io.ktor.server.resources.patch
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.routing.RoutingContext
import io.ktor.utils.io.KtorDsl

public fun interface ResourcedReceivingRespondingRaiseRoutingHandler<TRoute, TBody, TResponse> {
  context(_: Raise<Response>)
  public suspend fun RoutingContext.handle(request: TRoute, body: TBody): TResponse
}

@KtorDsl
@RaiseDSL
@IgnorableReturnValue
public inline fun <reified TRoute : Any, reified TRequest : Any, reified TResponse> Route.patchOrError(
  statusCode: HttpStatusCode? = null,
  body: ResourcedReceivingRespondingRaiseRoutingHandler<TRoute, TRequest, TResponse>,
): Route = patch<TRoute> { route -> respondOrRaise<TRequest, TResponse>(statusCode) { with(body) { handle(route, it) } } }


@KtorDsl
@RaiseDSL
@IgnorableReturnValue
public inline fun <reified TRoute : Any, reified TRequest : Any, reified TResponse> Route.postOrError(
  statusCode: HttpStatusCode? = null,
  body: ResourcedReceivingRespondingRaiseRoutingHandler<TRoute, TRequest, TResponse>,
): Route = post<TRoute> { route -> respondOrRaise<TRequest, TResponse>(statusCode) { with(body) { handle(route, it) } } }

@KtorDsl
@RaiseDSL
@IgnorableReturnValue
public inline fun <reified TRoute : Any, reified TRequest : Any, reified TResponse> Route.putOrError(
  statusCode: HttpStatusCode? = null,
  body: ResourcedReceivingRespondingRaiseRoutingHandler<TRoute, TRequest, TResponse>,
): Route = put<TRoute> { route -> respondOrRaise<TRequest, TResponse>(statusCode) { with(body) { handle(route, it) } } }
