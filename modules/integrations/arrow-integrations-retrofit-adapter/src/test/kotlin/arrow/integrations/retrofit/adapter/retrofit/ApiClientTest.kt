package arrow.integrations.retrofit.adapter.retrofit

import arrow.integrations.retrofit.adapter.CallK
import arrow.integrations.retrofit.adapter.mock.ResponseMock
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiClientTest {

  @GET("test")
  fun testCallK(): CallK<ResponseMock>

  @GET("testCallKResponse")
  fun testCallKResponse(): CallK<ResponseMock>

  @POST("testIOResponsePOST")
  fun testIOResponsePost(): CallK<Unit>

}
