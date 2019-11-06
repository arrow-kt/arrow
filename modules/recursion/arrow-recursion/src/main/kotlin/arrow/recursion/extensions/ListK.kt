package arrow.recursion.extensions

import arrow.Kind
import arrow.Kind2
import arrow.core.Eval
import arrow.core.ListK
import arrow.core.combineK
import arrow.core.k
import arrow.extension
import arrow.recursion.extensions.listf.functor.functor
import arrow.recursion.pattern.ForListF
import arrow.recursion.pattern.ListF
import arrow.recursion.pattern.ListFPartialOf
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
interface ListFFunctor<I> : Functor<ListFPartialOf<I>> {
  override fun <R, S> Kind<ListFPartialOf<I>, R>.map(f: (R) -> S): Kind<ListFPartialOf<I>, S> =
    fix().map(f)
}

@extension
interface ListFFoldable<I> : Foldable<ListFPartialOf<I>> {
  override fun <A, B> Kind<ListFPartialOf<I>, A>.foldLeft(b: B, f: (B, A) -> B): B = when (val l = fix()) {
    is ListF.NilF -> b
    is ListF.ConsF -> f(b, l.tail)
  }

  override fun <A, B> Kind<ListFPartialOf<I>, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = when (val l = fix()) {
    is ListF.NilF -> lb
    is ListF.ConsF -> f(l.tail, lb)
  }
}

@extension
interface ListFTraverse<I> : Traverse<ListFPartialOf<I>>, ListFFoldable<I> {
  override fun <G, A, B> Kind<ListFPartialOf<I>, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Kind<ListFPartialOf<I>, B>> = when (val l = fix()) {
    is ListF.NilF -> AP.just(ListF.NilF())
    is ListF.ConsF -> AP.run { f(l.tail).map { ListF.ConsF(l.a, it) } }
  }
}

@extension
interface ListFBifunctor : Bifunctor<ForListF> {
  override fun <A, B, C, D> Kind2<ForListF, A, B>.bimap(fl: (A) -> C, fr: (B) -> D): Kind2<ForListF, C, D> = when (val l = fix()) {
    is ListF.NilF -> ListF.NilF()
    is ListF.ConsF -> ListF.ConsF(fl(l.a), fr(l.tail))
  }
}

@extension
interface ListKBirecursive<A> : Birecursive<ListK<A>, ListFPartialOf<A>> {
  override fun FF(): Functor<ListFPartialOf<A>> = ListF.functor()

  override fun ListK<A>.projectT(): Kind<ListFPartialOf<A>, ListK<A>> = when {
    isEmpty() -> ListF.NilF()
    else -> ListF.ConsF(first(), drop(1).k())
  }

  override fun Kind<ListFPartialOf<A>, ListK<A>>.embedT(): ListK<A> =
    when (val ls = fix()) {
      is ListF.NilF -> ListK.empty()
      is ListF.ConsF -> ListK.just(ls.a).combineK(ls.tail)
    }
}

@extension
interface ListKRecursive<A> : Recursive<ListK<A>, ListFPartialOf<A>>, ListKBirecursive<A>

@extension
interface ListKCorecursive<A> : Corecursive<ListK<A>, ListFPartialOf<A>>, ListKBirecursive<A>
