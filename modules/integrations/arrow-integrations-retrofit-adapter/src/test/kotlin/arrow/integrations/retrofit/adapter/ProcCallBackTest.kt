package arrow.integrations.retrofit.adapter

import arrow.core.Either
import arrow.core.extensions.either.applicativeError.applicativeError
import arrow.core.fix
import arrow.fx.IO
import arrow.fx.extensions.io.async.async
import arrow.fx.fix
import arrow.fx.rx2.ObservableK
import arrow.fx.rx2.extensions.observablek.applicativeError.applicativeError
import arrow.fx.rx2.extensions.observablek.monadDefer.monadDefer
import arrow.fx.rx2.fix
import arrow.fx.rx2.value
import arrow.integrations.retrofit.adapter.mock.ResponseMock
import arrow.integrations.retrofit.adapter.retrofit.ApiClientTest
import arrow.integrations.retrofit.adapter.retrofit.retrofit
import arrow.test.UnitSpec
import io.kotlintest.fail
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals

class ProcCallBackTest : UnitSpec() {

  private fun server(): MockWebServer = MockWebServer().apply {
    enqueue(MockResponse().setBody("{\"response\":  \"hello, world!\"}").setResponseCode(200))
    start()
  }

  private fun baseUrl(): HttpUrl = server().url("/")

  init {
    "should be able to parse answer with ForIO" {
      createApiClientTest(baseUrl())
        .testCallK()
        .async(IO.async())
        .fix()
        .unsafeRunSync()
        .unwrapBody(Either.applicativeError())
        .fix()
        .fold({ throwable ->
          fail("The requested terminated with an exception. Message: ${throwable.message}")
        }, { responseMock ->
          assertEquals(ResponseMock("hello, world!"), responseMock)
        })
    }

    "should be able to parse answer with ForObservableK" {
      with(ObservableK) {
        createApiClientTest(baseUrl())
          .testCallK()
          .defer(monadDefer())
          .fix()
          .flatMap { response ->
            response.unwrapBody(applicativeError()).fix()
          }
          .value()
          .test()
          .assertValue(ResponseMock("hello, world!"))
      }
    }

    "should be able to run a POST with UNIT as response" {
      createApiClientTest(baseUrl())
        .testIOResponsePost()
        .async(IO.async())
        .fix()
        .unsafeRunSync()
    }
  }
}

private fun createApiClientTest(baseUrl: HttpUrl) = retrofit(baseUrl).create(ApiClientTest::class.java)
