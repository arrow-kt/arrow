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

private typealias ErrorResponse = (NonEmptyList<RequestError>) -> Response

private val errorResponseKey = AttributeKey<ErrorResponse>("ktor-raise-error-response")

internal var Attributes.errorResponse: ErrorResponse
  get() = getOrNull(errorResponseKey) ?: ::defaultErrorsResponse
  private set(value) {
    put(errorResponseKey, value)
  }

@PublishedApi
internal fun ApplicationCall.errorResponse(errors: NonEmptyList<RequestError>): Response = attributes.errorResponse(errors)

public class RaiseErrorResponseConfig(
  public var errorResponse: ErrorResponse = ::defaultErrorsResponse,
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
    call.attributes.errorResponse = pluginConfig.errorResponse
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
