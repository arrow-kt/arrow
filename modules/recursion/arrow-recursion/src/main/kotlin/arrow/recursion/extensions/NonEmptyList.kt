package arrow.recursion.extensions

import arrow.Kind
import arrow.Kind2
import arrow.core.Eval
import arrow.core.Nel
import arrow.core.NonEmptyList
import arrow.core.getOrElse
import arrow.core.none
import arrow.core.some
import arrow.extension
import arrow.recursion.extensions.nonemptylistf.functor.functor
import arrow.recursion.pattern.ForNonEmptyListF
import arrow.recursion.pattern.NonEmptyListF
import arrow.recursion.pattern.NonEmptyListFPartialOf
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
interface NonEmptyListFFunctor<I> : Functor<NonEmptyListFPartialOf<I>> {
  override fun <C, B> Kind<NonEmptyListFPartialOf<I>, C>.map(f: (C) -> B): Kind<NonEmptyListFPartialOf<I>, B> =
    fix().map(f)
}

@extension
interface NonEmptyListFFoldable<I> : Foldable<NonEmptyListFPartialOf<I>> {
  override fun <C, B> Kind<NonEmptyListFPartialOf<I>, C>.foldLeft(b: B, f: (B, C) -> B): B =
    fix().let {
      it.tail.foldLeft(b) { acc, v -> f(acc, v) }
    }

  override fun <C, B> Kind<NonEmptyListFPartialOf<I>, C>.foldRight(lb: Eval<B>, f: (C, Eval<B>) -> Eval<B>): Eval<B> =
    fix().let {
      it.tail.foldRight(lb) { v, acc -> f(v, acc) }
    }
}

@extension
interface NonEmptyListFTraverse<I> : Traverse<NonEmptyListFPartialOf<I>>, NonEmptyListFFoldable<I> {
  override fun <G, C, B> Kind<NonEmptyListFPartialOf<I>, C>.traverse(AP: Applicative<G>, f: (C) -> Kind<G, B>): Kind<G, Kind<NonEmptyListFPartialOf<I>, B>> =
    fix().tail.fold({ AP.just(NonEmptyListF(fix().head, none())) }, {
      AP.run { f(it).map { NonEmptyListF(fix().head, it.some()) } }
    })
}

@extension
interface NonEmptyListFBifunctor : Bifunctor<ForNonEmptyListF> {
  override fun <A, B, C, D> Kind2<ForNonEmptyListF, A, B>.bimap(fl: (A) -> C, fr: (B) -> D): Kind2<ForNonEmptyListF, C, D> =
    fix().let {
      NonEmptyListF(fl(it.head), it.tail.map(fr))
    }
}

@extension
interface NonEmptyListBirecursive<A> : Birecursive<NonEmptyList<A>, NonEmptyListFPartialOf<A>> {
  override fun FF(): Functor<NonEmptyListFPartialOf<A>> = NonEmptyListF.functor()

  override fun NonEmptyList<A>.projectT(): Kind<NonEmptyListFPartialOf<A>, NonEmptyList<A>> =
    NonEmptyListF(head, Nel.fromList(tail))

  override fun Kind<NonEmptyListFPartialOf<A>, NonEmptyList<A>>.embedT(): NonEmptyList<A> =
    fix().let {
      NonEmptyList(it.head, it.tail.map { it.all }.getOrElse { emptyList() })
    }
}

@extension
interface NonEmptyListRecursive<A> : Recursive<NonEmptyList<A>, NonEmptyListFPartialOf<A>>, NonEmptyListBirecursive<A>

@extension
interface NonEmptyListCorecursive<A> : Corecursive<NonEmptyList<A>, NonEmptyListFPartialOf<A>>, NonEmptyListBirecursive<A>
