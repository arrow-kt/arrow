package arrow.recursion.extensions

import arrow.Kind
import arrow.Kind2
import arrow.core.Eval
import arrow.core.identity
import arrow.extension
import arrow.free.Free
import arrow.free.step
import arrow.recursion.extensions.freef.functor.functor
import arrow.recursion.pattern.ForFreeF
import arrow.recursion.pattern.FreeF
import arrow.recursion.pattern.FreeFPartialOf
import arrow.recursion.pattern.fix
import arrow.recursion.typeclasses.Birecursive
import arrow.recursion.typeclasses.Corecursive
import arrow.recursion.typeclasses.Recursive
import arrow.typeclasses.Applicative
import arrow.typeclasses.Bifunctor
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Traverse
import arrow.undocumented

@extension
@undocumented
interface FreeFFunctor<F, E> : Functor<FreeFPartialOf<F, E>> {
  fun FF(): Functor<F>

  override fun <A, B> Kind<FreeFPartialOf<F, E>, A>.map(f: (A) -> B): Kind<FreeFPartialOf<F, E>, B> =
    fix().map(FF(), f)
}

@extension
interface FreeFFoldable<F, E> : Foldable<FreeFPartialOf<F, E>> {
  fun FF(): Foldable<F>

  override fun <A, B> Kind<FreeFPartialOf<F, E>, A>.foldLeft(b: B, f: (B, A) -> B): B = when (val fa = fix()) {
    is FreeF.Pure -> b
    is FreeF.Impure -> FF().run { fa.fa.foldLeft(b, f) }
  }

  override fun <A, B> Kind<FreeFPartialOf<F, E>, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = when (val fa = fix()) {
    is FreeF.Pure -> lb
    is FreeF.Impure -> FF().run { fa.fa.foldRight(lb, f) }
  }
}

@extension
interface FreeFTraverse<F, E> : Traverse<FreeFPartialOf<F, E>>, FreeFFoldable<F, E> {
  fun TF(): Traverse<F>
  override fun FF(): Foldable<F> = TF()

  override fun <G, A, B> Kind<FreeFPartialOf<F, E>, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Kind<FreeFPartialOf<F, E>, B>> = when (val fa = fix()) {
    is FreeF.Pure -> AP.just(FreeF.Pure(fa.e))
    is FreeF.Impure -> TF().run {
      AP.run {
        fa.fa.traverse(AP, f).map { FreeF.Impure<E, F, B>(it) }
      }
    }
  }
}

@extension
interface FreeFBifunctor<F> : Bifunctor<Kind<ForFreeF, F>> {
  fun FF(): Functor<F>

  override fun <A, B, C, D> Kind2<Kind<ForFreeF, F>, A, B>.bimap(fl: (A) -> C, fr: (B) -> D): Kind2<Kind<ForFreeF, F>, C, D> = when (val fa = fix()) {
    is FreeF.Pure -> FreeF.Pure(fl(fa.e))
    is FreeF.Impure -> FreeF.Impure(FF().run { fa.fa.map(fr) })
  }
}

@extension
interface FreeBirecursive<S, A> : Birecursive<Free<S, A>, FreeFPartialOf<S, A>> {
  fun SF(): Functor<S>
  override fun FF(): Functor<FreeFPartialOf<S, A>> = FreeF.functor(SF())

  override fun Free<S, A>.projectT(): Kind<FreeFPartialOf<S, A>, Free<S, A>> = when (val n = step()) {
    is Free.Pure -> FreeF.Pure(n.a)
    is Free.Suspend -> FreeF.Impure(SF().run { n.a.map { Free.Pure<S, A>(it) } })
    is Free.FlatMapped<*, *, *> -> when (val c = n.c) {
      is Free.Suspend -> FreeF.Impure(SF().run {
        (c.a as Kind<S, Any?>).map { (n.fm as (Any?) -> Free<S, A>).invoke(it) }
      })
      else -> throw IllegalStateException("Should be impossible by definition of step")
    }
  }

  override fun Kind<FreeFPartialOf<S, A>, Free<S, A>>.embedT(): Free<S, A> = when (val fa = fix()) {
    is FreeF.Pure -> Free.Pure(fa.e)
    is FreeF.Impure -> Free.FlatMapped(Free.liftF(fa.fa), ::identity)
  }
}

@extension
interface FreeRecursive<S, A> : Recursive<Free<S, A>, FreeFPartialOf<S, A>>, FreeBirecursive<S, A> {
  override fun SF(): Functor<S>
}

@extension
interface FreeCorecursive<S, A> : Corecursive<Free<S, A>, FreeFPartialOf<S, A>>, FreeBirecursive<S, A> {
  override fun SF(): Functor<S>
}
