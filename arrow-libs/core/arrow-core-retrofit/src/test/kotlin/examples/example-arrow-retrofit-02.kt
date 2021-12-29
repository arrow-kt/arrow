// This file was automatically generated from EitherCallAdapterFactory.kt by Knit tool. Do not edit.
package arrow.core.retrofit.examples.exampleArrowRetrofit02

import arrow.core.Either
import arrow.retrofit.adapter.either.networkhandling.CallError
import retrofit2.http.GET

data class User(val name: String)
interface MyService {
  @GET("/user/me")
  suspend fun user(): Either<CallError, User>
}
