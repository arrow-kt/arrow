package arrow.retrofit.adapter.either

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import okhttp3.Request
import okhttp3.ResponseBody
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.Type

internal class ArrowEitherCallAdapter<E, R>(
  retrofit: Retrofit,
  errorType: Type,
  private val bodyType: Type,
) : CallAdapter<R, Call<Either<E, R>>> {

  private val errorConverter: Converter<ResponseBody, E> =
    retrofit.responseBodyConverter(errorType, arrayOfNulls(0))

  override fun adapt(call: Call<R>): Call<Either<E, R>> = EitherCall(call, errorConverter, bodyType)

  override fun responseType(): Type = bodyType

  class EitherCall<E, R>(
    private val original: Call<R>,
    private val errorConverter: Converter<ResponseBody, E>,
    private val bodyType: Type,
  ) : Call<Either<E, R>> {

    override fun enqueue(callback: Callback<Either<E, R>>) {
      original.enqueue(object : Callback<R> {

        override fun onFailure(call: Call<R>, t: Throwable) {
          callback.onFailure(this@EitherCall, t)
        }

        override fun onResponse(call: Call<R>, response: Response<R>) {
          onResponseFn(
            callback,
            this@EitherCall,
            errorConverter,
            bodyType,
            response,
            { body, _ ->
              Response.success(response.code(), body.right())
            },
            { errorBody, _ ->
              Response.success(errorBody.left())
            },
          )
        }
      })
    }

    override fun isExecuted(): Boolean = original.isExecuted

    override fun timeout(): Timeout = original.timeout()

    override fun clone(): Call<Either<E, R>> = EitherCall(original.clone(), errorConverter, bodyType)

    override fun isCanceled(): Boolean = original.isCanceled

    override fun cancel() = original.cancel()

    override fun execute(): Response<Either<E, R>> =
      throw UnsupportedOperationException("This adapter does not support sync execution")

    override fun request(): Request = original.request()
  }
}
