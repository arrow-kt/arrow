package arrow.raise.ktor.server

import arrow.core.nel
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.Raise
import arrow.core.raise.RaiseAccumulate
import arrow.raise.ktor.server.request.Parameter
import arrow.raise.ktor.server.request.ParameterTransform
import arrow.raise.ktor.server.request.RaisingParameterProvider
import arrow.raise.ktor.server.request.RequestError
import arrow.raise.ktor.server.request.pathOrRaise
import arrow.raise.ktor.server.request.queryOrRaise
import arrow.raise.ktor.server.request.receiveNullableOrRaise
import arrow.raise.ktor.server.request.receiveOrRaise
import io.ktor.http.*
import io.ktor.server.routing.*
import kotlin.jvm.JvmName

public class RaiseRoutingContext(
  private val raise: Raise<Response>,
  routingContext: RoutingContext,
) : CallRaiseContext(routingContext.call), Raise<Response> by raise {
  @PublishedApi
  internal val errorRaise: Raise<RequestError> =
    object : Raise<RequestError> {
      override fun raise(requestError: RequestError) = raise(call.errorsResponse(requestError.nel()))
    }

  public fun raise(requestError: RequestError): Nothing = errorRaise.raise(requestError)

  public val pathRaising: RaisingParameterProvider = call.pathParameters.delegate(Parameter::Path)
  public val queryRaising: RaisingParameterProvider = call.queryParameters.delegate(Parameter::Query)
  public suspend fun formParametersDelegate(): RaisingParameterProvider =
    errorRaise.receiveOrRaise<Parameters>(call).delegate(Parameter::Form)

  private fun Parameters.delegate(parameter: (String) -> Parameter) = RaisingParameterProvider(errorRaise, this, parameter)
}

/** Temporary intersection type, until we have context parameters */
public open class CallRaiseContext internal constructor(public val call: RoutingCall) {
  // <editor-fold desc="raising extensions">
  @JvmName("pathOrRaiseReified")
  public inline fun <reified A : Any> Raise<RequestError>.pathOrRaise(name: String): A = pathOrRaise<A>(call, name)
  public inline fun <A : Any> Raise<RequestError>.pathOrRaise(name: String, transform: ParameterTransform<A>): A = pathOrRaise(call, name, transform)
  public fun Raise<RequestError>.pathOrRaise(name: String): String = pathOrRaise(call, name)

  @JvmName("queryOrRaiseReified")
  public inline fun <reified A : Any> Raise<RequestError>.queryOrRaise(name: String): A = queryOrRaise<A>(call, name)
  public inline fun <A : Any> Raise<RequestError>.queryOrRaise(name: String, transform: ParameterTransform<A>): A = queryOrRaise(call, name, transform)
  public fun Raise<RequestError>.queryOrRaise(name: String): String = queryOrRaise(call, name)

  public suspend inline fun <reified A : Any> Raise<RequestError>.receiveOrRaise(): A = receiveOrRaise(call)
  public suspend inline fun <reified A : Any> Raise<RequestError>.receiveNullableOrRaise(): A? = receiveNullableOrRaise(call)
  // </editor-fold>

  // <editor-fold desc="accumulating extensions">
  @ExperimentalRaiseAccumulateApi
  @JvmName("pathOrAccumulateReified")
  public inline fun <reified A : Any> RaiseAccumulate<RequestError>.pathOrAccumulate(name: String): RaiseAccumulate.Value<A> = accumulating { pathOrRaise<A>(call, name) }

  @ExperimentalRaiseAccumulateApi
  public inline fun <A : Any> RaiseAccumulate<RequestError>.pathOrAccumulate(name: String, transform: ParameterTransform<A>): RaiseAccumulate.Value<A> = accumulating { pathOrRaise(call, name, transform) }

  @ExperimentalRaiseAccumulateApi
  public fun RaiseAccumulate<RequestError>.pathOrAccumulate(name: String): RaiseAccumulate.Value<String> = accumulating { pathOrRaise(call, name) }

  @ExperimentalRaiseAccumulateApi
  @JvmName("queryOrAccumulateReified")
  public inline fun <reified A : Any> RaiseAccumulate<RequestError>.queryOrAccumulate(name: String): RaiseAccumulate.Value<A> = accumulating { queryOrRaise<A>(call, name) }

  @ExperimentalRaiseAccumulateApi
  public inline fun <A : Any> RaiseAccumulate<RequestError>.queryOrAccumulate(name: String, transform: ParameterTransform<A>): RaiseAccumulate.Value<A> = accumulating { queryOrRaise(call, name, transform) }

  @ExperimentalRaiseAccumulateApi
  public fun RaiseAccumulate<RequestError>.queryOrAccumulate(name: String): RaiseAccumulate.Value<String> = accumulating { queryOrRaise(call, name) }

  @ExperimentalRaiseAccumulateApi
  public suspend inline fun <reified A : Any> RaiseAccumulate<RequestError>.receiveOrAccumulate(): RaiseAccumulate.Value<A> = accumulating { receiveOrRaise(call) }

  @ExperimentalRaiseAccumulateApi
  public suspend inline fun <reified A : Any> RaiseAccumulate<RequestError>.receiveNullableOrAccumulate(): RaiseAccumulate.Value<A?> = accumulating { receiveNullableOrRaise(call) }
  // </editor-fold>
}
