package arrow.raise.ktor.server

import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*

internal typealias KtorRoutingHandler = RoutingHandler
// this is missing in Ktor - they have a RouteHandler typealias, but no "receiving" equivalent
internal typealias KtorReceivingRoutingHandler<T> = suspend RoutingContext.(T) -> Unit

internal typealias RespondOrRaiseHandler<Response> = suspend RaiseRoutingContext.() -> Response

// due to compilation ambiguity between n-ary lambdas on function resolution, by using this SAM on the API it's resolved with a lower priority
// which mitigates the ambiguity of `handler { }` vs `handler { it -> }`
public fun interface ReceivingRespondOrRaiseHandler<Request, Response> {
  public suspend fun RaiseRoutingContext.handle(request: Request): Response
}

@PublishedApi
internal  inline fun <reified Response> RespondOrRaiseHandler<Response>.asKtorHandler(statusCode: HttpStatusCode?) =
  asKtorHandler(statusCode, typeInfo<Response>())

@PublishedApi
internal fun <Response> RespondOrRaiseHandler<Response>.asKtorHandler(
  statusCode: HttpStatusCode?, typeInfo: TypeInfo,
): KtorRoutingHandler = { respondOrRaise(statusCode, typeInfo, ::invoke) }

@PublishedApi
internal  inline fun <Request, reified Response> ReceivingRespondOrRaiseHandler<Request, Response>.asKtorHandler(statusCode: HttpStatusCode?) =
  asKtorHandler(statusCode, typeInfo<Response>())

@PublishedApi
internal fun <Request, Response> ReceivingRespondOrRaiseHandler<Request, Response>.asKtorHandler(
  statusCode: HttpStatusCode?, typeInfo: TypeInfo
): KtorReceivingRoutingHandler<Request> = { respondOrRaise(statusCode, typeInfo) { handle(it) } }

