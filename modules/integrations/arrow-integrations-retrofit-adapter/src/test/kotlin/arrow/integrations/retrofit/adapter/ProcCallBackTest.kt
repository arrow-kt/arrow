package arrow.integrations.retrofit.adapter

import arrow.effects.IO
import arrow.effects.ObservableK
import arrow.effects.async
import arrow.effects.fix
import arrow.effects.monadDefer
import arrow.integrations.retrofit.adapter.mock.ResponseMock
import arrow.integrations.retrofit.adapter.retrofit.retrofit
import arrow.test.UnitSpec
import io.kotlintest.KTestJUnitRunner
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class ProcCallBackTest : UnitSpec() {
  private val server = MockWebServer().apply {
    enqueue(MockResponse().setBody("{\"response\":  \"hello, world!\"}").setResponseCode(200))
    enqueue(MockResponse().setBody("{\"response\":  \"hello, world!\"}").setResponseCode(200))
    enqueue(MockResponse().setBody("{\"response\":  \"hello, world!\"}").setResponseCode(200))
    start()
  }

  private val baseUrl: HttpUrl = server.url("/")

  init {

    "should be able to parse answer with ForIO" {
      val result = createApiClientTest(baseUrl)
        .test()
        .async(IO.async())
        .fix()
        .unsafeRunSync()

      assertEquals(result, ResponseMock("hello, world!"))
    }

    "should be able to parse answer with ForObservableK" {
      createApiClientTest(baseUrl)
        .test()
        .defer(ObservableK.monadDefer())
        .fix()
        .observable
        .test()
        .assertValue(ResponseMock("hello, world!"))
    }

    "should be able to parse answer with IO" {
      val result = createApiClientTest(baseUrl)
        .testIO()
        .unsafeRunSync()

      assertEquals(result, ResponseMock("hello, world!"))
    }
  }
}

private fun createApiClientTest(baseUrl: HttpUrl) = retrofit(baseUrl).create(ApiClientTest::class.java)