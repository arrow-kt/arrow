package arrow.integrations.retrofit.adapter

import arrow.effects.IO
import retrofit2.http.GET

interface ApiClientTest {

  @GET("test")
  fun test(): CallK<String>

  @GET("testIO")
  fun testIO(): IO<String>
}
