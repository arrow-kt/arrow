package arrow.raise.ktor.server

import arrow.core.nel
import arrow.core.raise.Raise
import arrow.raise.ktor.server.request.Parameter
import arrow.raise.ktor.server.request.ParameterDelegateProvider
import arrow.raise.ktor.server.request.RaisingParameterProvider
import arrow.raise.ktor.server.request.RequestError
import arrow.raise.ktor.server.request.parameterOrRaise
import arrow.raise.ktor.server.request.pathOrRaise
import arrow.raise.ktor.server.request.queryOrRaise
import arrow.raise.ktor.server.request.receiveNullableOrRaise
import arrow.raise.ktor.server.request.receiveOrRaise
import io.ktor.server.routing.*
import io.ktor.util.reflect.typeInfo
import kotlinx.io.files.Path
import kotlin.jvm.JvmName

public class RaiseRoutingContext(
  private val raise: Raise<Response>,
  private val routingContext: RoutingContext,
) : Raise<Response> by raise {

  @PublishedApi
  internal val errorRaise: Raise<RequestError> =
    object : Raise<RequestError> {
      override fun raise(requestError: RequestError): Nothing = raise(call.errorResponse(requestError.nel()))
    }

  public val call: RoutingCall get() = routingContext.call

  public val pathRaising: ParameterDelegateProvider =
    object : RaisingParameterProvider(errorRaise, call.pathParameters) {
      override fun parameter(name: String) = Parameter.Path(name)
    }

  public val queryRaising: ParameterDelegateProvider =
    object : RaisingParameterProvider(errorRaise, call.queryParameters) {
      override fun parameter(name: String) = Parameter.Query(name)
    }

  @JvmName("pathOrRaiseReified")
  public inline fun <reified A : Any> Raise<RequestError>.pathOrRaise(name: String): A = pathOrRaise<A>(call, name)
  public inline fun <A : Any> Raise<RequestError>.pathOrRaise(name: String, transform: Raise<String>.(String) -> A): A = pathOrRaise(call, name, transform)
  public fun Raise<RequestError>.pathOrRaise(name: String): String = pathOrRaise(call, name)

  @JvmName("queryOrRaiseReified")
  public inline fun <reified A : Any> Raise<RequestError>.queryOrRaise(name: String): A = queryOrRaise<A>(call, name)
  public inline fun <A : Any> Raise<RequestError>.queryOrRaise(name: String, transform: Raise<String>.(String) -> A): A = queryOrRaise(call, name, transform)
  public fun Raise<RequestError>.queryOrRaise(name: String): String = queryOrRaise(call, name)

  public suspend inline fun <reified A : Any> Raise<RequestError>.receiveOrRaise(): A = receiveOrRaise(call)
  public suspend inline fun <reified A : Any> Raise<RequestError>.receiveNullableOrRaise(): A? = receiveNullableOrRaise(call)
}
