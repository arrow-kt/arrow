package arrow.fx.test.eq

import arrow.Kind
import arrow.core.extensions.either.eq.eq
import arrow.fx.DecisionPartialOf
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.Schedule
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.applicativeError.attempt
import arrow.fx.extensions.io.concurrent.waitFor
import arrow.fx.fix
import arrow.fx.typeclasses.Duration
import arrow.fx.typeclasses.Fiber
import arrow.fx.typeclasses.FiberOf
import arrow.fx.typeclasses.FiberPartialOf
import arrow.fx.typeclasses.fix
import arrow.fx.typeclasses.seconds
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import java.util.concurrent.TimeoutException

fun <A> IO.Companion.eq(EQA: Eq<A> = Eq.any(), timeout: Duration = 5.seconds): Eq<Kind<ForIO, A>> = Eq { a, b ->
  arrow.core.Either.eq(Eq.any(), EQA).run {
    IO.applicative().mapN(a.attempt(), b.attempt()) { (a, b) -> a.eqv(b) }
      .waitFor(timeout)
      .unsafeRunSync()
  }
}

fun IO.Companion.eqK() = object : EqK<ForIO> {
  override fun <A> Kind<ForIO, A>.eqK(other: Kind<ForIO, A>, EQ: Eq<A>): Boolean = eq(EQ).run {
    fix().eqv(other.fix())
  }
}

fun <F, A> Fiber.Companion.eq(EQ: Eq<Kind<F, A>>): Eq<FiberOf<F, A>> = object : Eq<FiberOf<F, A>> {
  override fun FiberOf<F, A>.eqv(b: FiberOf<F, A>): Boolean = EQ.run {
    fix().join().eqv(b.fix().join())
  }
}

fun Fiber.Companion.eqK() = object : EqK<FiberPartialOf<ForIO>> {
  override fun <A> Kind<FiberPartialOf<ForIO>, A>.eqK(other: Kind<FiberPartialOf<ForIO>, A>, EQ: Eq<A>): Boolean =
    IO.eq<A>().run {
      fix().join().eqv(other.fix().join())
    }
}

/**
 * Comparing Throwable is not safe due to their structure (stacktrace),
 * so we structurally compare type and message instead.
 */
fun throwableEq() = Eq { a: Throwable, b ->
  a::class == b::class && a.message == b.message
}

fun Schedule.Decision.Companion.eqK(): EqK<DecisionPartialOf<Any?>> = object : EqK<DecisionPartialOf<Any?>> {
  override fun <A> Kind<DecisionPartialOf<Any?>, A>.eqK(other: Kind<DecisionPartialOf<Any?>, A>, EQ: Eq<A>): Boolean =
    (fix() to other.fix()).let { (l, r) ->
      l.cont == r.cont &&
        l.delay.nanoseconds == r.delay.nanoseconds &&
        // don't force end result if we don't continue as it may contain Nothing
        (if (l.cont) EQ.run { l.finish.value().eqv(r.finish.value()) } else true)
    }
}

fun <A> unsafeRunEq(fa: () -> A, fb: () -> A, EQA: Eq<A> = Eq.any()): Boolean {
  val aa = try {
    fa()
  } catch (err: Throwable) {
    err
  }
  val bb = try {
    fb()
  } catch (err: Throwable) {
    err
  }
  return when {
    aa is TimeoutException -> throw aa
    bb is TimeoutException -> throw bb
    aa is Exception && bb is Exception -> throwableEq().run { aa.eqv(bb) }
    aa is Exception || bb is Exception -> false
    else -> EQA.run { (aa as A).eqv(bb as A) }
  }
}
