package arrow.raise.ktor.server.request

public sealed interface RequestError

public class Missing<out C : Parameter>(
  public val parameter: C,
) : RequestError

public class Malformed<out C : RequestComponent>(
  public val component: C,
  public val message: String,
  public val cause: Throwable? = null,
) : RequestError

public interface UnhandledError : RequestError {
  public val message: String
}

public fun RequestError.toSimpleMessage(): String =
  when (this) {
    is Missing<*> -> "Missing ${parameter.describe()}."
    is Malformed<*> -> "Malformed ${component.describe()}: $message"
    is UnhandledError -> message
  }

private fun RequestComponent.describe(): String =
  when (this) {
    is Parameter.Path -> "path parameter '$name'"
    is Parameter.Query -> "query parameter '$name'"
    is ReceiveBody -> "body"
  }
