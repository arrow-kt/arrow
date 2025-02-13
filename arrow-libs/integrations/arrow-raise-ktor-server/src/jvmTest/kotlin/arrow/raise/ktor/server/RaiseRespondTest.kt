package arrow.raise.ktor.server

import arrow.core.raise.ensureNotNull
import arrow.raise.ktor.server.RoutingResponse.Companion.RoutingResponse
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test

class RaiseRespondTest {
  @Test
  fun `respond result of getOrRaise`() = testApplication {
    routing {
      getOrRaise("/foo", HttpStatusCode.Created) { "bar" }
    }

    client.get("/foo").let {
      assertSoftly {
        it.status shouldBe HttpStatusCode.Created
        it.bodyAsText() shouldBe "bar"
      }
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
      assertSoftly {
        it.status shouldBe HttpStatusCode.Created
        it.bodyAsText() shouldBe ""
      }
    }
  }

  @Test
  fun `respond with empty when getOrRaise returns unit`() = testApplication {
    routing {
      getOrRaise("/foo", statusCode = HttpStatusCode.Created) {

      }
    }

    client.get("/foo").let {
      assertSoftly {
        it.status shouldBe HttpStatusCode.Created
        it.bodyAsText() shouldBe ""
      }
    }
  }

  @Test
  fun `respond with raised statusCode when getOrRaise returns status code`() = testApplication {
    routing {
      getOrRaise<Unit>("/foo", statusCode = HttpStatusCode.Created) {
        ensureNotNull(emptyList<Unit>().firstOrNull()) { RoutingResponse(HttpStatusCode.Conflict) }
      }
    }

    client.get("/foo").let {
      assertSoftly {
        it.status shouldBe HttpStatusCode.Conflict
        it.bodyAsText() shouldBe ""
      }
    }
  }


  @Test
  fun `receive and respond typed with raise`() = testApplication {
    routing {
      postOrRaise<String, _>("/upper") { body: String -> body.uppercase() }
    }

    client.post("/upper") {
      setBody("hello")
    }.let {
      assertSoftly {
        it.status shouldBe HttpStatusCode.OK
        it.bodyAsText() shouldBe "HELLO"
      }
    }
  }
}
