package arrow.retrofit.adapter.either.networkhandling

import arrow.core.Either.Left
import arrow.core.left
import arrow.core.right
import arrow.core.test.UnitSpec
import arrow.retrofit.adapter.either.EitherCallAdapterFactory
import arrow.retrofit.adapter.mock.ResponseMock
import com.google.gson.stream.MalformedJsonException
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException

class NetworkEitherCallAdapterTest : UnitSpec() {

  private val server = MockWebServer()

  private val service: CallErrorTestClient by lazy {
    Retrofit.Builder()
      .baseUrl(server.url("/"))
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(EitherCallAdapterFactory.create())
      .build()
      .create(CallErrorTestClient::class.java)
  }

  init {

    beforeSpec { server.start() }
    afterSpec { server.shutdown() }

    "should return ResponseMock for 200 with valid JSON" {
      server.enqueue(MockResponse().setBody("""{"response":"Arrow rocks"}"""))

      val body = service.getEither()

      body shouldBe ResponseMock("Arrow rocks").right()
    }

    "should return HttpError for 400" {
      server.enqueue(MockResponse().setBody("""{"errorCode":666}""").setResponseCode(400))

      val body = service.getEither()

      body shouldBe HttpError(code = 400, body = """{"errorCode":666}""").left()
    }

    "should return IOError for 200 with invalid JSON" {
      server.enqueue(MockResponse().setBody("""not a valid JSON"""))

      val body = service.getEither()

      body.shouldBeInstanceOf<Left<IOError>>()
        .value.cause.shouldBeInstanceOf<MalformedJsonException>()
    }

    "should return HttpError for 400 and invalid JSON" {
      server.enqueue(MockResponse().setBody("""not a valid JSON""").setResponseCode(400))

      val body = service.getEither()

      val value = body.shouldBeInstanceOf<Left<HttpError>>().value
      value.shouldBe(HttpError(code = 400, body = """not a valid JSON"""))
    }

    "should return IOError when server disconnects" {
      server.enqueue(MockResponse().apply { socketPolicy = SocketPolicy.DISCONNECT_AFTER_REQUEST })

      val body = service.getEither()

      body.shouldBeInstanceOf<Left<IOError>>()
        .value.cause.shouldBeInstanceOf<SocketTimeoutException>()
    }
  }
}
