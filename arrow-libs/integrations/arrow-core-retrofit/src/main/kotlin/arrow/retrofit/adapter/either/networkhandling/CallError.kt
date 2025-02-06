package arrow.retrofit.adapter.either.networkhandling

import java.io.IOException

/**
 * Error hierarchy when calling remote server.
 */
public sealed class CallError

/**
 * Http request returned an error response.
 */
public data class HttpError(
  val code: Int,
  val message: String,
  val body: String,
) : CallError()

/**
 * IO error: no network, socket timeout etc. Check the [cause] for details.
 */
public data class IOError(val cause: IOException) : CallError()

/**
 * Unexpected API error.
 */
public data class UnexpectedCallError(val cause: Throwable) : CallError()
