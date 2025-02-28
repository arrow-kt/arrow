@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

package arrow.raise.ktor.server.request

import arrow.core.raise.Raise
import arrow.core.raise.ensureNotNull
import arrow.core.raise.withError
import arrow.raise.ktor.server.RaiseRoutingContext
import arrow.raise.ktor.server.Response
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.RoutingContext
import io.ktor.util.reflect.typeInfo
import kotlin.jvm.JvmName

// Raise+RoutingCall
context(Raise<RequestError>) public fun RoutingCall.queryOrRaise(name: String) = parameterOrRaise(queryParameters, Parameter.Query(name)) { it }
context(Raise<RequestError>) public inline fun <reified A : Any> RoutingCall.queryOrRaise(name: String): A = parameterOrRaise(queryParameters, Parameter.Query(name), typeInfo<A>())
context(Raise<RequestError>) public inline fun <A : Any> RoutingCall.queryOrRaise(name: String, transform: Raise<String>.(String) -> A): A = parameterOrRaise(queryParameters, Parameter.Query(name), transform)
context(Raise<RequestError>) public fun RoutingCall.queryIntOrRaise(name: String): Int = parameterOrRaise(queryParameters, Parameter.Query(name)) { value -> ensureNotNull(value.toIntOrNull()) { "Expected $value to be a valid Int." } }

// Raise+RaiseRoutingContext
context(RaiseRoutingContext) public fun Raise<RequestError>.queryOrRaise(name: String) = call.queryOrRaise(name)
context(RaiseRoutingContext) public inline fun <reified A : Any> Raise<RequestError>.queryOrRaise(name: String): A = call.queryOrRaise<A>(name)
context(RaiseRoutingContext) public inline fun <A : Any> Raise<RequestError>.queryOrRaise(name: String, transform: Raise<String>.(String) -> A): A = call.queryOrRaise(name, transform)
context(RaiseRoutingContext) public fun Raise<RequestError>.queryIntOrRaise(name: String): Int = call.queryIntOrRaise(name)

// Raise+RoutingContext
context(RoutingContext) public fun Raise<RequestError>.queryOrRaise(name: String) = call.queryOrRaise(name)
context(RoutingContext) public inline fun <reified A : Any> Raise<RequestError>.queryOrRaise(name: String): A = call.queryOrRaise<A>(name)
context(RoutingContext) public inline fun <A : Any> Raise<RequestError>.queryOrRaise(name: String, transform: Raise<String>.(String) -> A): A = call.queryOrRaise(name, transform)
context(RoutingContext) public fun Raise<RequestError>.queryIntOrRaise(name: String): Int = call.queryIntOrRaise(name)

// RaiseRoutingContext (default error response)
public fun RaiseRoutingContext.queryOrRaise(name: String): String = errorRaise.queryOrRaise(name) { it }
@JvmName("pathOrRaiseReified")
public inline fun <reified A : Any> RaiseRoutingContext.queryOrRaise(name: String): String = errorRaise.parameterOrRaise(call.queryParameters, Parameter.Query(name), typeInfo<A>())
public inline fun <A : Any> RaiseRoutingContext.queryOrRaise(name: String, transform: Raise<String>.(String) -> A): A = errorRaise.parameterOrRaise(call.queryParameters, Parameter.Query(name), transform)

// RaiseRoutingContext (custom error response)
public inline fun RaiseRoutingContext.queryOrRaise(name: String, errorResponse: (RequestError) -> Response): String = queryOrRaise(name, errorResponse) { it }
@JvmName("pathOrRaiseReified")
public inline fun <reified A : Any> RaiseRoutingContext.queryOrRaise(name: String, errorResponse: (RequestError) -> Response): String = withError(errorResponse) { parameterOrRaise(call.queryParameters, Parameter.Query(name), typeInfo<A>()) }
public inline fun <A : Any> RaiseRoutingContext.queryOrRaise(name: String, errorResponse: (RequestError) -> Response, transform: Raise<String>.(String) -> A): A =  withError(errorResponse) { errorRaise.parameterOrRaise(call.queryParameters, Parameter.Query(name), transform) }
