package arrow.typeclasses

import arrow.Kind
import arrow.core.Option
import arrow.core.identity
import arrow.higherkind

@higherkind
data class Const<A, out T>(private val value: A) {

  @Suppress("UNCHECKED_CAST")
  fun <U> retag(): Const<A, U> = this as Const<A, U>

  @Suppress("UNUSED_PARAMETER")
  fun <G, U> traverse(GA: Applicative<G>, f: (T) -> Kind<G, U>): Kind<G, Const<A, U>> = GA.just(retag())

  @Suppress("UNUSED_PARAMETER")
  fun <G, U> traverseFilter(GA: Applicative<G>, f: (T) -> Kind<G, Option<U>>): Kind<G, Const<A, U>> = GA.just(retag())

  companion object {
    fun <A, T> just(a: A): Const<A, T> = Const(a)
  }

  fun value(): A = value
}

fun <A, T> Const<A, T>.combine(SG: Semigroup<A>, that: Const<A, T>): Const<A, T> = Const(SG.run { value().combine(that.value()) })

fun <A, T, U> Const<A, T>.ap(SG: Semigroup<A>, ff: Const<A, (T) -> U>): Const<A, U> = ff.retag<U>().combine(SG, retag())

fun <T, A, G> Const<A, Kind<G, T>>.sequence(GA: Applicative<G>): Kind<G, Const<A, T>> =
  traverse(GA, ::identity)

fun <A> A.const(): Const<A, Nothing> = Const(this)
