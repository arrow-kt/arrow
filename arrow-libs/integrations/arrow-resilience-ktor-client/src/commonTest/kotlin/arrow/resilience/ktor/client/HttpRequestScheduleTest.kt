package arrow.resilience.ktor.client

import arrow.atomic.AtomicLong
import arrow.resilience.Schedule
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.checkAll
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.isSuccess
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class HttpRequestScheduleTest {

  fun ApplicationTestBuilder.configureServer(
    check:  suspend RoutingContext.(counter: Long) -> Unit
  ): AtomicLong {
    val counter = AtomicLong(0)
    routing {
      get("/do") {
        check(counter.incrementAndGet())
        call.respond(OK)
      }
    }
    return counter
  }

  fun ApplicationTestBuilder.configureClient(
    configure: HttpRequestSchedule.Configuration.() -> Unit
  ): HttpClient = createClient { install(HttpRequestSchedule, configure) }

  val MAX_CHECKS = 10L

  @Test fun recurs() = runTestUsingDefaultDispatcher {
    checkAll(Arb.long(0, MAX_CHECKS)) { l ->
      testApplication {
        val counter = configureServer { }
        val client = configureClient {
          repeat(Schedule.recurs(l))
        }

        val response = client.get("/do")

        counter.get() shouldBe l + 1
        response.status shouldBe OK
      }
    }
  }

  @Test fun doWhile() = runTestUsingDefaultDispatcher {
    checkAll(Arb.long(0, MAX_CHECKS)) { l ->
      testApplication {
        val counter = configureServer { c ->
          if (c <= l) call.respond(NotFound)
        }
        val client = configureClient {
          repeat(Schedule.doWhile { request, _ -> !request.status.isSuccess() })
        }

        val response = client.get("/do")

        counter.get() shouldBe l + 1
        response.status shouldBe OK
      }
    }
  }

  class NetworkError : Throwable()

  @Test fun retry() = runTestUsingDefaultDispatcher {
    checkAll(Arb.long(0, MAX_CHECKS)) { l ->
      testApplication {
        val counter = configureServer { c ->
          if (c <= l) throw NetworkError()
        }
        val client = configureClient {
          retry(Schedule.doWhile { throwable, _ -> throwable is NetworkError })
        }

        val response = client.get("/do")

        counter.get() shouldBe l + 1
        response.status shouldBe OK
      }
    }
  }

  @Test fun schedule() = runTestUsingDefaultDispatcher {
    checkAll(Arb.long(1, MAX_CHECKS)) { l ->
      testApplication {
        val counter = configureServer { c ->
          if (c <= l) throw NetworkError()
        }
        val client = configureClient {
          retry(Schedule.recurs(0))
        }

        shouldThrow<NetworkError> { client.get("/do") }

        val response = client.get("/do") {
          schedule {
            retry(Schedule.doWhile { throwable, _ -> throwable is NetworkError })
          }
        }

        counter.get() shouldBe l + 1
        response.status shouldBe OK
      }
    }
  }
}

// The normal dispatcher with 'runTest' does some magic
// which doesn't go well in some platforms
fun runTestUsingDefaultDispatcher(
  timeout: Duration = 40.seconds,
  testBody: suspend TestScope.() -> Unit
): TestResult = runTest(timeout = timeout) {
  withContext(Dispatchers.Default) {
    testBody()
  }
}

