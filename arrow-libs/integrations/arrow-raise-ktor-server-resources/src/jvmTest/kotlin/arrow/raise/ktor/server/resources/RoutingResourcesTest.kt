package arrow.raise.ktor.server.resources

import arrow.raise.ktor.server.response.raise
import io.kotest.matchers.shouldBe
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.client.request.options
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.resources.Resources
import io.ktor.server.testing.testApplication
import kotlin.test.Test

class RoutingResourcesTest {

  @Resource("/foo")
  class Foo {
    @Resource("/{name}")
    data class Id(val parent: Foo = Foo(), val name: String)
  }

  @Test
  fun `respond result of getOrError`() = testApplication {
    install(Resources)
    routing {
      getOrRaise<Foo, _> { "bar" }
    }

    client.get("/foo").let {
      it.status shouldBe HttpStatusCode.OK
      it.bodyAsText() shouldBe "bar"
    }
  }

  @Test
  fun `respond result with params of getOrError`() = testApplication {
    install(Resources)
    routing {
      getOrRaise<Foo.Id, _> { it.name.uppercase() }
    }

    client.get("/foo/bar").let {
      it.status shouldBe HttpStatusCode.OK
      it.bodyAsText() shouldBe "BAR"
    }
  }

  @Test
  fun `all http methods respond successfully`() = testApplication {
    install(Resources)
    routing {
      getOrRaise<Foo.Id, _> { it.name.uppercase() }
      deleteOrRaise<Foo.Id, _> { it.name.uppercase() }
      optionsOrRaise<Foo.Id, _> { it.name.uppercase() }
      patchOrRaise<Foo.Id, _> { it.name.uppercase() }
      postOrRaise<Foo.Id, _> { it.name.uppercase() }
      putOrRaise<Foo.Id, _> { it.name.uppercase() }
    }
    listOf(
      client.get("/foo/bar"),
      client.delete("/foo/bar"),
      client.options("/foo/bar"),
      client.patch("/foo/bar"),
      client.post("/foo/bar"),
      client.put("/foo/bar"),
    ).forEach {
      it.status shouldBe HttpStatusCode.OK
      it.bodyAsText() shouldBe "BAR"
    }
  }

  @Test
  fun `all http methods respond with error`() = testApplication {
    install(Resources)
    routing {
      getOrRaise<Foo.Id, String> { raise(HttpStatusCode.BadRequest, "BAR") }
      headOrRaise<Foo.Id, String> { raise(HttpStatusCode.BadRequest, "BAR") }
      deleteOrRaise<Foo.Id, String> { raise(HttpStatusCode.BadRequest, "BAR") }
      optionsOrRaise<Foo.Id, String> { raise(HttpStatusCode.BadRequest, "BAR") }
      patchOrRaise<Foo.Id, String> { raise(HttpStatusCode.BadRequest, "BAR") }
      postOrRaise<Foo.Id, String> { raise(HttpStatusCode.BadRequest, "BAR") }
      putOrRaise<Foo.Id, String> { raise(HttpStatusCode.BadRequest, "BAR") }
    }
    listOf(
      client.get("/foo/bar"),
      client.head("/foo/bar"),
      client.delete("/foo/bar"),
      client.options("/foo/bar"),
      client.patch("/foo/bar"),
      client.post("/foo/bar"),
      client.put("/foo/bar"),
    ).forEach {
      it.status shouldBe HttpStatusCode.BadRequest
      it.bodyAsText() shouldBe "BAR"
    }
  }
}
