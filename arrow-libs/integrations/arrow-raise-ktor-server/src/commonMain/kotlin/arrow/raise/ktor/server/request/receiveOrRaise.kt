@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

package arrow.raise.ktor.server.request

import arrow.core.raise.Raise
import arrow.core.raise.withError
import arrow.raise.ktor.server.RaiseRoutingContext
import arrow.raise.ktor.server.Response
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.RoutingContext
import io.ktor.util.reflect.typeInfo

// Raise+RoutingCall
context(Raise<RequestError>) public suspend inline fun <reified A: Any> RoutingCall.receiveOrRaise(): A = receiveOrRaise(this, typeInfo<A>())
context(Raise<RequestError>) public suspend inline fun <reified A: Any> RoutingCall.receiveNullableOrRaise(): A? = receiveNullableOrRaise(this, typeInfo<A>())

// Raise+RaiseRoutingContext
context(RaiseRoutingContext) public suspend inline fun <reified A: Any> Raise<RequestError>.receiveOrRaise(): A = receiveOrRaise(call, typeInfo<A>())
context(RaiseRoutingContext) public suspend inline fun <reified A: Any> Raise<RequestError>.receiveNullableOrRaise(): A? = receiveNullableOrRaise(call, typeInfo<A>())

// Raise+RoutingContext
context(RoutingContext) public suspend inline fun <reified A: Any> Raise<RequestError>.receiveOrRaise(): A = receiveOrRaise(call, typeInfo<A>())
context(RoutingContext) public suspend inline fun <reified A: Any> Raise<RequestError>.receiveNullableOrRaise(): A? = receiveNullableOrRaise(call, typeInfo<A>())

// RaiseRoutingContext (default error response)
public suspend inline fun <reified A : Any> RaiseRoutingContext.receiveOrRaise(): A =
  errorRaise.receiveOrRaise(call, typeInfo<A>())
public suspend inline fun <reified A : Any> RaiseRoutingContext.receiveNullableOrRaise(): A? =
  errorRaise.receiveNullableOrRaise(call, typeInfo<A>())

// RaiseRoutingContext (custom error response)
public suspend inline fun <reified A : Any> RaiseRoutingContext.receiveOrRaise(errorResponse: (RequestError) -> Response): A =
  withError(errorResponse) { receiveOrRaise(call, typeInfo<A>()) }
public suspend inline fun <reified A : Any> RaiseRoutingContext.receiveNullableOrRaise(errorResponse: (RequestError) -> Response): A? =
  withError(errorResponse) { receiveNullableOrRaise(call, typeInfo<A>()) }
