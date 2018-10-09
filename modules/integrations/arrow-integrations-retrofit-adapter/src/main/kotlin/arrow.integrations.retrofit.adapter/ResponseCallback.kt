package arrow.integrations.retrofit.adapter

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class ResponseCallback<R>(private val proc: (Either<Throwable, R>) -> Unit) : Callback<R> {
  override fun onResponse(call: Call<R>, response: Response<R>) {
    if (response.isSuccessful) {
      val body = response.body()
      if (body != null) {
        proc(body.right())
      } else {
        proc(IllegalStateException("The request returned a null body").left())
      }
    } else {
      proc(HttpException(response).left())
    }
  }

  override fun onFailure(call: Call<R>, throwable: Throwable) {
    proc(throwable.left())
  }
}
