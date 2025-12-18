@file:Suppress("API_NOT_AVAILABLE")

package arrow.raise.ktor.server.routing.typesafe

import arrow.core.raise.Raise
import arrow.core.raise.RaiseDSL
import arrow.raise.ktor.server.response.Response
import arrow.raise.ktor.server.routing.respondOrRaise
import io.ktor.http.HttpStatusCode
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.head
import io.ktor.server.resources.options
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.utils.io.KtorDsl

public typealias TypeSafeRespondingRaiseRoutingHandler<TRoute, TResponse> = suspend context(Raise<Response>) RoutingContext.(TRoute) -> TResponse

@KtorDsl
@RaiseDSL
@IgnorableReturnValue
public inline fun <reified TRoute : Any, reified TResponse> Route.deleteOrRaise(
  statusCode: HttpStatusCode? = null,
  crossinline body: TypeSafeRespondingRaiseRoutingHandler<TRoute, TResponse>,
): Route = delete<TRoute> {
  respondOrRaise(statusCode) { body(it) }
}

@KtorDsl
@RaiseDSL
@IgnorableReturnValue
public inline fun <reified TRoute : Any, reified TResponse> Route.getOrRaise(
  statusCode: HttpStatusCode? = null,
  crossinline body: TypeSafeRespondingRaiseRoutingHandler<TRoute, TResponse>,
): Route = get<TRoute> { respondOrRaise(statusCode) { body(it) } }

@KtorDsl
@RaiseDSL
@IgnorableReturnValue
public inline fun <reified TRoute : Any, reified TResponse> Route.headOrRaise(
  statusCode: HttpStatusCode? = null,
  crossinline body: TypeSafeRespondingRaiseRoutingHandler<TRoute, TResponse>,
): Route = head<TRoute> { respondOrRaise(statusCode) { body(it) } }

@KtorDsl
@RaiseDSL
@IgnorableReturnValue
public inline fun <reified TRoute : Any, reified TResponse> Route.optionsOrRaise(
  statusCode: HttpStatusCode? = null,
  crossinline body: TypeSafeRespondingRaiseRoutingHandler<TRoute, TResponse>,
): Route = options<TRoute> { respondOrRaise(statusCode) { body(it) } }

@KtorDsl
@RaiseDSL
@IgnorableReturnValue
public inline fun <reified TRoute : Any, reified TResponse> Route.patchOrRaise(
  statusCode: HttpStatusCode? = null,
  crossinline body: TypeSafeRespondingRaiseRoutingHandler<TRoute, TResponse>,
): Route = patch<TRoute> { respondOrRaise(statusCode) { body(it) } }

@KtorDsl
@RaiseDSL
@IgnorableReturnValue
public inline fun <reified TRoute : Any, reified TResponse> Route.postOrRaise(
  statusCode: HttpStatusCode? = null,
  crossinline body: TypeSafeRespondingRaiseRoutingHandler<TRoute, TResponse>,
): Route = post<TRoute> { respondOrRaise(statusCode) { body(it) } }

@KtorDsl
@RaiseDSL
@IgnorableReturnValue
public inline fun <reified TRoute : Any, reified TResponse> Route.putOrRaise(
  statusCode: HttpStatusCode? = null,
  crossinline body: TypeSafeRespondingRaiseRoutingHandler<TRoute, TResponse>,
): Route = put<TRoute> { respondOrRaise(statusCode) { body(it) } }
