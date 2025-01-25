package arrow.retrofit.adapter.retrofit

import arrow.core.Either
import arrow.retrofit.adapter.either.ResponseE
import arrow.retrofit.adapter.mock.ErrorMock
import arrow.retrofit.adapter.mock.ResponseMock
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SuspendApiTestClient {

  @GET("/")
  suspend fun getEither(): Either<ErrorMock, ResponseMock>

  @POST("/")
  suspend fun postSomething(@Body something: String): Either<ErrorMock, Unit>

  @GET("/")
  suspend fun getResponseE(): ResponseE<ErrorMock, ResponseMock>

  @POST("/")
  suspend fun postSomethingResponseE(@Body something: String): ResponseE<ErrorMock, Unit>
}
