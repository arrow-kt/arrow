package arrow.retrofit.adapter.either

import arrow.core.Either
import okhttp3.Headers
import okhttp3.Response

public data class ResponseE<E, A>(
  val raw: Response,
  val body: Either<E, A>,
) {

  val code: Int = raw.code()

  val message: String? = raw.message()

  val headers: Headers = raw.headers()

  val isSuccessful: Boolean = raw.isSuccessful
}
