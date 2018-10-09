package arrow.integrations.retrofit.adapter

import arrow.effects.IO
import arrow.integrations.retrofit.adapter.mock.ResponseMock
import retrofit2.http.GET

interface ApiClientTest {

  @GET("test")
  fun test(): CallK<ResponseMock>

  @GET("testIO")
  fun testIO(): IO<ResponseMock>
}
