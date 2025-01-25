package arrow.retrofit.adapter.either.networkhandling

import arrow.core.Either
import arrow.retrofit.adapter.mock.ResponseMock
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CallErrorTestClient {

  @GET("/")
  suspend fun getEither(): Either<CallError, ResponseMock>

  @POST("/")
  suspend fun postSomething(@Body something: String): Either<CallError, Unit>
}
