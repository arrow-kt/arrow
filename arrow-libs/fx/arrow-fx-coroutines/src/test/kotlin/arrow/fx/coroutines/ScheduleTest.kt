package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.Eval
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlin.math.pow

class ScheduleTest : StringSpec({

  class MyException : Exception()

  val exception = MyException()

  "Schedule.identity()" {
    val dec = Schedule.identity<Int>().calculateSchedule1(1)
    val expected = Schedule.Decision<Any?, Int>(true, 0.nanoseconds, Unit, Eval.now(1))

    dec eqv expected
  }

  "Schedule.unfold()" {
    val dec = Schedule.unfold<Any?, Int>(0) { it + 1 }.calculateSchedule1(0)
    val expected = Schedule.Decision<Any?, Int>(true, 0.nanoseconds, 1, Eval.now(1))

    dec eqv expected
  }

  "Schedule.forever() == Schedule.unfold(0) { it + 1 }" {
    val foreverDesc = Schedule.forever<Any?>().calculateSchedule1(0)
    val unfoldDesc = Schedule.unfold<Any?, Int>(0) { it + 1 }.calculateSchedule1(0)

    foreverDesc eqv unfoldDesc
  }

  "Schedule.recurs(negative number)" {
    checkRepeat(Schedule.recurs(-500), expected = 0)
  }

  "Schedule.recurs(0)" {
    checkRepeat(Schedule.recurs(0), expected = 0)
  }

  "Schedule.recurs(1)" {
    checkRepeat(Schedule.recurs(1), expected = 1)
  }

  "Schedule.recurs(n: Int)" {
    val n = 500
    val res = Schedule.recurs<Int>(n).calculateSchedule(0, n + 1)

    res.dropLast(1).map { it.delay.nanoseconds } shouldBe res.dropLast(1).map { 0L }
    res.dropLast(1).map { it.cont } shouldBe res.dropLast(1).map { true }

    res.last() eqv Schedule.Decision(false, 0.nanoseconds, n + 1, Eval.now(n + 1))
  }

  "Schedule.once() repeats 1 additional time" {
    var count = 0
    repeat(Schedule.once()) {
      count++
    }
    count shouldBe 2
  }

  "Schedule.doWhile repeats while condition holds and returns itself" {
    checkRepeat(Schedule.doWhile { it < 10 }, expected = 10)
    checkRepeat(Schedule.doWhile { it > 10 }, expected = 1)
    checkRepeat(Schedule.doWhile { it == 1 }, expected = 2)
  }

  "Schedule.doUntil repeats until the cond is satisfied" {
    checkRepeat(Schedule.doUntil { it < 10 }, expected = 1)
    checkRepeat(Schedule.doUntil { it > 10 }, expected = 11)
    checkRepeat(Schedule.doUntil { it == 1 }, expected = 1)
  }

  "Schedule.doWhile.collect() collects all inputs into a list" {
    checkRepeat(Schedule
      .doWhile<Int> { it < 10 }
      .collect(), expected = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9))
  }

  "Schedule.doUntil.collect() collects all inputs into a list" {
    checkRepeat(Schedule
      .doUntil<Int> { it > 10 }
      .collect(), expected = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
  }

  "Repeat a scheduled repeat repeats the whole number" {
    val n = 42
    var count = 0
    repeat(Schedule.recurs(1)) {
      repeat(Schedule.recurs(n)) {
        count++
      }
    }

    count shouldBe ((n + 1) * 2)
  }

  "Schedule.never() times out" {
    timeOutOrNull(10.milliseconds) {
      val a: Nothing = repeat(Schedule.never()) {
        1
      }
    } shouldBe null
  }

  "Schedule.spaced()" {
    val duration = 5.seconds
    val res = Schedule.spaced<Any>(duration).calculateSchedule(0, 500)

    res.map { it.cont } shouldBe res.map { true }
    res.map { it.delay.nanoseconds } shouldBe res.map { duration.nanoseconds }
  }

  "Schedule.fibonacci()" {
    val i = 10L
    val n = 10
    val res = Schedule.fibonacci<Any?>(i.seconds).calculateSchedule(0, n)

    val sum = res.fold(0L) { acc, v ->
      acc + v.delay.inSeconds
    }
    val fib = fibs(i).drop(1).take(n)

    res.all { it.cont } shouldBe true
    sum shouldBe fib.sum()
  }

  "Schedule.linear()" {
    val i = 10L
    val n = 10
    val res = Schedule.linear<Any?>(i.seconds).calculateSchedule(0, n)

    val sum = res.fold(0L) { acc, v -> acc + v.delay.inSeconds }
    val exp = linear(i).drop(1).take(n)

    res.all { it.cont } shouldBe true
    sum shouldBe exp.sum()
  }

  "Schedule.exponential()" {
    val i = 10L
    val n = 10
    val res = Schedule.exponential<Any?>(i.seconds).calculateSchedule(0, n)

    val sum = res.fold(0L) { acc, v -> acc + v.delay.inSeconds }
    val expSum = exp(i).drop(1).take(n).sum()

    res.all { it.cont } shouldBe true
    sum shouldBe expSum
  }

  "repeat is stack-safe" {
    checkRepeat(Schedule.recurs(20_000), expected = 20_000)
  }

  "repeat" {
    val stop = RuntimeException("WOOO")
    val dec = Schedule.Decision(true, 10.nanoseconds, 0, Eval.now("state"))
    val n = 100
    val schedule = Schedule({ 0 }) { _: Unit, _ -> dec }

    val eff = SideEffect()

    val l = Either.catch {
      repeat(schedule) {
        if (eff.counter >= n) throw stop
        else eff.increment()
      }
    }

    eff.counter shouldBe 100
    l shouldBe Either.Left(stop)
  }

  "retry is stack-safe" {
    val count = Atomic(0)
    val l = Either.catch {
      val n: Nothing = retry(Schedule.recurs(20_000)) {
        count.updateAndGet { it + 1 }
        throw exception
      }
    }

    l shouldBe Either.Left(exception)
    count.get() shouldBe 20_001
  }
})

private fun fibs(one: Long): Sequence<Long> = generateSequence(Pair(0L, one)) { (a, b) ->
  Pair(b, (a + b))
}.map { it.first }

private fun exp(base: Long): Sequence<Long> = generateSequence(Pair(base, 1.0)) { (_, n) ->
  Pair((base * 2.0.pow(n)).toLong(), n + 1)
}.map { it.first }

private fun linear(base: Long): Sequence<Long> = generateSequence(Pair(base, 1L)) { (_, n) ->
  Pair((base * n), (n + 1))
}.map { it.first }

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

private suspend fun <B> checkRepeat(schedule: Schedule<Int, B>, expected: B): Unit {
  val count = Atomic(0)
  repeat(schedule) {
    count.updateAndGet { it + 1 }
  } shouldBe expected
}

private infix fun <A> Schedule.Decision<Any?, A>.eqv(other: Schedule.Decision<Any?, A>): Unit {
  require(cont == other.cont) { "Decision#cont: ${this.cont} shouldBe ${other.cont}" }
  require(delay.nanoseconds == other.delay.nanoseconds) { "Decision#delay.nanoseconds: ${this.delay.nanoseconds} shouldBe ${other.delay.nanoseconds}" }
  if (cont) {
    val lh = finish.value()
    val rh = other.finish.value()
    require(lh == rh) { "Decision#cont: $lh shouldBe $rh" }
  }
}

private val Duration.inSeconds: Long
  get() = timeUnit.toSeconds(amount)
