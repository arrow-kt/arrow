package arrow.retrofit.adapter.either.networkhandling

import arrow.core.Either.Left
import arrow.core.left
import arrow.core.right
import arrow.retrofit.adapter.either.EitherCallAdapterFactory
import arrow.retrofit.adapter.mock.ResponseMock
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.SocketException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

abstract class NetworkEitherCallAdapterTest(
  private val jsonConverterFactory: Converter.Factory,
) {
  private var server: MockWebServer? = null
  private var service: CallErrorTestClient? = null

  open fun initialize() {
    server = MockWebServer()
    server!!.start()
    val client = OkHttpClient.Builder()
      .readTimeout(200, TimeUnit.MILLISECONDS)
      .build()
    service = Retrofit.Builder()
      .baseUrl(server!!.url("/"))
      .client(client)
      .addConverterFactory(jsonConverterFactory)
      .addCallAdapterFactory(EitherCallAdapterFactory.create())
      .build()
      .create(CallErrorTestClient::class.java)
  }

  open fun shutdown() {
    server!!.shutdown()
  }

  open fun shouldReturn200ForValidJson() = runTest {
    server!!.enqueue(MockResponse().setBody("""{"response":"Arrow rocks"}"""))

    val body = service!!.getEither()

    body shouldBe ResponseMock("Arrow rocks").right()
  }

  open fun shouldReturnHttpErrorFor404() = runTest {
    server!!.enqueue(MockResponse().setBody("""{"errorCode":666}""").setResponseCode(400))

    val body = service!!.getEither()

    body shouldBe HttpError(
      code = 400,
      message = "Client Error",
      body = """{"errorCode":666}""",
    ).left()
  }

  open fun shouldReturnCallErrorFor200InvalidJson() = runTest {
    server!!.enqueue(MockResponse().setBody("""not a valid JSON"""))

    val body = service!!.getEither()

    body.shouldBeInstanceOf<Left<*>>()
      .value.shouldBeInstanceOf<CallError>()
  }

  open fun shouldReturnHttpErrorFor404InvalidJson() = runTest {
    server!!.enqueue(MockResponse().setBody("""not a valid JSON""").setResponseCode(400))

    val body = service!!.getEither()

    body shouldBe HttpError(
      code = 400,
      message = "Client Error",
      body = """not a valid JSON""",
    ).left()
  }

  open fun shouldReturnIOErrorDisconnect() = runTest {
    server!!.enqueue(MockResponse().apply { socketPolicy = SocketPolicy.DISCONNECT_AT_START })

    val body = service!!.getEither()

    body.shouldBeInstanceOf<Left<*>>()
      .value.shouldBeInstanceOf<IOError>()
      .cause.shouldBeInstanceOf<SocketException>()
  }

  open fun shouldReturnIOErrorNoResponse() = runTest {
    server!!.enqueue(MockResponse().apply { socketPolicy = SocketPolicy.NO_RESPONSE })

    val body = service!!.getEither()

    body.shouldBeInstanceOf<Left<*>>()
      .value.shouldBeInstanceOf<IOError>()
      .cause.shouldBeInstanceOf<SocketTimeoutException>()
  }

  open fun shouldReturnUnitForNullBody() = runTest {
    server!!.enqueue(MockResponse().setResponseCode(204))

    val body = service!!.postSomething("Sample string")

    body shouldBe Unit.right()
  }

  open fun shouldReturnUnitForUnitBody() = runTest {
    server!!.enqueue(MockResponse().setBody("""{"response":"Arrow rocks"}"""))

    val body = service!!.postSomething("Sample string")

    body shouldBe Unit.right()
  }

  open fun shouldReturnCallErrorWithUnitForNonNullBody() = runTest {
    server!!.enqueue(MockResponse())

    val body = service!!.getEither()

    body.shouldBeInstanceOf<Left<*>>()
      .value.shouldBeInstanceOf<CallError>()
  }
}

class NetworkEitherCallAdapterTestGson : NetworkEitherCallAdapterTest(GsonConverterFactory.create()) {
  @BeforeTest override fun initialize() {
    super.initialize()
  }

  @AfterTest override fun shutdown() {
    super.shutdown()
  }

  @Test override fun shouldReturn200ForValidJson() = super.shouldReturn200ForValidJson()

  @Test override fun shouldReturnHttpErrorFor404() = super.shouldReturnHttpErrorFor404()

  @Test override fun shouldReturnCallErrorFor200InvalidJson() = super.shouldReturnCallErrorFor200InvalidJson()

  @Test override fun shouldReturnHttpErrorFor404InvalidJson() = super.shouldReturnHttpErrorFor404InvalidJson()

  @Test override fun shouldReturnIOErrorDisconnect() = super.shouldReturnIOErrorDisconnect()

  @Test override fun shouldReturnIOErrorNoResponse() = super.shouldReturnIOErrorNoResponse()

  @Test override fun shouldReturnUnitForNullBody() = super.shouldReturnUnitForNullBody()

  @Test override fun shouldReturnUnitForUnitBody() = super.shouldReturnUnitForUnitBody()

  @Test override fun shouldReturnCallErrorWithUnitForNonNullBody() = super.shouldReturnCallErrorWithUnitForNonNullBody()
}

class NetworkEitherCallAdapterTestMoshi : NetworkEitherCallAdapterTest(MoshiConverterFactory.create(Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build())) {
  @BeforeTest override fun initialize() {
    super.initialize()
  }

  @AfterTest override fun shutdown() {
    super.shutdown()
  }

  @Test override fun shouldReturn200ForValidJson() = super.shouldReturn200ForValidJson()

  @Test override fun shouldReturnHttpErrorFor404() = super.shouldReturnHttpErrorFor404()

  @Test override fun shouldReturnCallErrorFor200InvalidJson() = super.shouldReturnCallErrorFor200InvalidJson()

  @Test override fun shouldReturnHttpErrorFor404InvalidJson() = super.shouldReturnHttpErrorFor404InvalidJson()

  @Test override fun shouldReturnIOErrorDisconnect() = super.shouldReturnIOErrorDisconnect()

  @Test override fun shouldReturnIOErrorNoResponse() = super.shouldReturnIOErrorNoResponse()

  @Test override fun shouldReturnUnitForNullBody() = super.shouldReturnUnitForNullBody()

  @Test override fun shouldReturnUnitForUnitBody() = super.shouldReturnUnitForUnitBody()

  @Test override fun shouldReturnCallErrorWithUnitForNonNullBody() = super.shouldReturnCallErrorWithUnitForNonNullBody()
}

class NetworkEitherCallAdapterTestKotlinxSerialization : NetworkEitherCallAdapterTest(Json.asConverterFactory("application/json".toMediaType())) {
  @BeforeTest override fun initialize() {
    super.initialize()
  }

  @AfterTest override fun shutdown() {
    super.shutdown()
  }

  @Test override fun shouldReturn200ForValidJson() = super.shouldReturn200ForValidJson()

  @Test override fun shouldReturnHttpErrorFor404() = super.shouldReturnHttpErrorFor404()

  @Test override fun shouldReturnCallErrorFor200InvalidJson() = super.shouldReturnCallErrorFor200InvalidJson()

  @Test override fun shouldReturnHttpErrorFor404InvalidJson() = super.shouldReturnHttpErrorFor404InvalidJson()

  @Test override fun shouldReturnIOErrorDisconnect() = super.shouldReturnIOErrorDisconnect()

  @Test override fun shouldReturnIOErrorNoResponse() = super.shouldReturnIOErrorNoResponse()

  @Test override fun shouldReturnUnitForNullBody() = super.shouldReturnUnitForNullBody()

  @Test override fun shouldReturnUnitForUnitBody() = super.shouldReturnUnitForUnitBody()

  @Test override fun shouldReturnCallErrorWithUnitForNonNullBody() = super.shouldReturnCallErrorWithUnitForNonNullBody()
}
