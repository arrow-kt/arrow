package arrow.integrations.retrofit.adapter.either

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response

internal inline fun <E, R, T> onResponseFn(
  callback: Callback<T>,
  call: Call<T>,
  errorConverter: Converter<ResponseBody, E>,
  response: Response<R>,
  newResponseFn: (R, Response<R>) -> Response<T>,
  errorResponseFn: (E, Response<R>) -> Response<T>
) {
  if (response.isSuccessful) {
    val body = response.body()
    if (body == null) {
      callback.onFailure(call, IllegalStateException("Null body found!"))
    } else {
      callback.onResponse(call, newResponseFn(body, response))
    }
  } else {
    val error = response.errorBody()
    if (error == null) {
      callback.onFailure(call, IllegalStateException("Null errorBody found!"))
    } else {
      try {
        val errorBody = errorConverter.convert(response.errorBody()!!)
        if (errorBody == null) {
          callback.onFailure(call, IllegalStateException("Null errorBody found!"))
        } else {
          callback.onResponse(call, errorResponseFn(errorBody, response))
        }
      } catch (e: Exception) {
        callback.onFailure(call, IllegalStateException("Failed to convert error body!", e))
      }
    }
  }
}
