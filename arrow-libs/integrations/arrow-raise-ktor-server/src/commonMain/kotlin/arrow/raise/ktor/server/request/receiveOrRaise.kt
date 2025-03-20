@file:JvmName("RequestRaise")
@file:JvmMultifileClass
@file:OptIn(ExperimentalContracts::class)

package arrow.raise.ktor.server.request

import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.raise.ktor.server.RaiseRoutingContext
import io.ktor.serialization.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public suspend inline fun <reified A : Any> Raise<RequestError>.receiveOrRaise(call: RoutingCall): A =
  receiveOrRaise(call, typeInfo<A>())

public suspend inline fun <reified A : Any> Raise<RequestError>.receiveNullableOrRaise(call: RoutingCall): A? =
  receiveNullableOrRaise(call, typeInfo<A>())

// RaiseRoutingContext (default error response)
public suspend inline fun <reified A : Any> RaiseRoutingContext.receiveOrRaise(): A =
  errorRaise.receiveOrRaise(call, typeInfo<A>())

public suspend inline fun <reified A : Any> RaiseRoutingContext.receiveNullableOrRaise(): A? =
  errorRaise.receiveNullableOrRaise(call, typeInfo<A>())

@PublishedApi
internal suspend fun <A : Any> Raise<Malformed>.receiveOrRaise(
  call: RoutingCall,
  typeInfo: TypeInfo,
): A = receiveOrRaise(typeInfo) { call.receive(it) }

@PublishedApi
internal suspend fun <A : Any> Raise<Malformed>.receiveNullableOrRaise(
  call: RoutingCall,
  typeInfo: TypeInfo,
): A? = receiveOrRaise(typeInfo) { call.receiveNullable(it) }

private inline fun <A> Raise<Malformed>.receiveOrRaise(
  typeInfo: TypeInfo,
  f: (TypeInfo) -> A
): A {
  contract { callsInPlace(f, AT_MOST_ONCE) }
  return catch({ f(typeInfo) }) {
    val cause = when (it) {
      is ContentTransformationException,
      is ContentConvertException -> it

      is BadRequestException -> it.cause ?: it // TODO: do we want to unwrap this?
      else -> throw it
    }
    raise(Malformed(ReceiveBody, "could not be deserialized to ${typeInfo.simpleName}", cause))
  }
}
