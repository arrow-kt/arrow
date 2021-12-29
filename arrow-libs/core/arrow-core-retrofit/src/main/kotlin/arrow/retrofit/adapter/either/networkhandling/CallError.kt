package arrow.retrofit.adapter.either.networkhandling

/**
 * Error hierarchy when calling remote server.
 */
public sealed class CallError

/**
 * Http request returned an error response.
 */
public data class HttpError(val code: Int, val body: String) : CallError()

/**
 * The request timed out.
 */
public data class TimeoutError(val cause: Throwable) : CallError()

/**
 * IO error: network error, malformed JSON etc. Check the [cause] for details.
 */
public data class IOError(val cause: Throwable) : CallError()

/**
 * Unexpected API error.
 */
public data class UnexpectedCallError(val cause: Throwable) : CallError()
