package arrow.typeclasses

import arrow.Kind
import arrow.core.Option
import arrow.higherkind

fun <A, T> ConstOf<A, T>.value(): A = this.fix().value

@higherkind
data class Const<A, out T>(val value: A) : ConstOf<A, T> {

  @Suppress("UNCHECKED_CAST")
  fun <U> retag(): Const<A, U> = this as Const<A, U>

  fun <F, U> traverse(FA: Applicative<F>, f: (T) -> Kind<F, U>): Kind<F, Const<A, U>> = FA.just(retag())

  fun <F, U> traverseFilter(FA: Applicative<F>, f: (T) -> Kind<F, Option<U>>): Kind<F, Const<A, U>> = FA.just(retag())

  companion object {
    fun <A, T> just(a: A): Const<A, T> = Const(a)
  }
}

fun <A, T> ConstOf<A, T>.combine(that: ConstOf<A, T>, SG: Semigroup<A>): Const<A, T> = arrow.typeclasses.Const(SG.run { value().combine(that.value()) })

fun <A, T, U> ConstOf<A, T>.ap(ff: ConstOf<A, (T) -> U>, SG: Semigroup<A>): Const<A, U> = ff.fix().retag<U>().combine(this.fix().retag(), SG)

fun <A> A.const(): Const<A, Nothing> = Const(this)
