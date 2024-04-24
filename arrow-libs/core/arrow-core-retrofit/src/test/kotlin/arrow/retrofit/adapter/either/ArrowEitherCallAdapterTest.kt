package arrow.retrofit.adapter.either

import arrow.core.left
import arrow.core.right
import arrow.retrofit.adapter.mock.ErrorMock
import arrow.retrofit.adapter.mock.ResponseMock
import arrow.retrofit.adapter.retrofit.SuspendApiTestClient
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ArrowEitherCallAdapterTest {

  lateinit var server: MockWebServer
  lateinit var service: SuspendApiTestClient

  @BeforeTest fun initialize() {
    server = MockWebServer()
    server.start()
    service = Retrofit.Builder()
      .baseUrl(server.url("/"))
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(EitherCallAdapterFactory.create())
      .build()
      .create(SuspendApiTestClient::class.java)
  }

  @AfterTest fun shutdown() {
    server.shutdown()
  }

  @Test fun shouldReturnResponseMockFor200WithValidJson() = runTest {
    server.enqueue(MockResponse().setBody("""{"response":"Arrow rocks"}"""))

    val body = service.getEither()

    body shouldBe ResponseMock("Arrow rocks").right()
  }

  @Test fun shouldReturnUnitWhenServiceMethodReturnsUnitAndNullBodyReceived() = runTest {
    server.enqueue(MockResponse().setResponseCode(204))

    val body = service.postSomething("Sample string")

    body shouldBe Unit.right()
  }

  @Test fun shouldReturnUnitWhenServiceMethodReturnsUnitAndJsonBodyReceived() = runTest {
    server.enqueue(MockResponse().setBody("""{"response":"Arrow rocks"}"""))

    val body = service.postSomething("Sample string")

    body shouldBe Unit.right()
  }

  @Test fun shouldReturnErrorMockFor400WithvalidJson() = runTest {
    server.enqueue(MockResponse().setBody("""{"errorCode":666}""").setResponseCode(400))

    val body = service.getEither()

    body shouldBe ErrorMock(666).left()
  }

  @Test fun shouldThrowFor200WithInvalidJson() = runTest {
    server.enqueue(MockResponse().setBody("""not a valid JSON"""))

    val body = runCatching { service.getEither() }

    body.isFailure shouldBe true
  }

  @Test fun shouldThrowFor400AndInvalidJson() = runTest {
    server.enqueue(MockResponse().setBody("""not a valid JSON""").setResponseCode(400))

    val body = runCatching { service.getEither() }

    body.isFailure shouldBe true
  }

  @Test fun shouldThrowWhenServerDisconnects() = runTest {
    server.enqueue(MockResponse().apply { socketPolicy = SocketPolicy.DISCONNECT_AFTER_REQUEST })

    val body = runCatching { service.getEither() }

    body.isFailure shouldBe true
  }
}
