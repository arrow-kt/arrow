package arrow.raise.ktor.server

import arrow.core.raise.ensure
import arrow.core.raise.recover
import arrow.core.raise.withError
import io.kotest.assertions.asClue
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.ktor.util.reflect.*
import io.ktor.utils.io.*
import io.ktor.utils.io.charsets.*
import kotlin.test.Test

class HandleOrRaiseTest {
  @Test
  fun `can call respond from handleOrRaise`() = testApplication {
    routing {
      handleOrRaise {
        call.respondText("OK")
      }
    }

    val response = client.get { expectSuccess = false }

    assertSoftly {
      response.status shouldBe HttpStatusCode.OK
      response.bodyAsText() shouldBe "OK"
    }
  }

  @Test
  fun `can raise HttpStatusCode from handleOrRaise`() = testApplication {
    routing {
      handleOrRaise {
        raise(HttpStatusCode(418, "I'm a teapot"))
      }
    }

    val response = client.get { expectSuccess = false }

    assertSoftly {
      response.status shouldBe HttpStatusCode(418, "I'm a teapot")
      response.bodyAsBytes() shouldHaveSize 0
    }
  }

  @Test
  fun `can raise OutgoingContent from handleOrRaise`() = testApplication {
    routing {
      handleOrRaise {
        raise(TextContent("This,is,some,content", ContentType.Text.CSV, HttpStatusCode.Created))
      }
    }

    val response = client.get { expectSuccess = false }

    assertSoftly {
      response.status shouldBe HttpStatusCode.Created
      response.contentType().shouldNotBeNull().withoutParameters() shouldBe ContentType.Text.CSV
      response.bodyAsText() shouldBe "This,is,some,content"
    }
  }


  @Test
  fun `can raise string from handleOrRaise`() = testApplication {
    routing {
      handleOrRaise {
        // equivalent of `call.respond(BadRequest, "Hello world!")
        raise(HttpStatusCode.BadRequest, "Hello world!")
      }
    }

    val response = client.get { expectSuccess = false }

    assertSoftly {
      response.status shouldBe HttpStatusCode.BadRequest
      response.contentType().shouldNotBeNull().withoutParameters() shouldBe ContentType.Text.Plain
      response.bodyAsText() shouldBe "Hello world!"
    }
  }

  @Test
  fun `can raise typed response from handleOrRaise`() = testApplication {
    install(ContentNegotiation) {
      register(ContentType.Text.CSV, object : ContentConverter {
        override suspend fun deserialize(charset: Charset, typeInfo: TypeInfo, content: ByteReadChannel): Any? = null
        override suspend fun serialize(contentType: ContentType, charset: Charset, typeInfo: TypeInfo, value: Any?) =
          (value as? List<*>)?.joinToString(",")?.let { TextContent(it, contentType) }
      })
    }
    routing {
      handleOrRaise {
        raise(HttpStatusCode.BadRequest, listOf("Hello", "world!"))
      }
    }

    val response = client.get { expectSuccess = false }

    assertSoftly {
      response.status shouldBe HttpStatusCode.BadRequest
      response.contentType().shouldNotBeNull().withoutParameters() shouldBe ContentType.Text.CSV
      response.bodyAsText() shouldBe "Hello,world!"
    }
  }

  @Test
  fun `can raise custom type with error mapping from handleOrRaise`() = testApplication {
    data class MyError(val msg: String)
    routing {
      route("/three-check/{value}") {
        handleOrRaise {
          val value = call.parameters["value"]?.toIntOrNull() ?: raiseBadRequest()

          withError({ raise(HttpStatusCode.InternalServerError, it.msg) }) {
            ensure(value > 3) {
              MyError("$value is not greater than three")
            }
          }

          call.respondText("$value is win")
        }
      }
    }

    val response = client.get("/three-check/1") { expectSuccess = false }

    assertSoftly {
      response.status shouldBe HttpStatusCode.InternalServerError
      response.contentType().shouldNotBeNull().withoutParameters() shouldBe ContentType.Text.Plain
      response.bodyAsText() shouldBe "1 is not greater than three"
    }

    client.get("/three-check/4") { expectSuccess = false }.asClue {
      assertSoftly {
        it.status shouldBe HttpStatusCode.OK
        it.contentType().shouldNotBeNull().withoutParameters() shouldBe ContentType.Text.Plain
        it.bodyAsText() shouldBe "4 is win"
      }
    }
  }

}
