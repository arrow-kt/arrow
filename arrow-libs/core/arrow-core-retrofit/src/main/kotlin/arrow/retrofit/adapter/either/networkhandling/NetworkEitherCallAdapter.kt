package arrow.retrofit.adapter.either.networkhandling

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.TimeoutException

/**
 * Provides a [CallAdapter] wrapping the call result into an [Either].
 *
 * The adapter is opinionated regarding the returned error type which is [CallError].
 * This error type is a sealed class hierarchy mapping different communication failure types:
 * * HTTP error responses (4xx, 5xx)
 * * network errors (device has no network)
 * * timeout errors
 * * other unexpected errors
 *
 * This way the adapter can avoid throwing exceptions in these cases.
 *
 * A Retrofit API definition using this adapter has to adhere to the following pattern (where T is the expected response type):
 * ```
 * @GET(".")
 * suspend fun getSomeData(): Either<CallError, T>
 * ```
 *
 * If the result is a success (response code 2xx) and has a response body then the (parsed) response body is
 * returned wrapped into a [Right].
 *
 * If you expect a response with no response body (e.g. 204 OK) make sure to define your call as returning [Unit] in the success case.
 * Otherwise the call will return a [Left] signifying error.
 * ```
 * @POST("light/switch")
 * suspend fun switchLight(@Body switchPosition: SwitchPosition): Either<CallError, Unit>
 * ```
 */
public class NetworkEitherCallAdapterFactory : CallAdapter.Factory() {

  override fun get(
    returnType: Type,
    annotations: Array<Annotation>,
    retrofit: Retrofit
  ): CallAdapter<*, *>? {
    if (getRawType(returnType) != Call::class.java) return null
    require(returnType is ParameterizedType) { "Return type must be a parameterized type." }

    val responseType = getParameterUpperBound(0, returnType)
    if (getRawType(responseType) != Either::class.java) return null
    require(responseType is ParameterizedType) { "Response type must be a parameterized type." }

    val leftType = getParameterUpperBound(0, responseType)
    if (getRawType(leftType) != CallError::class.java) return null

    val rightType = getParameterUpperBound(1, responseType)
    return EitherCallAdapter<Any?>(rightType)
  }
}

private class EitherCallAdapter<R>(
  private val successType: Type
) : CallAdapter<R, Call<Either<CallError, R?>>> {

  override fun adapt(call: Call<R?>): Call<Either<CallError, R?>> = EitherCall(call, successType)

  override fun responseType(): Type = successType
}

private class EitherCall<R>(
  private val delegate: Call<R>,
  private val successType: Type
) : Call<Either<CallError, R>> {

  override fun enqueue(callback: Callback<Either<CallError, R>>) = delegate.enqueue(
    object : Callback<R> {

      override fun onResponse(call: Call<R>, response: Response<R>) {
        callback.onResponse(this@EitherCall, Response.success(response.toEither()))
      }

      private fun Response<R>.toEither(): Either<CallError, R> {
        // Http error response (4xx - 5xx)
        if (!isSuccessful) {
          val errorBody = errorBody()?.string() ?: ""
          return Left(HttpError(code(), errorBody))
        }

        // Http success response with body
        body()?.let { body -> return Right(body) }

        // if we defined Unit as success type it means we expected no response body
        // e.g. in case of 204 No Content
        return if (successType == Unit::class.java) {
          @Suppress("UNCHECKED_CAST")
          Right(Unit) as Either<CallError, R>
        } else {
          Left(UnexpectedCallError("Response body was null"))
        }
      }

      override fun onFailure(call: Call<R?>, throwable: Throwable) {
        val error = when (throwable) {
          is TimeoutException -> TimeoutError(throwable)
          is IOException -> NetworkError(throwable)
          else -> UnexpectedCallError(throwable)
        }
        callback.onResponse(this@EitherCall, Response.success(Left(error)))
      }
    }
  )

  override fun timeout(): Timeout = delegate.timeout()

  override fun isExecuted(): Boolean = delegate.isExecuted

  override fun clone(): Call<Either<CallError, R>> = EitherCall(delegate.clone(), successType)

  override fun isCanceled(): Boolean = delegate.isCanceled

  override fun cancel() = delegate.cancel()

  override fun execute(): Response<Either<CallError, R>> =
    throw UnsupportedOperationException("This adapter does not support sync execution")

  override fun request(): Request = delegate.request()
}
