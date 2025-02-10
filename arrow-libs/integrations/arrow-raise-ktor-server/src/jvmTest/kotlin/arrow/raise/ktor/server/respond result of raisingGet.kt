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
  fun `respond result of raisingGet`() = testApplication {
    routing {
      raisingGet("/foo", statusCode = HttpStatusCode.Created) { "bar" }
    }

    client.get("/foo").let {
      assertSoftly {
        it.status shouldBe HttpStatusCode.Created
        it.bodyAsText() shouldBe "bar"
      }
    }
  }

  @Test
  fun `respond with empty when raisingGet returns status code`() = testApplication {
    routing {
      raisingGet("/foo") {
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
  fun `respond with empty when raisingGet returns unit`() = testApplication {
    routing {
      raisingGet("/foo", statusCode = HttpStatusCode.Created) {

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
  fun `respond with raised statusCode when raisingGet returns status code`() = testApplication {
    routing {
      raisingGet<Unit>("/foo", statusCode = HttpStatusCode.Created) {
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
}
