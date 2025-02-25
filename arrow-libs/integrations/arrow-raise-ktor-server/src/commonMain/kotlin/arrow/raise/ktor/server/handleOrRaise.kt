package arrow.raise.ktor.server

import arrow.core.raise.Raise
import arrow.core.raise.RaiseDSL
import arrow.core.raise.recover
import arrow.raise.ktor.server.Response.Companion.Response
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.content.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*

public typealias RaiseRoutingHandler = suspend RaiseRoutingContext.() -> Unit

@KtorDsl
@RaiseDSL
public fun Route.handleOrRaise(body: RaiseRoutingHandler): Unit = handle { handleOrRaise { body() } }

@PublishedApi
internal suspend inline fun RoutingContext.handleOrRaise(body: RaiseRoutingContext.() -> Unit): Unit = recover(
  block = raise@{ RaiseRoutingContext(this@raise, this@handleOrRaise).body() },
  recover = { it.respondTo(call) },
)

@RaiseDSL
public fun Raise<Response>.raise(outgoingContent: OutgoingContent): Nothing = raise(Response(outgoingContent))

@RaiseDSL
public fun Raise<Response>.raise(statusCode: HttpStatusCode): Nothing = raise(Response(statusCode))

@RaiseDSL
public inline fun <reified T> Raise<Response>.raise(statusCode: HttpStatusCode, content: T): Nothing = raise(Response(statusCode, content))

@RaiseDSL
public fun Raise<Response>.raiseNotFound(): Nothing = raise(NotFound)

@RaiseDSL
public inline fun <reified T> Raise<Response>.raiseNotFound(content: T): Nothing = raise(NotFound, content)

@RaiseDSL
public fun Raise<Response>.raiseBadRequest(): Nothing = raise(BadRequest)

@RaiseDSL
public inline fun <reified T> Raise<Response>.raiseBadRequest(content: T): Nothing = raise(BadRequest, content)

public class RaiseRoutingContext(
  private val raise: Raise<Response>,
  private val routingContext: RoutingContext,
) : Raise<Response> by raise {
  public val call: RoutingCall get() = routingContext.call // TODO: custom wrapped RoutingCall-alike that requires opt-in for direct response calls (as they're non-terminal)
}

