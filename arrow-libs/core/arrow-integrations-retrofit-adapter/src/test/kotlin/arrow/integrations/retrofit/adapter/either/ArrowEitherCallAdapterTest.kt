package arrow.integrations.retrofit.adapter.either

import arrow.core.left
import arrow.core.right
import arrow.core.test.UnitSpec
import arrow.integrations.retrofit.adapter.mock.ErrorMock
import arrow.integrations.retrofit.adapter.mock.ResponseMock
import arrow.integrations.retrofit.adapter.retrofit.SuspedApiClientTest
import io.kotlintest.Spec
import io.kotlintest.shouldBe
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ArrowEitherCallAdapterTest : UnitSpec() {

  private val server = MockWebServer()

  private val service: SuspedApiClientTest by lazy {
    Retrofit.Builder()
      .baseUrl(server.url("/"))
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(EitherCallAdapterFactory.create())
      .build()
      .create(SuspedApiClientTest::class.java)
  }

  override fun beforeSpec(spec: Spec) {
    super.beforeSpec(spec)
    server.start()
  }

  override fun afterSpec(spec: Spec) {
    server.shutdown()
    super.afterSpec(spec)
  }

  init {

    "should return ResponseMock for 200 with valid JSON" {
      server.enqueue(MockResponse().setBody("""{"response":"Arrow rocks"}"""))

      val body = runBlocking { service.getEither() }

      body shouldBe ResponseMock("Arrow rocks").right()
    }

    "should return ErrorMock for 400 with valid JSON" {
      server.enqueue(MockResponse().setBody("""{"errorCode":666}""").setResponseCode(400))

      val body = runBlocking { service.getEither() }

      body shouldBe ErrorMock(666).left()
    }

    "should throw for 200 with invalid JSON" {
      server.enqueue(MockResponse().setBody("""not a valid JSON"""))

      val body = kotlin.runCatching { service.getEither() }

      body.isFailure shouldBe true
    }

    "should throw for 400 and invalid JSON" {
      server.enqueue(MockResponse().setBody("""not a valid JSON""").setResponseCode(400))

      val body = kotlin.runCatching { service.getEither() }

      body.isFailure shouldBe true
    }

    "should throw when server disconnects" {
      server.enqueue(MockResponse().apply { socketPolicy = SocketPolicy.DISCONNECT_AFTER_REQUEST })

      val body = runCatching { service.getEither() }

      body.isFailure shouldBe true
    }
  }
}
