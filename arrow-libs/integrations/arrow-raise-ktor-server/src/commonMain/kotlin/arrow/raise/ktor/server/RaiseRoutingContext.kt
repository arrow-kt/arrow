package arrow.raise.ktor.server

import arrow.core.raise.Raise
import arrow.raise.ktor.server.request.Parameter
import arrow.raise.ktor.server.request.ParameterDelegateProvider
import arrow.raise.ktor.server.request.RaisingParameterProvider
import arrow.raise.ktor.server.request.RequestError
import io.ktor.server.routing.*

public class RaiseRoutingContext(
  private val raise: Raise<Response>,
  private val routingContext: RoutingContext,
) : Raise<Response> by raise {

  @PublishedApi
  internal val errorRaise: Raise<RequestError> =
    object : Raise<RequestError> {
      override fun raise(r: RequestError): Nothing = raise(call.errorResponse(r))
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
}
