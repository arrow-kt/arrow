package arrow.raise.ktor.server.request

public sealed interface RequestError

public data class MissingParameter(
  public val parameter: Parameter,
) : RequestError

public data class Malformed(
  public val component: RequestComponent,
  public val message: String,
  public val cause: Throwable? = null,
) : RequestError

public fun RequestError.toSimpleMessage(): String =
  when (this) {
    is MissingParameter -> "Missing ${parameter.describe()}."
    is Malformed -> "Malformed ${component.describe()} $message${cause?.message?.let { ": $it" } ?: ""}"
  }

private fun RequestComponent.describe(): String =
  when (this) {
    is Parameter.Path -> "path parameter '$name'"
    is Parameter.Query -> "query parameter '$name'"
    is Parameter.Form -> "form parameter '$name'"
    is ReceiveBody -> "body"
  }
