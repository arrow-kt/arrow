package arrow.retrofit.adapter.either.networkhandling

import arrow.core.Either.Left
import arrow.core.left
import arrow.core.right
import arrow.retrofit.adapter.either.EitherCallAdapterFactory
import arrow.retrofit.adapter.mock.ResponseMock
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.stringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.SocketException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

@ExperimentalSerializationApi
class NetworkEitherCallAdapterTestSuite : StringSpec({
  include(networkEitherCallAdapterTests(GsonConverterFactory.create()))
  include(networkEitherCallAdapterTests(MoshiConverterFactory.create()))
  include(networkEitherCallAdapterTests(Json.asConverterFactory("application/json".toMediaType())))
})

private fun networkEitherCallAdapterTests(
  jsonConverterFactory: Converter.Factory,
) = stringSpec {
  var server: MockWebServer? = null
  var service: CallErrorTestClient? = null

  beforeAny {
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
  afterAny { server!!.shutdown() }

  "should return ResponseMock for 200 with valid JSON" {
    server!!.enqueue(MockResponse().setBody("""{"response":"Arrow rocks"}"""))

    val body = service!!.getEither()

    body shouldBe ResponseMock("Arrow rocks").right()
  }

  "should return HttpError for 400" {
    server!!.enqueue(MockResponse().setBody("""{"errorCode":666}""").setResponseCode(400))

    val body = service!!.getEither()

    body shouldBe HttpError(
      code = 400,
      message = "Client Error",
      body = """{"errorCode":666}""",
    ).left()
  }

  "should return CallError for 200 with invalid JSON" {
    server!!.enqueue(MockResponse().setBody("""not a valid JSON"""))

    val body = service!!.getEither()

    body.shouldBeInstanceOf<Left<*>>()
      .value.shouldBeInstanceOf<CallError>()
  }

  "should return HttpError for 400 and invalid JSON" {
    server!!.enqueue(MockResponse().setBody("""not a valid JSON""").setResponseCode(400))

    val body = service!!.getEither()

    body shouldBe HttpError(
      code = 400,
      message = "Client Error",
      body = """not a valid JSON""",
    ).left()
  }

  "should return IOError when server disconnects" {
    server!!.enqueue(MockResponse().apply { socketPolicy = SocketPolicy.DISCONNECT_AT_START })

    val body = service!!.getEither()

    body.shouldBeInstanceOf<Left<*>>()
      .value.shouldBeInstanceOf<IOError>()
      .cause.shouldBeInstanceOf<SocketException>()
  }

  "should return IOError when no response" {
    server!!.enqueue(MockResponse().apply { socketPolicy = SocketPolicy.NO_RESPONSE })

    val body = service!!.getEither()

    body.shouldBeInstanceOf<Left<*>>()
      .value.shouldBeInstanceOf<IOError>()
      .cause.shouldBeInstanceOf<SocketTimeoutException>()
  }

  "should return Unit when service method returns Unit and null body received" {
    server!!.enqueue(MockResponse().setResponseCode(204))

    val body = service!!.postSomething("Sample string")

    body shouldBe Unit.right()
  }

  "should return Unit when service method returns Unit and JSON body received" {
    server!!.enqueue(MockResponse().setBody("""{"response":"Arrow rocks"}"""))

    val body = service!!.postSomething("Sample string")

    body shouldBe Unit.right()
  }

  "should return CallError when service method returns type other than Unit but null body received" {
    server!!.enqueue(MockResponse())

    val body = service!!.getEither()

    body.shouldBeInstanceOf<Left<*>>()
      .value.shouldBeInstanceOf<CallError>()
  }
}
