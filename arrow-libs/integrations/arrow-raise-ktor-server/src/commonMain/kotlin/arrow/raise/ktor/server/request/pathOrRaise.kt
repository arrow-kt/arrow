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
context(Raise<RequestError>) public fun RoutingCall.pathOrRaise(name: String) = parameterOrRaise(pathParameters, Parameter.Path(name)) { it }
context(Raise<RequestError>) public inline fun <reified A : Any> RoutingCall.pathOrRaise(name: String): A = parameterOrRaise(pathParameters, Parameter.Path(name), typeInfo<A>())
context(Raise<RequestError>) public inline fun <A : Any> RoutingCall.pathOrRaise(name: String, transform: Raise<String>.(String) -> A): A = parameterOrRaise(pathParameters, Parameter.Path(name), transform)
context(Raise<RequestError>) public fun RoutingCall.pathIntOrRaise(name: String): Int = parameterOrRaise(pathParameters, Parameter.Path(name)) { value -> ensureNotNull(value.toIntOrNull()) { "Expected $value to be a valid Int." } }

// Raise+RaiseRoutingContext
context(RaiseRoutingContext) public fun Raise<RequestError>.pathOrRaise(name: String) = call.pathOrRaise(name)
context(RaiseRoutingContext) public inline fun <reified A : Any> Raise<RequestError>.pathOrRaise(name: String): A = call.pathOrRaise<A>(name)
context(RaiseRoutingContext) public inline fun <A : Any> Raise<RequestError>.pathOrRaise(name: String, transform: Raise<String>.(String) -> A): A = call.pathOrRaise(name, transform)
context(RaiseRoutingContext) public fun Raise<RequestError>.pathIntOrRaise(name: String): Int = call.pathIntOrRaise(name)

// Raise+RoutingContext
context(RoutingContext) public fun Raise<RequestError>.pathOrRaise(name: String) = call.pathOrRaise(name)
context(RoutingContext) public inline fun <reified A : Any> Raise<RequestError>.pathOrRaise(name: String): A = call.pathOrRaise<A>(name)
context(RoutingContext) public inline fun <A : Any> Raise<RequestError>.pathOrRaise(name: String, transform: Raise<String>.(String) -> A): A = call.pathOrRaise(name, transform)
context(RoutingContext) public fun Raise<RequestError>.pathIntOrRaise(name: String): Int = call.pathIntOrRaise(name)

// RaiseRoutingContext (default error response)
public fun RaiseRoutingContext.pathOrRaise(name: String): String = errorRaise.pathOrRaise(name) { it }
@JvmName("pathOrRaiseReified")
public inline fun <reified A : Any> RaiseRoutingContext.pathOrRaise(name: String): String = errorRaise.parameterOrRaise(call.pathParameters, Parameter.Path(name), typeInfo<A>())
public inline fun <A : Any> RaiseRoutingContext.pathOrRaise(name: String, transform: Raise<String>.(String) -> A): A = errorRaise.parameterOrRaise(call.pathParameters, Parameter.Path(name), transform)

// RaiseRoutingContext (custom error response)
public inline fun RaiseRoutingContext.pathOrRaise(name: String, errorResponse: (RequestError) -> Response): String = pathOrRaise(name, errorResponse) { it }
@JvmName("pathOrRaiseReified")
public inline fun <reified A : Any> RaiseRoutingContext.pathOrRaise(name: String, errorResponse: (RequestError) -> Response): String = withError(errorResponse) { parameterOrRaise(call.pathParameters, Parameter.Path(name), typeInfo<A>()) }
public inline fun <A : Any> RaiseRoutingContext.pathOrRaise(name: String, errorResponse: (RequestError) -> Response, transform: Raise<String>.(String) -> A): A =  withError(errorResponse) { errorRaise.parameterOrRaise(call.pathParameters, Parameter.Path(name), transform) }
