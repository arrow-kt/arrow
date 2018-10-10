package arrow.integrations.retrofit.adapter

import arrow.Kind
import arrow.typeclasses.ApplicativeError
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
