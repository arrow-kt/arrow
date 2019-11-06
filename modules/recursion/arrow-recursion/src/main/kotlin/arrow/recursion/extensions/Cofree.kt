package arrow.recursion.extensions

import arrow.Kind
import arrow.Kind2
import arrow.core.Eval
import arrow.extension
import arrow.free.Cofree
import arrow.recursion.extensions.cofreef.functor.functor
import arrow.recursion.pattern.CofreeF
import arrow.recursion.pattern.CofreeFPartialOf
import arrow.recursion.pattern.ForCofreeF
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
interface CofreeFFunctor<F, I> : Functor<CofreeFPartialOf<F, I>> {
  override fun <B, C> Kind<CofreeFPartialOf<F, I>, B>.map(f: (B) -> C): Kind<CofreeFPartialOf<F, I>, C> =
    fix().map(f)
}

@extension
interface CofreeFFoldable<F, I> : Foldable<CofreeFPartialOf<F, I>> {
  fun FF(): Foldable<F>

  override fun <A, B> Kind<CofreeFPartialOf<F, I>, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().let {
      FF().run { it.tail.foldLeft(b, f) }
    }

  override fun <A, B> Kind<CofreeFPartialOf<F, I>, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().let {
      FF().run { it.tail.foldRight(lb, f) }
    }
}

@extension
interface CofreeFTraverse<F, I> : Traverse<CofreeFPartialOf<F, I>>, CofreeFFoldable<F, I> {
  fun TF(): Traverse<F>
  override fun FF(): Foldable<F> = TF()

  override fun <G, A, B> Kind<CofreeFPartialOf<F, I>, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Kind<CofreeFPartialOf<F, I>, B>> =
    fix().let { co ->
      TF().run {
        co.tail.traverse(AP, f).let {
          AP.run {
            it.map {
              CofreeF(co.FF, co.head, it)
            }
          }
        }
      }
    }
}

@extension
interface CofreeFBifunctor<F> : Bifunctor<Kind<ForCofreeF, F>> {
  override fun <A, B, C, D> Kind2<Kind<ForCofreeF, F>, A, B>.bimap(fl: (A) -> C, fr: (B) -> D): Kind2<Kind<ForCofreeF, F>, C, D> =
    fix().let {
      CofreeF(it.FF, fl(it.head), it.FF.run { it.tail.map(fr) })
    }
}

@extension
interface CofreeRecursive<S, A> : Recursive<Cofree<S, A>, CofreeFPartialOf<S, A>> {
  fun SF(): Functor<S>
  override fun FF(): Functor<CofreeFPartialOf<S, A>> = CofreeF.functor()

  override fun Cofree<S, A>.projectT(): Kind<CofreeFPartialOf<S, A>, Cofree<S, A>> =
    CofreeF(SF(), head, tail.value())
}

@extension
interface CofreeCorecursive<S, A> : Corecursive<Cofree<S, A>, CofreeFPartialOf<S, A>> {
  fun SF(): Functor<S>
  override fun FF(): Functor<CofreeFPartialOf<S, A>> = CofreeF.functor()

  override fun Kind<CofreeFPartialOf<S, A>, Cofree<S, A>>.embedT(): Cofree<S, A> = fix().run {
    Cofree(SF(), head, Eval.now(tail))
  }
}

@extension
interface CofreeBirecursive<S, A> : Birecursive<Cofree<S, A>, CofreeFPartialOf<S, A>>, CofreeRecursive<S, A>, CofreeCorecursive<S, A> {
  override fun SF(): Functor<S>
  override fun FF(): Functor<CofreeFPartialOf<S, A>> = CofreeF.functor()
}
