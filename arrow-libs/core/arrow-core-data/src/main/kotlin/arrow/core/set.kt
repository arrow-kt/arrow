package arrow.core

import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import kotlin.collections.plus as _plus

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
