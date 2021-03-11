@file:JvmMultifileClass

package arrow.core

import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import kotlin.collections.plus as _plus

/**
 * Returns a list containing all elements except the first element
 */
fun <T> List<T>.tail(): List<T> = this.drop(1)

infix fun <T> T.prependTo(list: List<T>): List<T> = listOf(this)._plus(list)

fun <T> List<Option<T>>.flatten(): List<T> = flatMap { it.fold(::emptyList, ::listOf) }

fun <A> Semigroup.Companion.list(): Semigroup<List<A>> =
  Monoid.list()

fun <A> Monoid.Companion.list(): Monoid<List<A>> =
  ListMonoid as Monoid<List<A>>

object ListMonoid : Monoid<List<Any?>> {
  override fun empty(): List<Any?> = emptyList()
  override fun List<Any?>.combine(b: List<Any?>): List<Any?> = this._plus(b)
}
