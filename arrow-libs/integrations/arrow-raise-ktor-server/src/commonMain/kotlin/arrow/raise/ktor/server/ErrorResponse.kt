package arrow.raise.ktor.server

import arrow.core.NonEmptyList
import arrow.core.nel
import arrow.raise.ktor.server.request.RequestError
import arrow.raise.ktor.server.request.toSimpleMessage
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.content.TextContent
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.RouteScopedPlugin
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.util.AttributeKey
import io.ktor.util.Attributes

private typealias ErrorsResponse = (NonEmptyList<RequestError>) -> Response

private val errorsResponseKey = AttributeKey<ErrorsResponse>("ktor-raise-error-response")

internal var Attributes.errorsResponse: ErrorsResponse
  get() = getOrNull(errorsResponseKey) ?: ::defaultErrorsResponse
  private set(value) {
    put(errorsResponseKey, value)
  }

@PublishedApi
internal fun ApplicationCall.errorResponse(error: RequestError): Response = attributes.errorsResponse(error.nel())

@PublishedApi
internal fun ApplicationCall.errorsResponse(errors: NonEmptyList<RequestError>): Response = attributes.errorsResponse(errors)

public class RaiseErrorResponseConfig(
  public var errorResponse: ErrorsResponse = ::defaultErrorsResponse,
) {
  public fun errorResponse(response: (NonEmptyList<RequestError>) -> Response) {
    errorResponse = response
  }
}

public val RaiseErrorResponse: RouteScopedPlugin<RaiseErrorResponseConfig> = createRouteScopedPlugin(
  name = "RequestLoggingPlugin",
  createConfiguration = { RaiseErrorResponseConfig() }
) {
  onCall { call ->
    call.attributes.errorsResponse = pluginConfig.errorResponse
  }
}

private fun defaultErrorsResponse(errors: NonEmptyList<RequestError>): Response =
  Response.Companion.raw(
    TextContent(
      text = errors.joinToString("\n") { it.toSimpleMessage() },
      contentType = ContentType.Text.Plain,
      status = BadRequest,
    ),
  )
