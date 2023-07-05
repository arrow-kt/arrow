package arrow.retrofit.adapter.either.networkhandling

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.reflect.Type

internal class NetworkEitherCallAdapter<R>(
  private val successType: Type,
) : CallAdapter<R, Call<Either<CallError, R?>>> {

  override fun adapt(call: Call<R?>): Call<Either<CallError, R?>> = EitherCall(call, successType)

  override fun responseType(): Type = successType
}

private class EitherCall<R>(
  private val delegate: Call<R>,
  private val successType: Type,
) : Call<Either<CallError, R>> {

  override fun enqueue(callback: Callback<Either<CallError, R>>) = delegate.enqueue(
    object : Callback<R> {

      override fun onResponse(call: Call<R>, response: Response<R>) {
        callback.onResponse(this@EitherCall, Response.success(response.toEither()))
      }

      private fun Response<R>.toEither(): Either<CallError, R> {
        // Http error response (4xx - 5xx)
        if (!isSuccessful) {
          val errorBody = errorBody()!!.string()
          return HttpError(
            code = code(),
            message = message(),
            body = errorBody,
          ).left()
        }

        // Http success response with body
        body()?.let { body -> return body.right() }

        // if we defined Unit as success type it means we expected no response body
        // e.g. in case of 204 No Content
        return if (successType == Unit::class.java) {
          @Suppress("UNCHECKED_CAST")
          Unit.right() as Either<CallError, R>
        } else {
          UnexpectedCallError(
            IllegalStateException(
              "Response code is ${code()} but body is null.\n" +
                "If you expect response body to be null then define your API method as returning Unit:\n" +
                "@POST fun postSomething(): Either<CallError, Unit>",
            ),
          ).left()
        }
      }

      override fun onFailure(call: Call<R?>, throwable: Throwable) {
        val error = if (throwable is IOException) {
          IOError(throwable)
        } else {
          UnexpectedCallError(throwable)
        }
        callback.onResponse(this@EitherCall, Response.success(error.left()))
      }
    },
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
