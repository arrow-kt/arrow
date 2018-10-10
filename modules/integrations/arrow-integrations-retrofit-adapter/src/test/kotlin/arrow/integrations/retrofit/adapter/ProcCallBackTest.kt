package arrow.integrations.retrofit.adapter

import arrow.core.Either
import arrow.core.applicativeError
import arrow.core.fix
import arrow.effects.IO
import arrow.effects.ObservableK
import arrow.effects.applicativeError
import arrow.effects.async
import arrow.effects.fix
import arrow.effects.monadDefer
import arrow.integrations.retrofit.adapter.mock.ResponseMock
import arrow.integrations.retrofit.adapter.retrofit.retrofit
import arrow.test.UnitSpec
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.fail
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class ProcCallBackTest : UnitSpec() {
  private val server = MockWebServer().apply {
    enqueue(MockResponse().setBody("{\"response\":  \"hello, world!\"}").setResponseCode(200))
    start()
  }

  private val baseUrl: HttpUrl = server.url("/")

  init {
    "should be able to parse answer with ForIO" {
      createApiClientTest(baseUrl)
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
        createApiClientTest(baseUrl)
          .testCallK()
          .defer(monadDefer())
          .fix()
          .flatMap { response ->
            response.unwrapBody(applicativeError()).fix()
          }
          .observable
          .test()
          .assertValue(ResponseMock("hello, world!"))
      }
    }

    "should be able to parse answer with IO" {
      createApiClientTest(baseUrl)
        .testIO()
        .unsafeRunSync()
        .unwrapBody(Either.applicativeError())
        .fix()
        .fold({ throwable ->
          fail("The requested terminated with an exception. Message: ${throwable.message}")
        }, { responseMock ->
          assertEquals(ResponseMock("hello, world!"), responseMock)
        })
    }

    "should be able to run a POST with UNIT as response" {
      createApiClientTest(baseUrl)
        .testIOResponsePost()
        .unsafeRunSync()
    }
  }
}

private fun createApiClientTest(baseUrl: HttpUrl) = retrofit(baseUrl).create(ApiClientTest::class.java)