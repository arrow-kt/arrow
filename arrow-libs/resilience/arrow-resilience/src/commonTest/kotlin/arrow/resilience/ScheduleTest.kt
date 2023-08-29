package arrow.resilience

import arrow.atomic.AtomicLong
import arrow.atomic.updateAndGet
import arrow.core.Either
import arrow.resilience.Schedule.Decision.Continue
import arrow.resilience.Schedule.Decision.Done
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

internal data class SideEffect(var counter: Int = 0) {
  fun increment() {
    counter++
  }
}

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalTime
@Suppress("UNREACHABLE_CODE", "UNUSED_VARIABLE")
class ScheduleTest {
  class MyException : Exception()

  private val exception = MyException()

//  @Test
//  fun identity(): TestResult = runTest {
//    val identity = Schedule.identity<String>().calculateCont("test", 100)
//    assertEquals(identity.map { it.first }, (0 until 100L).map { "test" })
//    assertEquals(identity.map { it.second }, (0 until 100).map { ZERO })
//  }

//  @Test
//  fun unfold(): TestResult = runTest {
//    val unfold = Schedule.unfold<String, Long>(0) { it + 1 }.calculateCont("test", 100)
//    assertEquals(unfold.map { it.first }, (0 until 100L).toList())
//    assertEquals(unfold.map { it.second }, (0 until 100).map { ZERO })
//  }

//  @Test
//  fun forever(): TestResult = runTest {
//    val forever = Schedule.forever<String>().calculateCont("test", 100)
//    assertEquals(forever.map { it.first }, (0 until 100L).toList())
//    assertEquals(forever.map { it.second }, (0 until 100).map { ZERO })
//  }

  @Test
  fun recurs(): TestResult = runTest {
    checkRepeat(Schedule.recurs(-500), expected = 0)
    checkRepeat(Schedule.recurs(0), expected = 0)
    checkRepeat(Schedule.recurs(1), expected = 1)
  }

  @Test
  fun scheduleRecursWithPositiveNumber(): TestResult = runTest {
    val n = 500L
    val res = Schedule.recurs<Int>(n).calculateSchedule(0, n + 1)

    assertEquals(res.dropLast(1).map { ZERO }, res.dropLast(1).mapNotNull { it.delay() })
    assertEquals(res.dropLast(1).map { true }, res.dropLast(1).map { it is Continue })
    assertTrue(res.last() is Done)
  }

  @Test
  fun doWhileRepeatsWhileConditionHolds(): TestResult = runTest {
    checkRepeat(Schedule.doWhile { input, _ -> input < 10 }, expected = 10)
    checkRepeat(Schedule.doWhile { input, _ -> input > 10 }, expected = 1)
    checkRepeat(Schedule.doWhile { input, _ -> input == 1L }, expected = 2)
  }

  @Test
  fun doUntilRepeatsUntilConditionIsSatisfied(): TestResult = runTest {
    checkRepeat(Schedule.doUntil { input, _ -> input < 10 }, expected = 1)
    checkRepeat(Schedule.doUntil { input, _ -> input > 10 }, expected = 11)
    checkRepeat(Schedule.doUntil { input, _ -> input == 1L }, expected = 1)
  }

  @Test
  fun doWhileCollectCollectsAllInputsIntoAList(): TestResult = runTest {
    checkRepeat(
      Schedule.doWhile<Long> { input, _ -> input < 10L }.collect(),
      expected = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
    )
  }

  @Test
  fun doUntilCollectCollectsAllInputsIntoAList(): TestResult = runTest {
    checkRepeat(
      Schedule.doUntil<Long> { input, _ -> input > 10L }.collect(),
      expected = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    )
  }

  @Test
  fun repeatScheduledRepeatRepeatsTheWholeNumber(): TestResult = runTest {
    val n = 42L
    var count = 0L
    Schedule.recurs<Long>(1).repeat {
      Schedule.recurs<Long>(n).repeat {
        count++
      }
    }

    assertEquals(((n + 1) * 2), count)
  }

  @Test
  fun spaced(): TestResult = runTest {
    val duration = 5.seconds
    val res = Schedule.spaced<String>(duration).calculateSchedule("test", 500)

    assertTrue { res.all { it is Continue } }
    assertTrue { res.all { it.delay() == duration } }
  }

  @Test
  fun fibonacci(): TestResult = runTest {
    val n = 10L
    val res = Schedule.fibonacci<String>(10.seconds).calculateDelay("test", n)

    val fib = fibs(10.seconds).take(n.toInt()).toList()
    assertEquals(fib, res)
  }

  @Test
  fun linear(): TestResult = runTest {
    val n = 10L
    val res = Schedule.linear<String>(10.seconds).calculateDelay("test", n)

    val exp = linear(10.seconds).take(n.toInt()).toList()
    assertEquals(exp, res)
  }

  @Test
  fun exponential(): TestResult = runTest {
    val n = 10L
    val res = Schedule.exponential<String>(10.seconds).calculateDelay("test", n)

    val exp = exp(10.seconds).take(n.toInt()).toList()
    assertEquals(exp, res)
  }

  @Test
  fun repeatIsStackSafe(): TestResult = runTest {
    val iterations = 20_000L
    checkRepeat(Schedule.recurs(iterations), expected = iterations)
  }

//  @Test
//  fun repeatAsFlowIsStackSafe(): TestResult = runTest {
//    val iterations = stackSafeIteration()
//    checkRepeatAsFlow(Schedule.recurs(iterations), expected = (1..iterations).asFlow())
//  }

  @Test
  fun repeat(): TestResult = runTest {
    val stop = RuntimeException("WOOO")
    val n = 100

    val eff = SideEffect()

    val l = Either.catch {
      Schedule.forever<Unit>().repeat {
        if (eff.counter >= n) throw stop
        else eff.increment()
      }
    }

    assertEquals(100, eff.counter)
    assertEquals(Either.Left(stop), l)
  }

//  @Test
//  fun repeatAsFlow(): TestResult = runTest {
//    val stop = RuntimeException("WOOO")
//    val dec = Schedule.Decision(true, 10.nanoseconds, 0, Eval.now("state"))
//    val n = 100
//    val schedule = Schedule({ 0 }) { _: Unit, _ -> dec }
//
//    val eff = SideEffect()
//
//    val l = Either.catch {
//      schedule.repeatAsFlow {
//        if (eff.counter >= n) throw stop
//        else eff.increment()
//      }.collect()
//    }
//
//    assertEquals(100, eff.counter)
//    assertEquals(Either.Left(stop), l)
//  }

  @Test
  fun repeatFailsFastOnErrors(): TestResult = runTest {
    val ex = Throwable("Hello")
    Schedule.recurs<Int>(0).repeatOrElseEither({ throw ex }) { exc, _ -> exc }
      .fold({ assertEquals(ex, it) }, { fail("The impossible happened") })
  }

//  @Test
//  fun repeatAsFlowFailsFastOnErrors(): TestResult = runTest {
//    val ex = Throwable("Hello")
//    Schedule.recurs<Int>(0).repeatOrElseEitherAsFlow({ throw ex }, { t, _ -> t })
//      .collect { either -> either.fold({ assertEquals(ex, it) }, { fail("The impossible happened") }) }
//  }

  @Test
  fun repeatShouldRunTheScheduleWithTheCorrectInput(): TestResult = runTest {
    var i = 0L
    val n = 10L
    val result = (Schedule.recurs<Long>(n).zipRight(Schedule.collect())).repeat { i++ }

    assertEquals((0..n).toList(), result)
  }

//  @Test
//  fun repeatAsFlowShouldRunTheScheduleWithTheCorrectInput(): TestResult = runTest {
//    var i = 0
//    val n = 10
//    val result = (Schedule.recurs<Int>(n).zipRight(Schedule.collect())).repeatAsFlow { i++ }.toList()
//
//    assertEquals((0..n).map { (0..it).toList() }, result)
//  }

  @Test
  fun retryIsStackSafe(): TestResult = runTest {
    val count = AtomicLong(0)
    val iterations = 20_000L
    val l = Either.catch {
      Schedule.recurs<Throwable>(iterations).retry {
        count.updateAndGet { it + 1 }
        throw exception
      }
    }

    assertTrue { l is Either.Left && l.value is MyException }
    assertEquals(iterations + 1, count.get())
  }

  @Test
  fun retrySucceedsIfNoExceptionIsThrown(): TestResult = runTest {
    val result = Schedule.recurs<Throwable>(0).retry { 1 }
    assertEquals(1, result)
  }

  @Test
  fun retryOrElseEitherRunsTheScheduleWithCorrectInputAndRunsOrElseHandlerIfItDoesNotRetry(): TestResult = runTest {
    val ex = Throwable("Hello")
    val res = Schedule.recurs<Throwable>(0)
      .retryOrElseEither({ throw ex }) { e, _ -> e }

    res.fold({ assertEquals(ex, it) }, { fail("The impossible happened") })
  }

  @Test
  fun stopsRetryingIfFirstOfMorePredicatesIsMet(): TestResult = runTest {
    val ex = Throwable("Hello")

    val schedule = Schedule.exponential<Throwable>(1.0.milliseconds)
      .doUntil { _, output -> output > 50.0.milliseconds }
      .doUntil { input, _ -> input is IllegalStateException }

    val result: Either<Throwable, Unit> = withTimeout(10.seconds) {
      schedule.retryOrElseEither({
        throw ex
      }, { t, _ -> t })
    }

    result.fold({ assertEquals(ex, it) }, { fail("The impossible happened") })
  }
}

fun <A, B> Schedule.Decision<A, B>.delay(): Duration? = when (this) {
  is Continue -> this.delay
  is Done -> null
}

@ExperimentalTime
private fun fibs(one: Duration): Sequence<Duration> =
  generateSequence(Pair(one, one)) { (a, b) ->
    Pair(b, (a + b))
  }.map { it.first }

@ExperimentalTime
private fun exp(base: Duration): Sequence<Duration> =
  generateSequence(Pair(base, 1.0)) { (_, n) ->
    Pair(base * 2.0.pow(n), n + 1)
  }.map { it.first }

@ExperimentalTime
private fun linear(base: Duration): Sequence<Duration> =
  generateSequence(Pair(base, 1.0)) { (_, n) ->
    Pair((base * n), (n + 1))
  }.map { it.first }.drop(1)

/**
 * Calculates the schedule for [input] I, and [n] iterations
 * This allows to calculate the resulting [Schedule.Decision] state and make assertions.
 */
private suspend fun <I, A> Schedule<I, A>.calculateSchedule(input: I, n: Long): List<Schedule.Decision<I, A>> =
  buildList {
    var step = this@calculateSchedule.step
    for (i in 0 until n) {
      when (val decision = step(input)) {
        is Continue -> {
          add(decision)
          step = decision.step
        }

        is Done -> {
          add(decision)
          break
        }
      }
    }
  }

private suspend fun <I, A> Schedule<I, A>.calculateCont(input: I, n: Long): List<Pair<A, Duration>> =
  calculateSchedule(input, n).mapNotNull {
    when (it) {
      is Continue -> it.output to it.delay
      else -> null
    }
  }

private suspend fun <I, A> Schedule<I, A>.calculateDelay(input: I, n: Long): List<Duration> =
  calculateSchedule(input, n).mapNotNull { it.delay() }

private suspend fun <B> checkRepeat(schedule: Schedule<Long, B>, expected: B) {
  val count = AtomicLong(0)
  val result = schedule.repeat {
    count.updateAndGet { it + 1 }
  }

  assertEquals(expected, result)
}

private suspend fun <B> checkRepeat(schedule: Schedule<Long, List<B>>, expected: List<B>) {
  val count = AtomicLong(0)
  val result = schedule.repeat {
    count.updateAndGet { it + 1 }
  }

  assertContentEquals(expected, result)
}
