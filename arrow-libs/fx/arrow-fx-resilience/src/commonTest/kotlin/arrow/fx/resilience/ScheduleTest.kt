package arrow.fx.resilience

import arrow.core.Either
import arrow.core.Eval
import arrow.core.continuations.AtomicRef
import arrow.core.continuations.updateAndGet
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.pow
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.DurationUnit

internal data class SideEffect(var counter: Int = 0) {
  fun increment() {
    counter++
  }
}

data class JustANumber(val n: Int) {
  fun increment(): JustANumber = copy(n = n + 1)
}

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalTime
class ScheduleTest {
  class MyException : Exception()

  val exception = MyException()

  @Test
  fun scheduleIdentity(): TestResult = runTest {
    val dec = Schedule.identity<Int>().calculateSchedule1(1)
    val expected = Schedule.Decision<Any?, Int>(true, ZERO, Unit, Eval.now(1))

    dec eqv expected
  }

  @Test
  fun scheduleUnfold(): TestResult = runTest {
    val dec = Schedule.unfold<Any?, Int>(0) { it + 1 }.calculateSchedule1(0)
    val expected = Schedule.Decision<Any?, Int>(true, ZERO, 1, Eval.now(1))

    dec eqv expected
  }

  // schedule.forever() == Schedule.unfold(0) { it + 1 }
  @Test
  fun scheduleForever(): TestResult = runTest {
    val foreverDesc = Schedule.forever<Any?>().calculateSchedule1(0)
    val unfoldDesc = Schedule.unfold<Any?, Int>(0) { it + 1 }.calculateSchedule1(0)

    foreverDesc eqv unfoldDesc
  }

  @Test
  fun scheduleRecursWithNegativeNumber(): TestResult = runTest {
    checkRepeat(Schedule.recurs(-500), expected = 0)
  }

  @Test
  fun scheduleRecursWithZero(): TestResult = runTest {
    checkRepeat(Schedule.recurs(0), expected = 0)
  }

  @Test
  fun scheduleRecursWithOne(): TestResult = runTest {
    checkRepeat(Schedule.recurs(1), expected = 1)
  }

  @Test
  fun scheduleRecursWithPositiveNumber(): TestResult = runTest {
    val n = 500
    val res = Schedule.recurs<Int>(n).calculateSchedule(0, n + 1)

    assertEquals(res.dropLast(1).map { ZERO }, res.dropLast(1).map { it.duration })
    assertEquals(res.dropLast(1).map { true }, res.dropLast(1).map { it.cont })

    res.last() eqv Schedule.Decision(false, ZERO, n + 1, Eval.now(n + 1))
  }

  @Test
  fun scheduleOnceRepeatsOneAdditionalTime(): TestResult = runTest {
    var count = 0
    Schedule.once<Int>().repeat {
      count++
    }
    assertEquals(2, count)
  }

  @Test
  fun scheduleDoWhileRepeatsWhileConditionHolds(): TestResult = runTest {
    checkRepeat(Schedule.doWhile { it < 10 }, expected = 10)
    checkRepeat(Schedule.doWhile { it > 10 }, expected = 1)
    checkRepeat(Schedule.doWhile { it == 1 }, expected = 2)
  }

  @Test
  fun scheduleDoUntilRepeatsUntilConditionIsSatisfied(): TestResult = runTest {
    checkRepeat(Schedule.doUntil { it < 10 }, expected = 1)
    checkRepeat(Schedule.doUntil { it > 10 }, expected = 11)
    checkRepeat(Schedule.doUntil { it == 1 }, expected = 1)
  }

  @Test
  fun scheduleDoWhileCollectCollectsAllInputsIntoAList(): TestResult = runTest {
    checkRepeat(
      Schedule
        .doWhile<Int> { it < 10 }
        .collect(),
      expected = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
    )
  }

  @Test
  fun scheduleDoUntilCollectCollectsAllInputsIntoAList(): TestResult = runTest {
    checkRepeat(
      Schedule
        .doUntil<Int> { it > 10 }
        .collect(),
      expected = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    )
  }

  @Test
  fun repeatScheduledRepeatRepeatsTheWholeNumber(): TestResult = runTest {
    val n = 42
    var count = 0
    Schedule.recurs<Int>(1).repeat {
      Schedule.recurs<Int>(n).repeat {
        count++
      }
    }

    assertEquals(((n + 1) * 2), count)
  }

  @Suppress("UNREACHABLE_CODE", "UNUSED_VARIABLE")
  @Test
  fun scheduleNeverTimesOut(): TestResult = runTest {
    val result = withTimeoutOrNull(10.milliseconds) {
      val a: Nothing = Schedule.never<Int>().repeat {
        1
      }
    }
    assertNull(result)
  }

  @Test
  fun scheduleSpaced(): TestResult = runTest {
    val duration = 5.seconds
    val res = Schedule.spaced<Any>(duration).calculateSchedule(0, 500)

    assertTrue { res.all { it.cont } }
    assertTrue { res.all { it.duration == duration } }
    assertContentEquals(res.map { true }, res.map { it.cont })
    assertContentEquals(res.map { duration }, res.map { it.duration })
  }

  fun secondsToNanos(sec: Int): Double =
    sec * 1_000_000_000.0

  @Test
  fun scheduleFibonacci(): TestResult = runTest {
    val n = 10
    val res = Schedule.fibonacci<Any?>(10.seconds).calculateSchedule(0, n)

    val sum = res.fold(ZERO) { acc, v -> acc + v.duration }
    val fib = fibs(secondsToNanos(10)).drop(1).take(n)

    assertTrue { res.all { it.cont } }
    assertEquals(fib.sum(), sum.toDouble(DurationUnit.NANOSECONDS))
  }

  @Test
  fun scheduleLinear(): TestResult = runTest {
    val n = 10
    val res = Schedule.linear<Any?>(10.seconds).calculateSchedule(0, n)

    val sum = res.fold(ZERO) { acc, v -> acc + v.duration }
    val exp = linear(secondsToNanos(10)).drop(1).take(n)

    assertTrue { res.all { it.cont } }
    assertEquals(exp.sum(), sum.toDouble(DurationUnit.NANOSECONDS))
  }

  @Test
  fun scheduleExponential(): TestResult = runTest {
    val n = 10
    val res = Schedule.exponential<Any?>(10.seconds).calculateSchedule(0, n)

    val sum = res.fold(ZERO) { acc, v -> acc + v.duration }
    val exp = exp(secondsToNanos(10)).drop(1).take(n)

    assertTrue { res.all { it.cont } }
    assertEquals(exp.sum(), sum.toDouble(DurationUnit.NANOSECONDS))
  }

  @Test
  fun repeatIsStackSafe(): TestResult = runTest {
    checkRepeat(Schedule.recurs(20_000), expected = 20_000)
  }

  @Test
  fun repeatAsFlowIsStackSafe(): TestResult = runTest {
    checkRepeatAsFlow(Schedule.recurs(500_000), expected = (1..500_000).asFlow())
  }

  @Test
  fun repeat(): TestResult = runTest {
    val stop = RuntimeException("WOOO")
    val dec = Schedule.Decision(true, 10.nanoseconds, 0, Eval.now("state"))
    val n = 100
    val schedule = Schedule({ 0 }) { _: Unit, _ -> dec }

    val eff = SideEffect()

    val l = Either.catch {
      schedule.repeat {
        if (eff.counter >= n) throw stop
        else eff.increment()
      }
    }

    assertEquals(100, eff.counter)
    assertEquals(Either.Left(stop), l)
  }

  @Test
  fun repeatAsFlow(): TestResult = runTest {
    val stop = RuntimeException("WOOO")
    val dec = Schedule.Decision(true, 10.nanoseconds, 0, Eval.now("state"))
    val n = 100
    val schedule = Schedule({ 0 }) { _: Unit, _ -> dec }

    val eff = SideEffect()

    val l = Either.catch {
      schedule.repeatAsFlow {
        if (eff.counter >= n) throw stop
        else eff.increment()
      }.collect()
    }

    assertEquals(100, eff.counter)
    assertEquals(Either.Left(stop), l)
  }

  @Test
  fun repeatFailsFastOnErrors(): TestResult = runTest {
    val ex = Throwable("Hello")
    Schedule.recurs<Int>(0).repeatOrElseEither({ throw ex }) { exc, _ -> exc }
      .fold({ assertEquals(ex, it) }, { fail("The impossible happened") })
  }

  @Test
  fun repeatAsFlowFailsFastOnErrors(): TestResult = runTest {
    val ex = Throwable("Hello")
    Schedule.recurs<Int>(0).repeatOrElseEitherAsFlow({ throw ex }, { t, _ -> t })
      .collect { either -> either.fold({ assertEquals(ex, it) }, { fail("The impossible happened") }) }
  }

  @Test
  fun repeatShouldRunTheScheduleWithTheCorrectInput(): TestResult = runTest {
    var i = 0
    val n = 10
    val result = (Schedule.recurs<Int>(n).zipRight(Schedule.collect())).repeat { i++ }

    assertEquals((0..n).toList(), result)
  }

  @Test
  fun repeatAsFlowShouldRunTheScheduleWithTheCorrectInput(): TestResult = runTest {
    var i = 0
    val n = 10
    val result = (Schedule.recurs<Int>(n).zipRight(Schedule.collect())).repeatAsFlow { i++ }.toList()

    assertEquals((0..n).map { (0..it).toList() }, result)
  }

  @Test
  fun retryIsStackSafe(): TestResult = runTest {
    val count = AtomicRef(JustANumber(0))
    val l = Either.catch {
      Schedule.recurs<Throwable>(20_000).retry {
        count.updateAndGet(JustANumber::increment)
        throw exception
      }
    }

    assertTrue { l is Either.Left && l.value is MyException }
    assertEquals(20_001, count.get().n)
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
  fun scheduleStopsRetryingIfFirstOfMorePredicatesIsMet(): TestResult = runTest {
    val ex = Throwable("Hello")

    val schedule = Schedule.exponential<Throwable>(1.0.milliseconds)
      .untilOutput { it > 50.0.milliseconds }
      .untilInput<Throwable> { it is IllegalStateException }

    val result: Either<Throwable, Unit> = withTimeout(10.seconds) {
      schedule.retryOrElseEither({
        throw ex
      }, { t, _ -> t })
    }

    result.fold({ assertEquals(ex, it) }, { fail("The impossible happened") })
  }
}

@ExperimentalTime
private fun fibs(one: Double): Sequence<Double> =
  generateSequence(Pair(0.0, one)) { (a, b) ->
    Pair(b, (a + b))
  }.map { it.first }

@ExperimentalTime
private fun exp(base: Double): Sequence<Double> =
  generateSequence(Pair(base, 1.0)) { (_, n) ->
    Pair(base * 2.0.pow(n), n + 1)
  }.map { it.first }

@ExperimentalTime
private fun linear(base: Double): Sequence<Double> =
  generateSequence(Pair(base, 1.0)) { (_, n) ->
    Pair((base * n), (n + 1))
  }.map { it.first }

@ExperimentalTime
internal fun Sequence<Double>.sum(): Double {
  var sum = 0.0
  for (element in this) {
    sum += element
  }
  return sum
}

private suspend fun <I, A> Schedule<I, A>.calculateSchedule1(input: I): Schedule.Decision<Any?, A> =
  calculateSchedule(input, 1).first()

/**
 * Calculates the schedule for [input] I, and [n] iterations
 * This allows to calculate the resulting [Schedule.Decision] state and make assertions.
 */
@Suppress("UNCHECKED_CAST")
private suspend fun <I, A> Schedule<I, A>.calculateSchedule(input: I, n: Int): List<Schedule.Decision<Any?, A>> {
  (this as Schedule.ScheduleImpl<Any?, I, A>)
  val state = initialState.invoke()
  return go(this, input, state, n, emptyList())
}

private tailrec suspend fun <I, A> go(
  schedule: Schedule.ScheduleImpl<Any?, I, A>,
  input: I,
  s: Any?,
  rem: Int,
  acc: List<Schedule.Decision<Any?, A>>
): List<Schedule.Decision<Any?, A>> =
  if (rem <= 0) acc
  else {
    val res = schedule.update(input, s) // Calculate new decision
    go(schedule, input, res.state, rem - 1, acc + listOf(res))
  }

private suspend fun <B> checkRepeat(schedule: Schedule<Int, B>, expected: B) {
  val count = AtomicRef(JustANumber(0))
  val result = schedule.repeat {
    count.updateAndGet(JustANumber::increment).n
  }

  assertEquals(expected, result)
}

private suspend fun <B> checkRepeatAsFlow(schedule: Schedule<Int, B>, expected: Flow<B>) {
  val count = AtomicRef(JustANumber(0))
  schedule.repeatAsFlow {
    count.updateAndGet(JustANumber::increment).n
  }.zip(expected, ::Pair)
    .collect { (a, b) -> assertEquals(b, a) }
}

@ExperimentalTime
private infix fun <A> Schedule.Decision<Any?, A>.eqv(other: Schedule.Decision<Any?, A>) {
  require(cont == other.cont) { "Decision#cont: ${this.cont} shouldBe ${other.cont}" }
  require(duration == other.duration) { "Decision#duration: ${this.duration} shouldBe ${other.duration}" }
  if (cont) {
    val lh = finish.value()
    val rh = other.finish.value()
    require(lh == rh) { "Decision#cont: $lh shouldBe $rh" }
  }
}
