package arrow.raise.ktor.server

import arrow.core.NonEmptyList
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.accumulate
import arrow.core.raise.ensure
import arrow.core.raise.ensureNotNull
import arrow.core.raise.withError
import arrow.raise.ktor.server.Response.Companion.invoke
import arrow.raise.ktor.server.request.RequestError
import arrow.raise.ktor.server.request.pathOrRaise
import arrow.raise.ktor.server.request.toSimpleMessage
import arrow.raise.ktor.server.request.validate
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import kotlin.test.Test

@OptIn(ExperimentalRaiseAccumulateApi::class)
class ValidateTest {
  @Serializable
  data class Info(val email: String)

  @Serializable
  data class Person(
    val name: String,
    val age: Int,
    val info: Info
  )

  @Test
  fun `accumulate with multiple errors`() = testApplication {
    install(ContentNegotiation) { json() }
    routing {
      putOrRaise("/user/{id}") {
        val person = withError({ raise(call.errorResponse(it)) }) {
          accumulate {
            val name by accumulating { pathOrRaise("name") }
            val age by accumulating { queryOrRaise<Int>("age") }
            val info by accumulating { receiveOrRaise<Info>() }
            Person(name, age, info)
          }
        }
        HttpStatusCode.Created(person)
      }
    }

    client.put("/user/hello") {
      parameter("age", null)
      setBody("""{"emule":"donkey"}""")
    }.let {
      assertSoftly {
        it.status shouldBe HttpStatusCode.BadRequest
        it.bodyAsText() shouldBe
          """
          Missing path parameter 'name'.
          Missing query parameter 'age'.
          Malformed body could not be deserialized to Info: Cannot transform this request's content to arrow.raise.ktor.server.ValidateTest.Info
          """.trimIndent()
      }
    }
  }

  @Test
  fun `accumulate from form parameters`() = testApplication {
    install(ContentNegotiation) { json() }
    routing {
      putOrRaise("/user/{name}") {
        val person = validate {
          val name by pathAccumulating()
          val form = formParametersDelegate()
          val age by form<Int>()
          val email by form("info.email")
          Person(name, age, Info(email))
        }

        HttpStatusCode.Created(person)
      }
    }

    client.put("/user/hello") {
      setBody(FormDataContent(parameters {
        append("age", "123")
        append("info.email", "don@key.oatie")
      }))
    }.let {
      assertSoftly {
        it.status shouldBe HttpStatusCode.Created
        Json.Default.parseToJsonElement(it.bodyAsText()) shouldBeEqual buildJsonObject {
          put("name", "hello")
          put("age", 123)
          putJsonObject("info") { put("email", "don@key.oatie") }
        }
      }
    }
  }

  @Test
  fun `manual validation without syntactic sugar`() = testApplication {
    install(ContentNegotiation) { json() }
    routing {
      put("/user/{name}") {
        val response = call.validate(call::errorResponse) {
          val name: String by pathAccumulating
          val form = formParametersDelegate()
          val age by form<Int>()
          val email by form("info.email")

          Response.payload(Person(name, age, Info(email)), HttpStatusCode.Created)
        }

        response.respondTo(call)
      }
    }

    client.put("/user/hello") {
      setBody(FormDataContent(parameters {
        append("age", "123")
        append("info.email", "don@key.oatie")
      }))
    }.let {
      assertSoftly {
        it.status shouldBe HttpStatusCode.Created
        Json.Default.parseToJsonElement(it.bodyAsText()) shouldBeEqual buildJsonObject {
          put("name", "hello")
          put("age", 123)
          putJsonObject("info") { put("email", "don@key.oatie") }
        }
      }
    }
  }

  @Test
  fun `validate by delegation multiple errors`() = testApplication {
    install(ContentNegotiation) { json() }
    routing {
      putOrRaise("/user/{id}") {
        val person = validate {
          val name: String by pathAccumulating
          val age: Int by queryAccumulating {
            val age = ensureNotNull(it.toIntOrNull()) { "not a valid number" }
            ensure(age >= 21) { "too young" }
            age
          }
          val info: Info by receiveAccumulating()
          Person(name, age, info)
        }

        HttpStatusCode.Created(person)
      }
    }

    client.put("/user/hello") {
      parameter("age", "12")
      contentType(ContentType.Application.Json)
      setBody("""{"emule":"donkey"}""")
    }.let {
      assertSoftly {
        it.status shouldBe HttpStatusCode.BadRequest
        it.bodyAsText() shouldBe
          """
          Missing path parameter 'name'.
          Malformed query parameter 'age' too young
          Malformed body could not be deserialized to Info: Illegal input: Encountered an unknown key 'emule' at offset 2 at path: $
          Use 'ignoreUnknownKeys = true' in 'Json {}' builder or '@JsonIgnoreUnknownKeys' annotation to ignore unknown keys.
          JSON input: {"emule":"donkey"}
          """.trimIndent()
      }
    }
  }


  @Test
  fun `validate by named delegation conversion`() = testApplication {
    install(ContentNegotiation) { json() }
    routing {
      putOrRaise("/user/{name}") {
        val person = validate {
          val name: String by pathAccumulating("user-name")
          val age: Int by queryAccumulating
          val info: Info by receiveAccumulating()
          Person(name, age, info)
        }
        HttpStatusCode.Created(person)
      }
    }

    client.put("/user/hello") {
      parameter("age", "old")
      setBody("""{"emule":"donkey"}""")
    }.let {
      assertSoftly {
        it.status shouldBe HttpStatusCode.BadRequest
        it.bodyAsText() shouldBe
          """
                  Missing path parameter 'user-name'.
                  Malformed query parameter 'age' couldn't be parsed/converted to Int: For input string: "old"
                  Malformed body could not be deserialized to Info: Cannot transform this request's content to arrow.raise.ktor.server.ValidateTest.Info
                  """.trimIndent()
      }
    }
  }

  @Test
  fun `validate by named delegation multiple errors`() = testApplication {
    install(ContentNegotiation) { json() }
    routing {
      putOrRaise("/user/{id}") {
        val person = validate {
          val name by pathAccumulating("user-name") { it }
          val age: Int by queryAccumulating("age") { it.toIntOrNull() ?: raise("nope") }
          val info: Info by receiveAccumulating()
          Person(name, age, info)
        }

        HttpStatusCode.Created(person)
      }
    }

    client.put("/user/hello") {
      parameter("age", "old")
      setBody("""{"emule":"donkey"}""")
    }.let {
      assertSoftly {
        it.status shouldBe HttpStatusCode.BadRequest
        it.bodyAsText() shouldBe
          """
          Missing path parameter 'user-name'.
          Malformed query parameter 'age' nope
          Malformed body could not be deserialized to Info: Cannot transform this request's content to arrow.raise.ktor.server.ValidateTest.Info
          """.trimIndent()
      }
    }
  }

  @Test
  fun `validate success`() = testApplication {
    install(ContentNegotiation) { json() }
    routing {
      putOrRaise("/user/{name}") {
        val person = validate {
          val name: String by pathAccumulating
          val age: Int by queryAccumulating
          val info: Info by receiveAccumulating()
          Person(name, age, info)
        }

        HttpStatusCode.Created(person)
      }
    }

    assertSoftly(client.put("/user/bob") {
      parameter("age", "31")
      contentType(ContentType.Application.Json)
      setBody("""{"email":"don@key.io"}""")
    }) {
      it.status shouldBe HttpStatusCode.Created
      Json.Default.parseToJsonElement(it.bodyAsText()) shouldBe
        buildJsonObject {
          put("name", "bob")
          put("age", 31)
          putJsonObject("info") {
            put("email", "don@key.io")
          }
        }

    }
  }

  @Test
  fun `validate with multiple errors with custom response`() = testApplication {
    fun validationError(errors: NonEmptyList<RequestError>): Response {
      @Serializable
      data class ErrorResponse(val errors: List<String>)

      return HttpStatusCode.BadRequest(
        ErrorResponse(
          errors = errors.map(RequestError::toSimpleMessage)
        )
      )
    }

    install(ContentNegotiation) { json() }
    routing {
      putOrRaise("/user/{id}") {
        val person = validate(::validationError) {
          call.queryParameters
          val name by accumulating { pathOrRaise("name") }
          val age by accumulating { queryOrRaise<Int>("age") }
          val info by accumulating { receiveOrRaise<Info>() }
          Person(name, age, info)
        }
        HttpStatusCode.Created(person)
      }
    }

    client.put("/user/hello") {
      parameter("age", "old")
      setBody("""{"emule":"donkey"}""")
    }.let {
      assertSoftly {
        it.status shouldBe HttpStatusCode.BadRequest
        Json.Default.parseToJsonElement(it.bodyAsText()) shouldBe buildJsonObject {
          putJsonArray("errors") {
            add("Missing path parameter 'name'.")
            add("Malformed query parameter 'age' couldn't be parsed/converted to Int: For input string: \"old\"")
            add("Malformed body could not be deserialized to Info: Cannot transform this request's content to arrow.raise.ktor.server.ValidateTest.Info")
          }
        }
      }
    }
  }

  @Test
  fun `validate with multiple errors with custom response via plugin`() = testApplication {
    fun validationError(errors: NonEmptyList<RequestError>): Response {
      @Serializable
      data class ErrorResponse(val errors: List<String>)

      return HttpStatusCode.BadRequest(
        ErrorResponse(
          errors = errors.map(RequestError::toSimpleMessage)
        )
      )
    }

    install(ContentNegotiation) { json() }
    routing {
      install(RaiseErrorResponse) {
        errorResponse { validationError(it) }
      }
      putOrRaise("/user/{id}") {
        val person = validate {
          val name by accumulating { pathOrRaise("name") }
          val age by accumulating { queryOrRaise<Int>("age") }
          val info by accumulating { receiveOrRaise<Info>() }
          Person(name, age, info)
        }
        HttpStatusCode.Created(person)
      }
    }

    client.put("/user/hello") {
      parameter("age", "old")
      setBody("""{"emule":"donkey"}""")
    }.let {
      assertSoftly {
        it.status shouldBe HttpStatusCode.BadRequest
        Json.Default.parseToJsonElement(it.bodyAsText()) shouldBe buildJsonObject {
          putJsonArray("errors") {
            add("Missing path parameter 'name'.")
            add("Malformed query parameter 'age' couldn't be parsed/converted to Int: For input string: \"old\"")
            add("Malformed body could not be deserialized to Info: Cannot transform this request's content to arrow.raise.ktor.server.ValidateTest.Info")
          }
        }
      }
    }
  }

  @Test
  fun `nested route-specific error handling`() = testApplication {
    routing {
      install(RaiseErrorResponse) {
        errorResponse {
          HttpStatusCode.BadRequest("root handler: ${it.joinToString { it.toSimpleMessage() }}")
        }
      }

      getOrRaise("/fail") {
        pathOrRaise("nothing")
      }

      route("/user") {
        install(RaiseErrorResponse) {
          errorResponse {
            HttpStatusCode.BadRequest("user handler: ${it.joinToString { it.toSimpleMessage() }}")
          }
        }

        getOrRaise("/{name?}") {
          val name: String by pathRaising
          name
        }
      }
    }

    assertSoftly(client.get("/fail")) {
      it.status shouldBe HttpStatusCode.BadRequest
      it.bodyAsText() shouldBe "root handler: Missing path parameter 'nothing'."
    }

    assertSoftly(client.get("/user/")) {
      it.status shouldBe HttpStatusCode.BadRequest
      it.bodyAsText() shouldBe "user handler: Missing path parameter 'name'."
    }

    assertSoftly(client.get("/user/bob")) {
      it.status shouldBe HttpStatusCode.OK
      it.bodyAsText() shouldBe "bob"
    }
  }
}
