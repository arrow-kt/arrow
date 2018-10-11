package arrow.integrations.retrofit.adapter

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResponseCallback<R>(private val proc: (Either<Throwable, Response<R>>) -> Unit) : Callback<R> {
  override fun onResponse(call: Call<R>, response: Response<R>) {
    proc(response.right())
  }

  override fun onFailure(call: Call<R>, throwable: Throwable) {
    proc(throwable.left())
  }
}
