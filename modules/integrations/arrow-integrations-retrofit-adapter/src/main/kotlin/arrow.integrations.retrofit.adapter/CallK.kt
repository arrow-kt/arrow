package arrow.integrations.retrofit.adapter

import arrow.Kind
import arrow.effects.typeclasses.Async
import arrow.effects.typeclasses.MonadDefer
import arrow.typeclasses.MonadError
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response

data class CallK<R>(val call: Call<R>) {
  fun <F> async(async: Async<F>): Kind<F, R> =
    async.async { proc -> call.enqueue(ResponseCallback(proc)) }

  fun <F> defer(defer: MonadDefer<F>): Kind<F, R> =
    defer {
      handleResponse(call.execute())
    }

  fun <F> catch(defer: MonadError<F, Throwable>): Kind<F, R> =
    defer.run {
      catch {
        handleResponse(call.execute())
      }
    }
}

private fun <R> handleResponse(response: Response<R>) =
  if (response.isSuccessful) {
    response.body() ?: throw IllegalStateException("The request returned a null body")
  } else {
    throw HttpException(response)
  }
