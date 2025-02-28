package arrow.raise.ktor.server

import arrow.core.NonEmptyList
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

private typealias ErrorResponse = (RequestError) -> Response
private typealias ErrorsResponse = (NonEmptyList<RequestError>) -> Response

private val errorResponseKey = AttributeKey<ErrorResponse>("ktor-raise-error-response")
private val errorsResponseKey = AttributeKey<ErrorsResponse>("ktor-raise-errors-response")

internal var Attributes.errorResponse: ErrorResponse
  get() = getOrNull(errorResponseKey) ?: ::defaultErrorResponse
  private set(value) {
    put(errorResponseKey, value)
  }

internal var Attributes.errorsResponse: ErrorsResponse
  get() = getOrNull(errorsResponseKey) ?: ::defaultErrorsResponse
  private set(value) {
    put(errorsResponseKey, value)
  }

@PublishedApi
internal fun ApplicationCall.errorResponse(errors: NonEmptyList<RequestError>): Response = attributes.errorsResponse(errors)

@PublishedApi
internal fun ApplicationCall.errorResponse(error: RequestError): Response = attributes.errorResponse(error)

public class RaiseErrorResponseConfig(
  public var errorResponse: ErrorResponse = ::defaultErrorResponse,
  public var errorsResponse: ErrorsResponse = ::defaultErrorsResponse,
)

public val RaiseErrorResponse: RouteScopedPlugin<RaiseErrorResponseConfig> = createRouteScopedPlugin(
  name = "RequestLoggingPlugin",
  createConfiguration = { RaiseErrorResponseConfig() }
) {
  onCall { call ->
    call.attributes.errorResponse = pluginConfig.errorResponse
    call.attributes.errorsResponse = pluginConfig.errorsResponse
  }
}

// <editor-fold desc="Default Response">
private fun defaultErrorsResponse(errors: NonEmptyList<RequestError>): Response =
  Response.Companion.raw(
    TextContent(
      text = errors.joinToString("\n") { it.toSimpleMessage() },
      contentType = ContentType.Text.Plain,
      status = BadRequest,
    ),
  )

private fun defaultErrorResponse(error: RequestError): Response =
  Response.Companion.raw(
    TextContent(
      text = error.toSimpleMessage(),
      contentType = ContentType.Text.Plain,
      status = BadRequest,
    ),
  )
// </editor-fold>
