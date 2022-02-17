// This file was automatically generated from EitherCallAdapterFactory.kt by Knit tool. Do not edit.
package arrow.core.retrofit.examples.exampleArrowRetrofit01

import arrow.core.Either
import arrow.retrofit.adapter.either.ResponseE
import retrofit2.http.GET

data class User(val name: String)
data class ErrorBody(val msg: String)

interface MyService {

  @GET("/user/me")
  suspend fun getUser(): Either<ErrorBody, User>

  @GET("/user/me")
  suspend fun getUserResponseE(): ResponseE<ErrorBody, User>
}
