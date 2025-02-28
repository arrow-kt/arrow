package arrow.raise.ktor.server.request

import arrow.core.NonEmptyList
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.Raise
import arrow.core.raise.RaiseAccumulate
import arrow.core.raise.recover
import arrow.raise.ktor.server.RaiseRoutingContext
import arrow.raise.ktor.server.Response
import arrow.raise.ktor.server.errorResponse
import io.ktor.server.routing.*
import io.ktor.util.reflect.*

public class CallValidationContext @PublishedApi internal constructor(
  @PublishedApi
  internal val call: RoutingCall,
  raise: Raise<NonEmptyList<RequestError>>,
) : RaiseAccumulate<RequestError>(raise) {
  public val pathAccumulating: ParameterDelegateProvider =
    object : AccumulatingParameterProvider(this, call.pathParameters) {
      override fun parameter(name: String) = Parameter.Path(name)
    }

  public val queryAccumulating: ParameterDelegateProvider =
    object : AccumulatingParameterProvider(this, call.queryParameters) {
      override fun parameter(name: String) = Parameter.Query(name)
    }

  @ExperimentalRaiseAccumulateApi
  public suspend inline fun <reified A : Any> receiveAccumulating(): Value<A> =
    accumulating { receiveOrRaise(call, typeInfo<A>()) }

  @ExperimentalRaiseAccumulateApi
  public suspend inline fun <reified A : Any> receiveNullableAccumulating(): Value<A?> =
    accumulating { receiveNullableOrRaise(call, typeInfo<A>()) }
}

@ExperimentalRaiseAccumulateApi
public inline fun <A> RaiseRoutingContext.validate(
  transform: (errors: NonEmptyList<RequestError>) -> Response = call::errorResponse,
  block: CallValidationContext.() -> A,
): A = call.validate(block) { raise(transform(it)) }

@ExperimentalRaiseAccumulateApi
public inline fun <A> RoutingCall.validate(
  block: CallValidationContext.() -> A,
  recover: (errors: NonEmptyList<RequestError>) -> A,
): A =
  recover({
    block(CallValidationContext(this@validate, this))
  }, recover)

