@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

package arrow.raise.ktor.server.request

import arrow.core.raise.Raise
import arrow.core.raise.withError
import arrow.raise.ktor.server.RaiseRoutingContext
import arrow.raise.ktor.server.Response
import io.ktor.server.routing.RoutingCall
import io.ktor.util.reflect.*
import kotlin.jvm.JvmName

@JvmName("queryOrRaiseReified")
public inline fun <reified A : Any> Raise<RequestError>.queryOrRaise(call: RoutingCall, name: String): A = parameterOrRaise(call.queryParameters, Parameter.Query(name), typeInfo<A>())
public inline fun <A : Any> Raise<RequestError>.queryOrRaise(call: RoutingCall, name: String, transform: Raise<String>.(String) -> A): A = parameterOrRaise(call.queryParameters, Parameter.Query(name), transform)
public fun Raise<RequestError>.queryOrRaise(call: RoutingCall, name: String): String = parameterOrRaise(call.queryParameters, Parameter.Query(name))

// RaiseRoutingContext (default error response)
@JvmName("queryOrRaiseReified")
public inline fun <reified A : Any> RaiseRoutingContext.queryOrRaise(name: String): A = errorRaise.queryOrRaise<A>(call, name)
public inline fun <A : Any> RaiseRoutingContext.queryOrRaise(name: String, transform: Raise<String>.(String) -> A): A = errorRaise.queryOrRaise(call, name, transform)
public fun RaiseRoutingContext.queryOrRaise(name: String): String = errorRaise.queryOrRaise(call, name)

// RaiseRoutingContext (custom error response)
@JvmName("queryOrRaiseReified")
public inline fun <reified A : Any> RaiseRoutingContext.queryOrRaise(name: String, errorResponse: (RequestError) -> Response): A = withError(errorResponse) { queryOrRaise<A>(call, name) }
public inline fun <A : Any> RaiseRoutingContext.queryOrRaise(name: String, errorResponse: (RequestError) -> Response, transform: Raise<String>.(String) -> A): A = withError(errorResponse) { queryOrRaise(call, name, transform) }
public inline fun RaiseRoutingContext.queryOrRaise(name: String, errorResponse: (RequestError) -> Response): String = withError(errorResponse) { queryOrRaise(call, name) }
