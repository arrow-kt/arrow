package arrow.resilience.ktor.client

import arrow.atomic.AtomicLong
import arrow.resilience.Schedule
import io.kotest.assertions.assertionCounter
import io.kotest.assertions.failure
import io.kotest.matchers.shouldBe
import io.kotest.mpp.bestName
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.checkAll
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.coroutines.test.runTest
import java.net.ConnectException
import kotlin.test.Test

class HttpRequestScheduleTest {
  class NotFoundExceptionForMocking : Exception()

  fun setup(
    check: suspend (counter: Long) -> Unit,
    configure: HttpRequestScheduleConfiguration.() -> Unit,
  ): Pair<AtomicLong, HttpClient> {
    val counter = AtomicLong(0)
    val engine = MockEngine { _ ->
      try {
        check(counter.incrementAndGet())
        respond(content = "OK", status = HttpStatusCode.OK)
      } catch (_: NotFoundExceptionForMocking) {
        respond(content = "Not Found", status = HttpStatusCode.NotFound)
      }
    }
    return counter to HttpClient(engine) {
      install(HttpRequestSchedule, configure)
    }
  }

  val MAX_CHECKS = 19L

  @Test fun recurs() = runTest {
    checkAll(Arb.long(0, MAX_CHECKS)) { l ->
      val (counter, client) = setup(check = { }) { repeat(Schedule.recurs(l)) }
      val response = client.get("/do")

      counter.get() shouldBe l + 1
      response.status shouldBe HttpStatusCode.OK
    }
  }

  @Test fun doWhile() = runTest {
    checkAll(Arb.long(0, MAX_CHECKS)) { l ->
      val (counter, client) = setup(check = { c ->
        if (c <= l) throw NotFoundExceptionForMocking()
      }) { repeat(Schedule.doWhile { request, _ ->
        !request.status.isSuccess()
      }) }

      val response = client.get("/do")

      counter.get() shouldBe l + 1
      response.status shouldBe HttpStatusCode.OK
    }
  }

  @Test fun retry() = runTest {
    checkAll(Arb.long(0, MAX_CHECKS)) { l ->
      val (counter, client) = setup(check = { c ->
        if (c <= l) throw ConnectException()
      }) { retry(Schedule.doWhile { throwable, _ -> throwable is ConnectException }) }

      val response = client.get("/do")

      counter.get() shouldBe l + 1
      response.status shouldBe HttpStatusCode.OK
    }
  }

  @Test fun schedule() = runTest {
    checkAll(Arb.long(1, MAX_CHECKS)) { l ->
      val (counter, client) = setup(check = { c ->
        if (c <= l) throw ConnectException()
      }) { retry(Schedule.recurs(0)) }

      shouldThrow<ConnectException> { client.get("/do") }

      val response = client.get("/do") {
        schedule {
          retry(Schedule.doWhile { throwable, _ -> throwable is ConnectException })
        }
      }

      counter.get() shouldBe l + 1
      response.status shouldBe HttpStatusCode.OK
    }
  }
}

// copied from Kotest so we can inline it
inline fun <reified T : Throwable> shouldThrow(block: () -> Any?): T {
  assertionCounter.inc()
  val expectedExceptionClass = T::class
  val thrownThrowable = try {
    block()
    null  // Can't throw failure here directly, as it would be caught by the catch clause, and it's an AssertionError, which is a special case
  } catch (thrown: Throwable) {
    thrown
  }

  return when (thrownThrowable) {
    null -> throw failure("Expected exception ${expectedExceptionClass.bestName()} but no exception was thrown.")
    is T -> thrownThrowable               // This should be before `is AssertionError`. If the user is purposefully trying to verify `shouldThrow<AssertionError>{}` this will take priority
    is AssertionError -> throw thrownThrowable
    else -> throw failure(
      "Expected exception ${expectedExceptionClass.bestName()} but a ${thrownThrowable::class.simpleName} was thrown instead.",
      thrownThrowable
    )
  }
}
