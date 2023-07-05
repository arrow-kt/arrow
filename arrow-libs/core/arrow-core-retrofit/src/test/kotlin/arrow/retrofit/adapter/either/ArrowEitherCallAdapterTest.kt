package arrow.retrofit.adapter.either

import arrow.core.left
import arrow.core.right
import arrow.retrofit.adapter.mock.ErrorMock
import arrow.retrofit.adapter.mock.ResponseMock
import arrow.retrofit.adapter.retrofit.SuspendApiTestClient
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ArrowEitherCallAdapterTest : StringSpec({

  lateinit var server: MockWebServer
  lateinit var service: SuspendApiTestClient

  beforeAny {
    server = MockWebServer()
    server.start()
    service = Retrofit.Builder()
      .baseUrl(server.url("/"))
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(EitherCallAdapterFactory.create())
      .build()
      .create(SuspendApiTestClient::class.java)
  }

  afterAny { server.shutdown() }

  "should return ResponseMock for 200 with valid JSON" {
    server.enqueue(MockResponse().setBody("""{"response":"Arrow rocks"}"""))

    val body = service.getEither()

    body shouldBe ResponseMock("Arrow rocks").right()
  }

  "should return Unit when service method returns Unit and null body received" {
    server.enqueue(MockResponse().setResponseCode(204))

    val body = service.postSomething("Sample string")

    body shouldBe Unit.right()
  }

  "should return Unit when service method returns Unit and JSON body received" {
    server.enqueue(MockResponse().setBody("""{"response":"Arrow rocks"}"""))

    val body = service.postSomething("Sample string")

    body shouldBe Unit.right()
  }

  "should return ErrorMock for 400 with valid JSON" {
    server.enqueue(MockResponse().setBody("""{"errorCode":666}""").setResponseCode(400))

    val body = service.getEither()

    body shouldBe ErrorMock(666).left()
  }

  "should throw for 200 with invalid JSON" {
    server.enqueue(MockResponse().setBody("""not a valid JSON"""))

    val body = runCatching { service.getEither() }

    body.isFailure shouldBe true
  }

  "should throw for 400 and invalid JSON" {
    server.enqueue(MockResponse().setBody("""not a valid JSON""").setResponseCode(400))

    val body = runCatching { service.getEither() }

    body.isFailure shouldBe true
  }

  "should throw when server disconnects" {
    server.enqueue(MockResponse().apply { socketPolicy = SocketPolicy.DISCONNECT_AFTER_REQUEST })

    val body = runCatching { service.getEither() }

    body.isFailure shouldBe true
  }
})
