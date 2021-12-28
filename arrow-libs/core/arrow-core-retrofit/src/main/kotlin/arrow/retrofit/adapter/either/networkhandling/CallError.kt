package arrow.retrofit.adapter.either.networkhandling

/**
 * Error hierarchy when calling remote server.
 */
public sealed class CallError(public open val cause: Throwable?)

/**
 * Http request returned an error response.
 */
public data class HttpError(val code: Int, val body: String, override val cause: Throwable? = null) : CallError(cause)

/**
 * The request timed out.
 */
public data class TimeoutError(override val cause: Throwable? = null) : CallError(cause)

/**
 * IO error: network error, malformed JSON etc. Check the [cause] for details.
 */
public data class IOError(override val cause: Throwable) : CallError(cause)

/**
 * Unknown API error.
 */
public data class UnexpectedCallError(override val cause: Throwable) : CallError(cause) {

  internal constructor(message: String) : this(Exception(message))
}
