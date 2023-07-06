package arrow.retrofit.adapter.either

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

internal class ArrowResponseECallAdapter<E, R>(
  retrofit: Retrofit,
  errorType: Type,
  private val bodyType: Type,
) : CallAdapter<R, Call<ResponseE<E, R>>> {

  private val errorConverter: Converter<ResponseBody, E> =
    retrofit.responseBodyConverter(errorType, arrayOfNulls(0))

  override fun adapt(call: Call<R>): Call<ResponseE<E, R>> = ResponseECall(call, errorConverter, bodyType)

  override fun responseType(): Type = bodyType

  class ResponseECall<E, R>(
    private val original: Call<R>,
    private val errorConverter: Converter<ResponseBody, E>,
    private val bodyType: Type,
  ) : Call<ResponseE<E, R>> {

    override fun enqueue(callback: Callback<ResponseE<E, R>>) {
      original.enqueue(object : Callback<R> {

        override fun onFailure(call: Call<R>, t: Throwable) {
          callback.onFailure(this@ResponseECall, t)
        }

        override fun onResponse(call: Call<R>, response: Response<R>) {
          onResponseFn(
            callback,
            this@ResponseECall,
            errorConverter,
            bodyType,
            response,
            { body, responseT ->
              Response.success(responseT.code(), ResponseE(responseT.raw(), body.right()))
            },
            { errorBody, responseV ->
              Response.success(ResponseE(responseV.raw(), errorBody.left()))
            },
          )
        }
      })
    }

    override fun isExecuted(): Boolean = original.isExecuted

    override fun timeout(): Timeout = original.timeout()

    override fun clone(): Call<ResponseE<E, R>> = ResponseECall(original.clone(), errorConverter, bodyType)

    override fun isCanceled(): Boolean = original.isCanceled

    override fun cancel() = original.cancel()

    override fun execute(): Response<ResponseE<E, R>> =
      throw UnsupportedOperationException("This adapter does not support sync execution")

    override fun request(): Request = original.request()
  }
}
