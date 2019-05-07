package arrow.data.extensions

import arrow.Kind
import arrow.data.fingertree.FingerTree
import arrow.data.fingertree.ForFingerTree
import arrow.data.fingertree.fix
import arrow.extension
import arrow.typeclasses.*

@extension
interface FingerTreeMonoid<T> : Monoid<FingerTree<T>> {
  override fun empty(): FingerTree<T> = FingerTree.empty()

  override fun FingerTree<T>.combine(b: FingerTree<T>): FingerTree<T> = this.concat(b)
}

@extension
interface FingerTreeFunctor : Functor<ForFingerTree> {
  override fun <A, B> Kind<ForFingerTree, A>.map(f: (A) -> B): FingerTree<B> = fix().map(f)
}

@extension
interface FingerTreeEq<T> : Eq<FingerTree<T>> {
  override fun FingerTree<T>.eqv(b: FingerTree<T>): Boolean = this == b
}

@extension
interface FingerTreeApplicative : Applicative<ForFingerTree> {
  override fun <A> just(a: A): FingerTree<A> = FingerTree.single(a)

  override fun <A, B> Kind<ForFingerTree, A>.ap(ff: Kind<ForFingerTree, (A) -> B>): FingerTree<B> =
    fix().ap(ff)
}


