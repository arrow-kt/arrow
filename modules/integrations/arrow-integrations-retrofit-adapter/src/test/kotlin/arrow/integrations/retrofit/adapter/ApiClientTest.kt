package arrow.integrations.retrofit.adapter

import arrow.effects.IO
import arrow.integrations.retrofit.adapter.mock.ResponseMock
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiClientTest {

  @GET("test")
  fun testCallK(): CallK<ResponseMock>

  @GET("testCallKResponse")
  fun testCallKResponse(): CallK<Response<ResponseMock>>

  @GET("testIO")
  fun testIO(): IO<ResponseMock>

  @GET("testIOResponse")
  fun testIOResponse(): IO<Response<ResponseMock>>

  @POST("testIOResponsePOST")
  fun testIOResponsePost(): IO<Unit>

}
