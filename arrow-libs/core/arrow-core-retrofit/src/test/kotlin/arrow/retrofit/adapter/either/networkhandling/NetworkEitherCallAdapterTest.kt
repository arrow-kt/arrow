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
import java.io.EOFException
import java.net.ConnectException
import java.net.SocketTimeoutException

class NetworkEitherCallAdapterTest : UnitSpec() {

  private lateinit var server: MockWebServer

  private lateinit var service: CallErrorTestClient

  init {

    beforeAny {
      server = MockWebServer()
      server.start()
      service = Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(EitherCallAdapterFactory.create())
        .build()
        .create(CallErrorTestClient::class.java)
    }
    afterAny { server.shutdown() }

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
        .value.cause.shouldBeInstanceOf<ConnectException>()
    }

    "should return IOError when no response" {
      server.enqueue(MockResponse().apply { socketPolicy = SocketPolicy.NO_RESPONSE })

      val body = service.getEither()

      body.shouldBeInstanceOf<Left<IOError>>()
        .value.cause.shouldBeInstanceOf<SocketTimeoutException>()
    }

    "should return Unit when service method returns Unit and null body received" {
      server.enqueue(MockResponse().setResponseCode(204))

      val body = service.postSomething("Sample string")

      body shouldBe Unit.right()
    }

    "should return IOError when service method returns type other than Unit but null body received" {
      server.enqueue(MockResponse())

      val body = service.getEither()

      body.shouldBeInstanceOf<Left<IOError>>()
        .value.cause.shouldBeInstanceOf<EOFException>()
    }
  }
}
