package arrow.recursion.extensions

import arrow.Kind
import arrow.core.Eval
import arrow.extension
import arrow.free.Free
import arrow.free.extensions.free.monad.flatten
import arrow.free.step
import arrow.recursion.Algebra
import arrow.recursion.data.FreeF
import arrow.recursion.data.FreeFPartialOf
import arrow.recursion.data.fix
import arrow.recursion.extensions.free.birecursive.birecursive
import arrow.recursion.extensions.freef.functor.functor
import arrow.recursion.typeclasses.Birecursive
import arrow.typeclasses.Functor

@extension
interface FreeFFunctor<E, F> : Functor<FreeFPartialOf<E, F>> {
  fun FF(): Functor<F>

  override fun <A, B> Kind<FreeFPartialOf<E, F>, A>.map(f: (A) -> B): Kind<FreeFPartialOf<E, F>, B> =
    fix().map(FF(), f)
}

@extension
interface FreeBirecursive<S, A> : Birecursive<Free<S, A>, FreeFPartialOf<A, S>> {
  fun SF(): Functor<S>
  override fun FF(): Functor<FreeFPartialOf<A, S>> = FreeF.functor(SF())

  override fun Free<S, A>.projectT(): Kind<FreeFPartialOf<A, S>, Free<S, A>> = when (val n = step()) {
    is Free.Pure -> FreeF.Pure(n.a)
    is Free.Suspend -> FreeF.Impure(SF().run { n.a.map { Free.Pure<S, A>(it) } })
    is Free.FlatMapped<*, *, *> -> when (val c = n.c) {
      is Free.Suspend -> FreeF.Impure(SF().run {
        (c.a as Kind<S, Any?>).map { (n.fm as (Any?) -> Free<S, A>).invoke(it) }
      })
      else -> throw IllegalStateException("Should be impossible by definition of step")
    }
  }

  override fun Kind<FreeFPartialOf<A, S>, Eval<Free<S, A>>>.embedT(): Eval<Free<S, A>> = when (val fa = fix()) {
    is FreeF.Pure -> Eval.now(Free.Pure(fa.e))
    is FreeF.Impure -> Eval.later { Free.liftF(SF().run { fa.fa.map { it.value() } }).flatten() }
  }
}

fun <S, A, B> Free<S, A>.interpret(SF: Functor<S>, base: (A) -> B, alg: Algebra<S, Eval<B>>): B =
  Free.birecursive<S, A>(SF).run {
    cata {
      when (val fa = it.fix()) {
        is FreeF.Pure -> Eval.now(base(fa.e))
        is FreeF.Impure -> alg(fa.fa)
      }
    }
  }