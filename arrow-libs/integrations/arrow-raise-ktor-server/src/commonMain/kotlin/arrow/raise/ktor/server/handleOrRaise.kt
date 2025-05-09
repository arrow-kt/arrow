package arrow.raise.ktor.server

import arrow.core.raise.Raise
import arrow.core.raise.RaiseDSL
import arrow.core.raise.recover
import arrow.raise.ktor.server.Response.Companion.Response
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*

public typealias RaiseRoutingHandler = suspend RaiseRoutingContext.() -> Unit

@KtorDsl
@RaiseDSL
public fun Route.handleOrRaise(body: RaiseRoutingHandler): Unit = handle { handleOrRaise { body() } }

@PublishedApi
internal suspend inline fun RoutingContext.handleOrRaise(body: RaiseRoutingContext.() -> Unit): Unit =
  recover({ RaiseRoutingContext(this, this@handleOrRaise).body() }, { it: Response -> it.respondTo(call) })

@RaiseDSL
public fun Raise<Response>.raise(outgoingContent: OutgoingContent): Nothing = raise(Response(outgoingContent))

@RaiseDSL
public fun Raise<Response>.raise(statusCode: HttpStatusCode): Nothing = raise(Response(statusCode))

@RaiseDSL
public inline fun <reified T> Raise<Response>.raise(statusCode: HttpStatusCode, content: T): Nothing = raise(Response(statusCode, content))
