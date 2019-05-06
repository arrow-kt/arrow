package arrow.data.extensions

import arrow.data.fingertree.FingerTree
import arrow.extension
import arrow.typeclasses.Monoid

@extension
interface FingerTreeMonoid<T> : Monoid<FingerTree<T>> {
  override fun empty(): FingerTree<T> = FingerTree.empty()

  override fun FingerTree<T>.combine(b: FingerTree<T>): FingerTree<T> = this.concat(b)
}
