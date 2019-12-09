package arrow.fx.extensions

import arrow.Kind
import arrow.core.Tuple2
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.Fiber
import arrow.fx.typeclasses.FiberOf
import arrow.fx.typeclasses.FiberPartialOf
import arrow.fx.typeclasses.ForFiber
import arrow.fx.typeclasses.fix
import arrow.typeclasses.Applicative
import arrow.typeclasses.Apply
import arrow.typeclasses.Functor
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup

fun <F> Fiber.Companion.functor(C: Concurrent<F>): Functor<Kind<ForFiber, F>> =
  object : Functor<FiberPartialOf<F>> {
    override fun <A, B> FiberOf<F, A>.map(f: (A) -> B): Fiber<F, B> = C.run {
      val fiber: Fiber<F, A> = fix()
      Fiber(fiber.join().map(f), fiber.cancel())
    }
  }

fun <F> Fiber.Companion.apply(C: Concurrent<F>): Apply<Kind<ForFiber, F>> =
  object : Apply<FiberPartialOf<F>>, Functor<FiberPartialOf<F>> by functor(C) {
    override fun <A, B> Kind<FiberPartialOf<F>, A>.ap(ff: Kind<FiberPartialOf<F>, (A) -> B>): Kind<FiberPartialOf<F>, B> = C.run {
      ff.map2(fix()) { (f, a) -> f(a) }
    }

    override fun <A, B, Z> Kind<FiberPartialOf<F>, A>.map2(fb: Kind<FiberPartialOf<F>, B>, f: (Tuple2<A, B>) -> Z): Kind<FiberPartialOf<F>, Z> = C.run {
      val fa2 = fix().join().guaranteeCase {
        when (it) {
          is ExitCase.Completed -> unit()
          else -> fb.fix().cancel()
        }
      }
      val fb2 = fb.fix().join().guaranteeCase {
        when (it) {
          is ExitCase.Completed -> unit()
          else -> fix().cancel()
        }
      }

      Fiber(
        dispatchers().default().parMapN(fa2, fb2) { a, b -> f(Tuple2(a, b)) },
        tupled(fix().cancel(), fb.fix().cancel()).unit()
      )
    }
  }

fun <F> Fiber.Companion.applicative(C: Concurrent<F>): Applicative<Kind<ForFiber, F>> =
  object : Applicative<FiberPartialOf<F>>, Apply<FiberPartialOf<F>> by apply(C) {
    override fun <A> just(a: A): Kind<FiberPartialOf<F>, A> =
      Fiber(C.just(a), C.unit())
  }

fun <F, A> Fiber.Companion.semigroup(C: Concurrent<F>, S: Semigroup<A>) =
  object : Semigroup<Fiber<F, A>> {
    override fun Fiber<F, A>.combine(b: Fiber<F, A>): Fiber<F, A> = apply(C).run {
      S.run {
        map2(b) { (a, b) -> a.combine(b) }.fix()
      }
    }
  }

fun <F, A> Fiber.Companion.monoid(C: Concurrent<F>, M: Monoid<A>): Monoid<Fiber<F, A>> =
  object : Monoid<Fiber<F, A>>, Semigroup<Fiber<F, A>> by semigroup(C, M) {
    override fun empty(): Fiber<F, A> =
      Fiber(C.just(M.empty()), C.unit())
  }
