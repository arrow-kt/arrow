package arrow.raise.ktor.server.response

import arrow.raise.ktor.server.routing.getOrError
import arrow.raise.ktor.server.routing.postOrError
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.resources.Resources
import io.ktor.server.testing.it
import io.ktor.server.testing.testApplication
import io.ktor.utils.io.charsets.name
import kotlin.test.Test

class RespondOrRaiseTypedTest {

  @Resource("/foo")
  class Foo() {
    @Resource("/{name}")
    data class Id(val parent: Foo = Foo(), val name: String)
  }

  @Test
  fun `respond result of getOrError`() = testApplication {
    install(Resources)
    routing {
      getOrError<Foo, _> { "bar" }
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
      getOrError<Foo.Id, _> { it.name.uppercase() }
    }

    client.get("/foo/bar").let {
      it.status shouldBe HttpStatusCode.OK
      it.bodyAsText() shouldBe "BAR"
    }
  }

}
