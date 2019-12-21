package arrow.core

import arrow.Kind
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Semigroup

fun <A, T> ConstOf<A, T>.value(): A = this.fix().value()

@higherkind
data class Const<A, out T>(private val value: A) : ConstOf<A, T> {

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

fun <A, T> ConstOf<A, T>.combine(SG: Semigroup<A>, that: ConstOf<A, T>): Const<A, T> = Const(SG.run { value().combine(that.value()) })

fun <A, T, U> ConstOf<A, T>.ap(SG: Semigroup<A>, ff: ConstOf<A, (T) -> U>): Const<A, U> = fix().retag<U>().combine(SG, ff.fix().retag())

fun <T, A, G> ConstOf<A, Kind<G, T>>.sequence(GA: Applicative<G>): Kind<G, Const<A, T>> =
  fix().traverse(GA, ::identity)

fun <A> A.const(): Const<A, Nothing> = Const(this)
