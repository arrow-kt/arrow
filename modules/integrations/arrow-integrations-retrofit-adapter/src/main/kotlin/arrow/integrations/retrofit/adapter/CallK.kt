package arrow.integrations.retrofit.adapter

import arrow.Kind
import arrow.fx.typeclasses.Async
import arrow.fx.typeclasses.MonadDefer
import arrow.typeclasses.MonadError
import retrofit2.Call
import retrofit2.Response

data class CallK<R>(val call: Call<R>) {
  fun <F> async(AC: Async<F>): Kind<F, Response<R>> = call.runAsync(AC)

  fun <F> defer(defer: MonadDefer<F>): Kind<F, Response<R>> = call.runSyncDeferred(defer)

  fun <F> catch(monadError: MonadError<F, Throwable>): Kind<F, Response<R>> = call.runSyncCatch(monadError)
}
