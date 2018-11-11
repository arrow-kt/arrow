package arrow.integrations.retrofit.adapter

import arrow.Kind
import arrow.effects.typeclasses.Async
import arrow.effects.typeclasses.MonadDefer
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.MonadError
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response

fun <F, R> Response<R>.unwrapBody(apError: ApplicativeError<F, Throwable>): Kind<F, R> =
  if (this.isSuccessful) {
    val body = this.body()
    if (body != null) {
      apError.just(body)
    } else {
      apError.raiseError(IllegalStateException("The request returned a null body"))
    }
  } else {
    apError.raiseError(HttpException(this))
  }

fun <F, A> Call<A>.runAsync(AC: Async<F>): Kind<F, Response<A>> =
  AC.async { callback ->
    enqueue(ResponseCallback(callback))
  }

fun <F, A> Call<A>.runSyncDeferred(defer: MonadDefer<F>): Kind<F, Response<A>> = defer { execute() }

fun <F, A> Call<A>.runSyncCatch(monadError: MonadError<F, Throwable>): Kind<F, Response<A>> =
  monadError.run {
    catch {
      execute()
    }
  }
