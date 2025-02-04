package arrow.raise.ktor.server

import arrow.core.NonEmptyList
import arrow.core.raise.Raise
import arrow.core.raise.ensureNotNull
import arrow.core.raise.recover
import arrow.core.raise.withError
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.RoutingContext
import io.ktor.util.reflect.*
import kotlin.reflect.KClass

context(Raise<OutgoingContent>)
public inline fun <A> validate(
  transform: (errors: NonEmptyList<String>) -> OutgoingContent = ::defaultBadRequest,
  block: RaiseAccumulate<String>.() -> A
): A = recover({
  block(RaiseAccumulate(this))
}) { errors -> raise(transform(errors)) }

public fun defaultBadRequest(errors: NonEmptyList<String>): TextContent =
  TextContent(
    errors.joinToString(),
    ContentType.Text.Plain,
    BadRequest
  )

//<editor-fold desc="Path Parameters">
/**
 * Get a _path_ parameter from the incoming request [ApplicationCall],
 * or raise a [HttpStatusCode.BadRequest] in case the path parameter is missing.
 */
context(RoutingContext)
public inline fun <A> Raise<String>.pathOrRaise(name: String, transform: Raise<String>.(String) -> A): A {
  val value = pathOrRaise(name)
  val resultOrNull = transform(value)
  return ensureNotNull(resultOrNull) {
    "Path parameter $name was found, but $resultOrNull was incorrectly formatted."
  }
}

context(RoutingContext)
public fun Raise<String>.pathOrRaise(name: String): String =
  pathOrRaise(name) { it }

context(RoutingContext)
public inline fun <A> Raise<OutgoingContent>.pathOrRaise(name: String, transform: Raise<String>.(String) -> A): A =
  withError({ s: String -> TextContent(s, ContentType.Text.Plain, BadRequest) }) {
    pathOrRaise(name, transform)
  }

context(RoutingContext)
public fun Raise<OutgoingContent>.pathOrRaise(name: String): String =
  pathOrRaise(name) { it }
//</editor-fold>

//<editor-fold desc="Query Parameters">
context(RoutingContext)
public inline fun <A> Raise<String>.queryOrRaise(
  name: String,
  transform: Raise<String>.(String) -> A
): A {
  val value = ensureNotNull(call.request.queryParameters[name]) {
    "Query parameter $name was expected."
  }
  return transform(value)
}

context(RoutingContext)
public fun Raise<String>.queryIntOrRaise(name: String): Int =
  queryOrRaise(name) { value ->
    ensureNotNull(value.toIntOrNull()) { "Expected $value to be a valid Int." }
  }

context(RoutingContext)
public fun Raise<String>.queryOrRaise(name: String): String =
  queryOrRaise(name) { it }

context(RoutingContext)
public inline fun <A : Any> Raise<OutgoingContent>.queryOrRaise(name: String, transform: Raise<String>.(String) -> A): A =
  withError({ s: String -> TextContent(s, ContentType.Text.Plain, BadRequest) }) {
    queryOrRaise(name, transform)
  }

context(RoutingContext)
public fun <A> Raise<OutgoingContent>.queryOrRaise(name: String): String =
  queryOrRaise(name) { it }
//</editor-fold>

context(RoutingContext)
public suspend fun <A : Any> Raise<String>.receiveOrRaise(type: KClass<A>): A =
  try {
    call.receive(type)
  } catch (e: ContentTransformationException) {
    raise(e.message ?: "Could not deserialize ${type.simpleName} from request body")
  }

context(RoutingContext)
public suspend inline fun <reified A : Any> Raise<String>.receiveOrRaise(): A =
  receiveOrRaise(A::class)

context(RoutingContext)
public suspend fun <A : Any> Raise<String>.receiveNullableOrRaise(type: TypeInfo): A? =
  try {
    call.receiveNullable(type)
  } catch (e: ContentTransformationException) {
    raise(e.message ?: "Could not deserialize ${type.type.simpleName} from request body")
  }
