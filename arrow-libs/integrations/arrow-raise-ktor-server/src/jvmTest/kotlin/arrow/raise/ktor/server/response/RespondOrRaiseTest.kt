package arrow.raise.ktor.server.response

import arrow.core.raise.Raise
import arrow.core.raise.context.ensure
import arrow.core.raise.context.ensureNotNull
import arrow.core.raise.context.raise
import arrow.core.raise.context.withError
import arrow.raise.ktor.server.response.Response.Companion.Response
import arrow.raise.ktor.server.response.Response.Companion.invoke
import arrow.raise.ktor.server.routing.getOrRaise
import arrow.raise.ktor.server.routing.postOrRaise
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.content.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.ktor.util.reflect.*
import io.ktor.utils.io.*
import io.ktor.utils.io.charsets.*
import org.slf4j.event.Level
import org.slf4j.event.SubstituteLoggingEvent
import org.slf4j.helpers.SubstituteLogger
import kotlin.test.Test

class RespondOrRaiseTest {
  @Test
  fun `respondOrRaise nested in get`() = testApplication {
    routing {
      get("/foo") {
        call.respondOrRaise { "bar" }
      }
    }

    client.get("/foo").let {
      it.status shouldBe OK
      it.bodyAsText() shouldBe "bar"
    }
  }

  @Test
  fun `respond within respondOrRaise logs warning`() = testApplication {
    val loggingEvents = java.util.ArrayDeque<SubstituteLoggingEvent>()
    environment { log = SubstituteLogger("", loggingEvents, false) }
    routing {
      get("/foo") {
        call.respondOrRaise {
          call.respond("bar")
          "baz"
        }
      }
    }

    client.get("/foo").let {
      it.status shouldBe OK
      it.bodyAsText() shouldBe "bar"
    }

    loggingEvents.map { it.level to it.message }
      .shouldContain(Level.WARN to CallAlreadyHandledInRespondOrRaiseBody)
  }

  @Test
  fun `respondOrRaise with custom error`() = testApplication {
    routing {
      get("/foo") {
        call.respondOrRaise({ err: Int -> BadRequest("Error:$err") }) {
          ensureNotNull(call.pathParameters["missing"]) { 123 }
        }
      }
    }

    client.get("/foo").let {
      it.status shouldBe BadRequest
      it.bodyAsText() shouldBe "Error:123"
    }
  }

  @Test
  fun `respondOrRaise Unit is NoContent`() = testApplication {
    routing {
      get("/foo") {
        call.respondOrRaise { }
      }
    }

    client.get("/foo").let {
      it.status shouldBe HttpStatusCode.NoContent
      it.bodyAsText() shouldBe ""
    }
  }

  @Test
  fun `respond result of getOrRaise`() = testApplication {
    routing {
      getOrRaise("/foo", HttpStatusCode.Created) { "bar" }
    }

    client.get("/foo").let {
      it.status shouldBe HttpStatusCode.Created
      it.bodyAsText() shouldBe "bar"
    }
  }

  @Test
  fun `raise response from getOrRaise`() = testApplication {
    routing {
      getOrRaise("/foo", HttpStatusCode.Created) {
        ensure(System.currentTimeMillis() == 0L) { BadRequest("not epoch") }
        "bar"
      }
    }

    client.get("/foo").let {
      it.status shouldBe BadRequest
      it.bodyAsText() shouldBe "not epoch"
    }
  }

  @Test
  fun `respond with empty when getOrRaise returns status code`() = testApplication {
    routing {
      getOrRaise("/foo") {
        HttpStatusCode.Created
      }
    }

    client.get("/foo").let {
      it.status shouldBe HttpStatusCode.Created
      it.bodyAsText() shouldBe ""
    }
  }

  @Test
  fun `respond with empty when getOrRaise returns unit`() = testApplication {
    routing {
      getOrRaise("/foo", statusCode = HttpStatusCode.Created) {}
    }

    client.get("/foo").let {
      it.status shouldBe HttpStatusCode.Created
      it.bodyAsText() shouldBe ""
    }
  }

  @Test
  fun `respond with NoContent when getOrRaise returns unit and no explicit status code`() = testApplication {
    routing {
      getOrRaise("/foo") {}
    }

    client.get("/foo").let {
      it.status shouldBe HttpStatusCode.NoContent
      it.bodyAsText() shouldBe ""
    }
  }

  @Test
  fun `respond with raised statusCode when getOrRaise returns status code`() = testApplication {
    routing {
      getOrRaise<Unit>("/foo", HttpStatusCode.Created) {
        ensureNotNull(emptyList<Unit>().firstOrNull()) { Response(HttpStatusCode.Conflict) }
      }
    }

    client.get("/foo").let {
      it.status shouldBe HttpStatusCode.Conflict
      it.bodyAsText() shouldBe ""
    }
  }

  @Test
  fun `receive and respond with raise`() = testApplication {
    routing {
      postOrRaise("/upper") {
        val body = call.receive<String>()
        body.uppercase()
      }
    }

    client.post("/upper") {
      setBody("hello")
    }.let {
      it.status shouldBe OK
      it.bodyAsText() shouldBe "HELLO"
    }
  }

  @Test
  fun `receive and respond typed with raise`() = testApplication {
    routing {
      postOrRaise<String, _>("/upper") { it.uppercase() }
    }

    client.post("/upper") {
      setBody("hello")
    }.let {
      it.status shouldBe OK
      it.bodyAsText() shouldBe "HELLO"
    }
  }

  @Test
  fun `custom domain error handling`() = testApplication {
    // example domain errors/service
    abstract class DomainError
    data class UserBanned(val userId: String) : DomainError()
    data class ServerError(val code: String, val message: String) : DomainError()

    data class User(val id: String)

    val userService = object {
      context(_: Raise<DomainError>)
      fun lookupUser(userId: String): User? = when (userId) {
        "bob" -> raise(UserBanned(userId))
        "alice" -> User(userId)
        else -> null
      }
    }

    // http error representation and handler
    data class ErrorPayload(val code: String, val message: String)

    fun handleError(domainError: DomainError): Response = when (domainError) {
      is UserBanned -> Unauthorized(ErrorPayload("Banned", domainError.userId))
      is ServerError -> InternalServerError(ErrorPayload("ServerError:${domainError.code}", domainError.message))
      else -> error("no local sealed class ;)")
    }

    // install basic `toString` content converter
    install(ContentNegotiation) {
      register(ContentType.Text.Plain, object : ContentConverter {
        override suspend fun deserialize(charset: Charset, typeInfo: TypeInfo, content: ByteReadChannel): Nothing = TODO()
        override suspend fun serialize(contentType: ContentType, charset: Charset, typeInfo: TypeInfo, value: Any?) =
          TextContent(value.toString(), contentType)
      })
    }

    routing {
      getOrRaise("/users/{userId?}") {
        val userId = call.pathParameters["userId"] ?: raise(BadRequest, "userId not specified")
        withError(::handleError) {
          userService.lookupUser(userId) ?: raise(NotFound)
        }
      }
    }

    client.get("/users/").let {
      it.status shouldBe BadRequest
      it.bodyAsText() shouldBe "userId not specified"
    }

    client.get("/users/alice").let {
      it.status shouldBe OK
      it.bodyAsText() shouldBe "User(id=alice)"
    }

    client.get("/users/bob").let {
      it.status shouldBe Unauthorized
      it.bodyAsText() shouldBe "ErrorPayload(code=Banned, message=bob)"
    }

    client.get("/users/carol").let {
      it.status shouldBe NotFound
      it.bodyAsText() shouldBe ""
    }
  }
}
