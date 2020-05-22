package arrow.integrations.retrofit.adapter.retrofit

import arrow.core.Either
import arrow.integrations.retrofit.adapter.either.ResponseE
import arrow.integrations.retrofit.adapter.mock.ErrorMock
import arrow.integrations.retrofit.adapter.mock.ResponseMock
import retrofit2.http.GET

interface SuspedApiClientTest {

  @GET("/")
  suspend fun getEither(): Either<ErrorMock, ResponseMock>

  @GET("/")
  suspend fun getResponseE(): ResponseE<ErrorMock, ResponseMock>
}
