package arrow.raise.ktor.server.resources

import arrow.core.raise.context.ensure
import arrow.raise.ktor.server.response.raise
import arrow.core.raise.context.raise
import arrow.raise.ktor.server.resources.RoutingResourcesTest.Foo
import arrow.raise.ktor.server.response.Response.Companion.invoke
import io.kotest.matchers.shouldBe
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.resources.Resources
import io.ktor.server.response.respond
import io.ktor.server.testing.testApplication
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test

class RoutingReceivingResourcesTest {
  @Test
  fun `receive and respond typed success with string body and params`() = testApplication {
    install(Resources)
    routing {
      patchOrRaise<Foo.Id, _, _> { route, body: String ->
        call.respond(body + route.name)
      }
    }
    client.patch("/foo/bar") {
      setBody("foo")
    }.let {
      it.status shouldBe HttpStatusCode.OK
      it.bodyAsText() shouldBe "foobar"
    }
  }

  @Test
  fun `receive and respond typed with raise`() = testApplication {
    install(Resources)
    routing {
      postOrRaise<Foo, _, String> { _, body: String ->
        raise(HttpStatusCode.BadRequest(body))
      }
    }

    client.post("/foo") {
      setBody("hello")
    }.let {
      it.status shouldBe HttpStatusCode.BadRequest
      it.bodyAsText() shouldBe "hello"
    }
  }

  @Test
  fun `receive with typed body and content negotiation`() = testApplication {
    install(Resources)
    install(ContentNegotiation) {
      json(Json.Default)
    }
    @Serializable
    data class Body(val id: String, val num: Int)

    @Serializable
    data class OtherBody(val name: String)

    val defaultBody = Body("1", 1)
    routing {
      postOrRaise { _: Foo, body: Body ->
        ensure(body.num > 0) { HttpStatusCode.Conflict("Bad") }
        body
      }
    }
    // installing content negotiation on the test client wouldnt work, manually encode and decode
    client.post("/foo") { contentType(ContentType.Application.Json); setBody(Json.encodeToString(defaultBody)) }
      .let {
        it.status shouldBe HttpStatusCode.OK
        Json.decodeFromString<Body>(it.bodyAsText()) shouldBe defaultBody
      }
    // Ensure raising error in response returns bad code
    client.post("/foo") {
      contentType(ContentType.Application.Json); setBody(
      Json.encodeToString(
        Body(
          "",
          -1
        )
      )
    )
    }.let {
      it.bodyAsText() shouldBe "Bad"
      it.status shouldBe HttpStatusCode.Conflict
    }
    // Ensures sending wrong body type is handled
    client.post("/foo") { contentType(ContentType.Application.Json); setBody(Json.encodeToString(OtherBody("name"))) }
      .let {
        it.bodyAsText() shouldBe ""
        it.status shouldBe HttpStatusCode.BadRequest
      }
  }

  @Test
  fun `all http methods receive and  respond successfully`() = testApplication {
    install(Resources)
    routing {
      patchOrRaise<Foo.Id, _, _> { route, body: String -> route.name.uppercase() + body.uppercase() }
      postOrRaise<Foo.Id, _, _> { route, body: String -> route.name.uppercase() + body.uppercase() }
      putOrRaise<Foo.Id, _, _> { route, body: String -> route.name.uppercase() + body.uppercase() }
    }
    listOf(
      client.patch("/foo/bar") { setBody("bazz") },
      client.post("/foo/bar") { setBody("bazz") },
      client.put("/foo/bar") { setBody("bazz") },
    ).forEach {
      it.status shouldBe HttpStatusCode.OK
      it.bodyAsText() shouldBe "BARBAZZ"
    }
  }

  @Test
  fun `all http methods respond and receive with error`() = testApplication {
    install(Resources)
    routing {
      patchOrRaise<Foo.Id, String, String> { _, _ -> raise(HttpStatusCode.BadRequest, "BAR") }
      postOrRaise<Foo.Id, String, String> { _, _ -> raise(HttpStatusCode.BadRequest, "BAR") }
      putOrRaise<Foo.Id, String, String> { _, _ -> raise(HttpStatusCode.BadRequest, "BAR") }
    }
    listOf(
      client.patch("/foo/bar"),
      client.post("/foo/bar"),
      client.put("/foo/bar"),
    ).forEach {
      it.status shouldBe HttpStatusCode.BadRequest
      it.bodyAsText() shouldBe "BAR"
    }
  }
}
