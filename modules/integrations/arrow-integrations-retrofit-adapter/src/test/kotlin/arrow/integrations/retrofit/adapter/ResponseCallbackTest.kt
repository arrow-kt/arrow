package arrow.integrations.retrofit.adapter

import arrow.fx.IO
import arrow.fx.extensions.io.async.async
import arrow.fx.fix
import arrow.integrations.retrofit.adapter.retrofit.ApiClientTest
import arrow.integrations.retrofit.adapter.retrofit.retrofit
import arrow.test.UnitSpec
import io.kotlintest.fail
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer

class ResponseCallbackTest : UnitSpec() {
  private val server = MockWebServer().apply {
    enqueue(MockResponse().setBody("{response:  \"hello, world!\"}").setResponseCode(200))
    start()
  }

  private val baseUrl: HttpUrl = server.url("/")

  init {
    "bad deserialization should return Either.Left" {
      createApiClientTest(baseUrl)
        .testCallK()
        .async(IO.async())
        .fix()
        .attempt()
        .unsafeRunSync()
        .fold({
        }, {
          fail("The request should have not terminated correctly")
        })
    }
  }
}

private fun createApiClientTest(baseUrl: HttpUrl) = retrofit(baseUrl).create(ApiClientTest::class.java)
