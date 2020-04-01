package arrow.fx.test.eq

import arrow.Kind
import arrow.core.extensions.either.eq.eq
import arrow.fx.DecisionPartialOf
import arrow.fx.IO
import arrow.fx.IOPartialOf
import arrow.fx.IOResult
import arrow.fx.Schedule
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.concurrent.waitFor
import arrow.fx.fix
import arrow.fx.typeclasses.Duration
import arrow.fx.typeclasses.Fiber
import arrow.fx.typeclasses.FiberOf
import arrow.fx.typeclasses.FiberPartialOf
import arrow.fx.typeclasses.fix
import arrow.fx.typeclasses.seconds
import arrow.fx.unsafeRunSync
import arrow.fx.test.eq
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK

fun <E, A> IO.Companion.eq(EQA: Eq<A> = Eq.any(), EQE: Eq<E> = Eq.any(), timeout: Duration = 5.seconds): Eq<Kind<IOPartialOf<E>, A>> = Eq { a, b ->
  IOResult.eq(EQE, EQA, Eq.any()).run {
    IO.applicative<Nothing>().mapN(a.fix().result(), b.fix().result()) { (a, b) -> a.eqv(b) }
      .waitFor(timeout)
      .unsafeRunSync()
  }
}

fun <E> IO.Companion.eqK(EQE: Eq<E> = Eq.any(), timeout: Duration = 5.seconds) = object : EqK<IOPartialOf<E>> {
  override fun <A> Kind<IOPartialOf<E>, A>.eqK(other: Kind<IOPartialOf<E>, A>, EQ: Eq<A>): Boolean = eq(EQ, EQE, timeout).run {
    fix().eqv(other.fix())
  }
}

fun <F, A> Fiber.Companion.eq(EQ: Eq<Kind<F, A>>): Eq<FiberOf<F, A>> = object : Eq<FiberOf<F, A>> {
  override fun FiberOf<F, A>.eqv(b: FiberOf<F, A>): Boolean = EQ.run {
    fix().join().eqv(b.fix().join())
  }
}

fun <E> Fiber.Companion.eqK() = object : EqK<FiberPartialOf<IOPartialOf<E>>> {
  override fun <A> Kind<FiberPartialOf<IOPartialOf<E>>, A>.eqK(other: Kind<FiberPartialOf<IOPartialOf<E>>, A>, EQ: Eq<A>): Boolean =
    IO.eq<E, A>().run {
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
