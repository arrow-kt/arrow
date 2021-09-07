package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.Eval
import io.kotest.assertions.fail
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.pow
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds
import kotlin.time.nanoseconds
import kotlin.time.seconds

@ExperimentalTime
class ScheduleTest : ArrowFxSpec(
  spec = {
    class MyException : Exception()

    val exception = MyException()

    "Schedule.identity()" {
      val dec = Schedule.identity<Int>().calculateSchedule1(1)
      val expected = Schedule.Decision<Any?, Int>(true, 0.0, Unit, Eval.now(1))

      dec eqv expected
    }

    "Schedule.unfold()" {
      val dec = Schedule.unfold<Any?, Int>(0) { it + 1 }.calculateSchedule1(0)
      val expected = Schedule.Decision<Any?, Int>(true, 0.0, 1, Eval.now(1))

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

      res.dropLast(1).map { it.delayInNanos.nanoseconds } shouldBe res.dropLast(1).map { 0.nanoseconds }
      res.dropLast(1).map { it.cont } shouldBe res.dropLast(1).map { true }

      res.last() eqv Schedule.Decision(false, 0.0, n + 1, Eval.now(n + 1))
    }

    "Schedule.once() repeats 1 additional time" {
      var count = 0
      Schedule.once<Int>().repeat {
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
      checkRepeat(
        Schedule
          .doWhile<Int> { it < 10 }
          .collect(),
        expected = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
      )
    }

    "Schedule.doUntil.collect() collects all inputs into a list" {
      checkRepeat(
        Schedule
          .doUntil<Int> { it > 10 }
          .collect(),
        expected = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
      )
    }

    "Repeat a scheduled repeat repeats the whole number" {
      val n = 42
      var count = 0
      Schedule.recurs<Int>(1).repeat {
        Schedule.recurs<Int>(n).repeat {
          count++
        }
      }

      count shouldBe ((n + 1) * 2)
    }

    "Schedule.never() times out" {
      withTimeoutOrNull(10.milliseconds) {
        val a: Nothing = Schedule.never<Int>().repeat {
          1
        }
      } shouldBe null
    }

    "Schedule.spaced()" {
      val duration = 5.seconds
      val res = Schedule.spaced<Any>(duration).calculateSchedule(0, 500)

      res.map { it.cont } shouldBe res.map { true }
      res.map { it.delayInNanos.nanoseconds } shouldBe res.map { duration }
    }

    fun secondsToNanos(sec: Int): Double =
      sec * 1_000_000_000.0

    "Schedule.fibonacci()" {
      val i = secondsToNanos(10)
      val n = 10
      val res = Schedule.fibonacci<Any?>(i).calculateSchedule(0, n)

      val sum = res.fold(0.0) { acc, v ->
        acc + v.delayInNanos
      }
      val fib = fibs(i).drop(1).take(n)

      res.all { it.cont } shouldBe true
      sum shouldBe fib.sum()
    }

    "Schedule.linear()" {
      val i = secondsToNanos(10)
      val n = 10
      val res = Schedule.linear<Any?>(i).calculateSchedule(0, n)

      val sum = res.fold(0.0) { acc, v -> acc + v.delayInNanos }
      val exp = linear(i).drop(1).take(n)

      res.all { it.cont } shouldBe true
      sum shouldBe exp.sum()
    }

    "Schedule.exponential()" {
      val i = secondsToNanos(10)
      val n = 10
      val res = Schedule.exponential<Any?>(i).calculateSchedule(0, n)

      val sum = res.fold(0.0) { acc, v -> acc + v.delayInNanos }
      val expSum = exp(i).drop(1).take(n).sum()

      res.all { it.cont } shouldBe true
      sum shouldBe expSum
    }

    "repeat is stack-safe" {
      checkRepeat(Schedule.recurs(20_000), expected = 20_000)
    }

    "repeat" {
      val stop = RuntimeException("WOOO")
      val dec = Schedule.Decision(true, 10.0, 0, Eval.now("state"))
      val n = 100
      val schedule = Schedule({ 0 }) { _: Unit, _ -> dec }

      val eff = SideEffect()

      val l = Either.catch {
        schedule.repeat {
          if (eff.counter >= n) throw stop
          else eff.increment()
        }
      }

      eff.counter shouldBe 100
      l shouldBe Either.Left(stop)
    }

    "repeat fails fast on errors" {
      val ex = Throwable("Hello")
      Schedule.recurs<Int>(0).repeatOrElseEither({ throw ex }) { exc, _ -> exc }
        .fold({ it shouldBe ex }, { fail("The impossible happened") })
    }

    "repeat should run the schedule with the correct input" {
      var i = 0
      (Schedule.recurs<Int>(10).zipRight(Schedule.collect())).repeat { i++ } shouldBe (0..10).toList()
    }

    "retry is stack-safe" {
      val count = Atomic(0)
      val l = Either.catch {
        Schedule.recurs<Throwable>(20_000).retry {
          count.updateAndGet { it + 1 }
          throw exception
        }
      }

      l should leftException(exception)
      count.get() shouldBe 20_001
    }

    "retry succeeds if no exception is thrown" {
      Schedule.recurs<Throwable>(0).retry { 1 } shouldBe 1
    }

    "retryOrElseEither runs the schedule with the correct input and runs the orElse handler if it does not retry" {
      val ex = Throwable("Hello")
      val res = Schedule.recurs<Throwable>(0)
        .retryOrElseEither({ throw ex }) { e, _ -> e }

      res.fold({ it shouldBe ex }, { fail("The impossible happened") })
    }
  }
)

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

private suspend fun <B> checkRepeat(schedule: Schedule<Int, B>, expected: B): Unit {
  val count = Atomic(0)
  schedule.repeat {
    count.updateAndGet { it + 1 }
  } shouldBe expected
}

@ExperimentalTime
private infix fun <A> Schedule.Decision<Any?, A>.eqv(other: Schedule.Decision<Any?, A>): Unit {
  require(cont == other.cont) { "Decision#cont: ${this.cont} shouldBe ${other.cont}" }
  require(delayInNanos.nanoseconds == other.delayInNanos.nanoseconds) { "Decision#delay.nanoseconds: ${this.delayInNanos.nanoseconds} shouldBe ${other.delayInNanos.nanoseconds}" }
  if (cont) {
    val lh = finish.value()
    val rh = other.finish.value()
    require(lh == rh) { "Decision#cont: $lh shouldBe $rh" }
  }
}
