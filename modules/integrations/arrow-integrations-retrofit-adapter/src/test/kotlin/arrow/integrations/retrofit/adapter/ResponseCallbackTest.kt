package arrow.integrations.retrofit.adapter

import arrow.effects.IO
import arrow.effects.fix
import arrow.effects.instances.io.async.async
import arrow.integrations.retrofit.adapter.retrofit.ApiClientTest
import arrow.integrations.retrofit.adapter.retrofit.retrofit
import arrow.test.UnitSpec
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.fail
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
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
