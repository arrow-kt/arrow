// This file was automatically generated from EitherCallAdapterFactory.kt by Knit tool. Do not edit.
package arrow.core.retrofit.examples.exampleArrowRetrofit02

import arrow.core.Either
import arrow.retrofit.adapter.either.networkhandling.CallError
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class User(val name: String)

interface MyService {

  @GET("/user/me")
  suspend fun getUser(): Either<CallError, User>

  // Set the expected response type as Unit if you expect a null response body
  // (e.g. for 204 No Content response)
  @POST("/")
  suspend fun postSomething(@Body something: String): Either<CallError, Unit>
}
