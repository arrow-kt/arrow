package arrow.raise.ktor.server

import arrow.core.raise.Raise
import arrow.core.raise.RaiseDSL
import arrow.core.raise.recover
import arrow.raise.ktor.server.RoutingResponse.Companion.RoutingResponse
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.content.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import io.ktor.utils.io.*

public sealed interface RoutingResponse {
  public companion object {
    @PublishedApi
    internal fun RoutingResponse(statusCode: HttpStatusCode, value: Any?, typeInfo: TypeInfo): RoutingResponse = TypedResponse(statusCode, value, typeInfo)

    public inline fun <reified T> RoutingResponse(statusCode: HttpStatusCode, value: T): RoutingResponse = RoutingResponse(statusCode, value, typeInfo<T>())

    public fun RoutingResponse(statusCode: HttpStatusCode): RoutingResponse = EmptyResponse(statusCode)

    public fun RoutingResponse(outgoingContent: OutgoingContent): RoutingResponse = RawResponse(outgoingContent)

    public inline operator fun <reified T: Any> HttpStatusCode.invoke(payload: T) = RoutingResponse(this, payload, typeInfo<T>())
  }
}

internal data class EmptyResponse(val statusCode: HttpStatusCode) : RoutingResponse

@PublishedApi
internal data class TypedResponse(val statusCode: HttpStatusCode = HttpStatusCode.OK, val content: Any?, val typeInfo: TypeInfo) : RoutingResponse

internal data class RawResponse(val outgoingContent: OutgoingContent) : RoutingResponse

@RaiseDSL
public fun Raise<RoutingResponse>.raise(outgoingContent: OutgoingContent): Nothing = raise(RoutingResponse(outgoingContent))

@RaiseDSL
public fun Raise<RoutingResponse>.raise(statusCode: HttpStatusCode): Nothing = raise(RoutingResponse(statusCode))

@RaiseDSL
public inline fun <reified T> Raise<RoutingResponse>.raise(statusCode: HttpStatusCode, content: T): Nothing = raise(RoutingResponse(statusCode, content))

@RaiseDSL
public fun Raise<RoutingResponse>.raiseNotFound(): Nothing = raise(NotFound)

@RaiseDSL
public inline fun <reified T> Raise<RoutingResponse>.raiseNotFound(content: T): Nothing = raise(NotFound, content)

@RaiseDSL
public fun Raise<RoutingResponse>.raiseBadRequest(): Nothing = raise(BadRequest)

@RaiseDSL
public inline fun <reified T> Raise<RoutingResponse>.raiseBadRequest(content: T): Nothing = raise(BadRequest, content)

public class RaiseRoutingContext(
  private val raise: Raise<RoutingResponse>,
  private val routingContext: RoutingContext,
) : Raise<RoutingResponse> by raise {
  public val call: RoutingCall get() = routingContext.call // TODO: custom wrapped RoutingCall-alike that requires opt-in for direct response calls (as they're non-terminal)
}

public typealias RaiseRoutingHandler = suspend RaiseRoutingContext.() -> Unit

@KtorDsl
@RaiseDSL
public fun Route.handleOrRaise(body: RaiseRoutingHandler): Unit =
  handle { handleOrRaise { body() } }

@PublishedApi
internal suspend inline fun RoutingContext.handleOrRaise(body: RaiseRoutingContext.() -> Unit): Unit = recover(
  block = { RaiseRoutingContext(this@recover, this@handleOrRaise).body() },
  recover = { call.respond(it) },
)

@PublishedApi
internal suspend fun RoutingCall.respond(routingResponse: RoutingResponse) {
  when (routingResponse) {
    is EmptyResponse -> respond(HttpStatusCodeContent(routingResponse.statusCode), null)
    is TypedResponse -> respond(routingResponse.statusCode, routingResponse.content, routingResponse.typeInfo)
    is RawResponse -> respond(routingResponse.outgoingContent, null)
  }
}
