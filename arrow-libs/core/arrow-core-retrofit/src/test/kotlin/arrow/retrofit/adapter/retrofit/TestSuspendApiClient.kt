package arrow.retrofit.adapter.retrofit

import arrow.core.Either
import arrow.retrofit.adapter.either.ResponseE
import arrow.retrofit.adapter.mock.ErrorMock
import arrow.retrofit.adapter.mock.ResponseMock
import retrofit2.http.GET

interface TestSuspendApiClient {

  @GET("/")
  suspend fun getEither(): Either<ErrorMock, ResponseMock>

  @GET("/")
  suspend fun getResponseE(): ResponseE<ErrorMock, ResponseMock>
}
