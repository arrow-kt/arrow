// This file was automatically generated from EitherCallAdapterFactory.kt by Knit tool. Do not edit.
package arrow.core.retrofit.examples.exampleArrow01

interface MyService {
  @GET("/user/me")
  suspend fun user(): Either<ErrorBody, User>

  @GET("/user/me")
  suspend fun userResponse(): EitherR<ErrorBody, User>
}
