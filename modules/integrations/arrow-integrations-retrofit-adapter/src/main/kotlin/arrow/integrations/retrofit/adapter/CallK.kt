package arrow.integrations.retrofit.adapter

import arrow.Kind
import arrow.effects.typeclasses.Async
import arrow.effects.typeclasses.MonadDefer
import arrow.typeclasses.MonadError
import retrofit2.Call
import retrofit2.Response

data class CallK<R>(val call: Call<R>) {
  fun <F> async(async: Async<F>): Kind<F, Response<R>> =
    async.async { proc ->
      call.enqueue(ResponseCallback(proc))
    }

  fun <F> defer(defer: MonadDefer<F>): Kind<F, Response<R>> =
    defer {
      call.execute()
    }

  fun <F> catch(defer: MonadError<F, Throwable>): Kind<F, Response<R>> =
    defer.run {
      catch {
        call.execute()
      }
    }
}
