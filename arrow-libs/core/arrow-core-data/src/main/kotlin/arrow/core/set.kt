package arrow.core

import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import kotlin.collections.plus as _plus

@Deprecated("Receiver SetExtensions object is deprecated, prefer to turn Set functions into top-level functions")
object SetExtensions

@Deprecated("Receiver SortedSetInstances object is deprecated, prefer to turn SortedSet functions into top-level functions")
object SortedSetInstances

fun <A> Semigroup.Companion.set(): Semigroup<Set<A>> = object : Semigroup<Set<A>> {
  override fun Set<A>.combine(b: Set<A>): Set<A> =
    this._plus(b)
}

fun <A> Monoid.Companion.set(): Monoid<Set<A>> = object : Monoid<Set<A>> {
  override fun empty(): Set<A> =
    emptySet<A>()

  override fun Set<A>.combine(b: Set<A>): Set<A> =
    this._plus(b)
}
