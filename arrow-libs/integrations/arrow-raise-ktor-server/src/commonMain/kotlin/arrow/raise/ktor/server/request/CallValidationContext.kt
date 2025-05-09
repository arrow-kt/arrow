@file:OptIn(ExperimentalContracts::class)

package arrow.raise.ktor.server.request

import arrow.core.NonEmptyList
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.Raise
import arrow.core.raise.RaiseAccumulate
import arrow.core.raise.recover
import arrow.raise.ktor.server.RaiseRoutingContext
import arrow.raise.ktor.server.Response
import arrow.raise.ktor.server.errorsResponse
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract

public class CallValidationContext @PublishedApi internal constructor(
  @PublishedApi
  internal val call: RoutingCall,
  raise: Raise<NonEmptyList<RequestError>>,
) : RaiseAccumulate<RequestError>(raise) {
  public val pathAccumulating: AccumulatingParameterProvider = call.pathParameters.delegate(Parameter::Path)
  public val queryAccumulating: AccumulatingParameterProvider = call.queryParameters.delegate(Parameter::Query)
  public suspend fun formParametersDelegate(): AccumulatingParameterProvider = receiveOrRaise<Parameters>(call).delegate(Parameter::Form)

  @ExperimentalRaiseAccumulateApi
  public suspend inline fun <reified A : Any> receiveAccumulating(): Value<A> =
    accumulating { receiveOrRaise(call, typeInfo<A>()) }

  @ExperimentalRaiseAccumulateApi
  public suspend inline fun <reified A : Any> receiveNullableAccumulating(): Value<A?> =
    accumulating { receiveNullableOrRaise(call, typeInfo<A>()) }

  private inline fun Parameters.delegate(crossinline parameter: (String) -> Parameter) =
    AccumulatingParameterProvider(this@CallValidationContext, this, parameter)
}

@ExperimentalRaiseAccumulateApi
public inline fun <A> RaiseRoutingContext.validate(
  transform: (errors: NonEmptyList<RequestError>) -> Response = call::errorsResponse,
  block: CallValidationContext.() -> A,
): A {
  contract {
    callsInPlace(block, AT_MOST_ONCE)
    callsInPlace(transform, AT_MOST_ONCE)
  }
  return call.validate({ raise(transform(it)) }, block)
}

@ExperimentalRaiseAccumulateApi
public inline fun <A> RoutingCall.validate(
  recover: (errors: NonEmptyList<RequestError>) -> A,
  block: CallValidationContext.() -> A,
): A {
  contract {
    callsInPlace(block, AT_MOST_ONCE)
    callsInPlace(recover, AT_MOST_ONCE)
  }
  return recover({
    block(CallValidationContext(this@validate, this@recover))
  }, recover)
}
