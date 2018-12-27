@file:Suppress("NotImplementedDeclaration")
package arrow.ap.objects.deriving

import arrow.core.Either
import arrow.deriving
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad

@Suppress("DEPRECATION", "UNUSED_PARAMETER")
@deriving(Functor::class, Applicative::class, Monad::class)
data class Deriving<A>(val value: A) : DerivingOf<A> {
  fun <B> map(f: (A) -> B): Deriving<B> = TODO()

  companion object {
    fun <A> just(a: A): Deriving<A> = TODO()
    fun <A, B> tailRecM(a: A, f: (A) -> DerivingOf<Either<A, B>>): Deriving<B> = TODO()
  }
}
