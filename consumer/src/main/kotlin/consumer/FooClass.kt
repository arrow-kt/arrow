package consumer

import arrow.Kind
import arrow.`*`
import arrow.extension
import arrow.with

/** HigherKinds **/
sealed class Option<out A> {
  object None : Option<Nothing>()
  data class Some<out A>(val value: A) : Option<A>()

  fun <B> map(f: (A) -> B): Option<B> =
    when (this) {
      None -> None
      is Some -> Some(f(value))
    }
}

sealed class Either<out A, out B> {
  class Left<out A> : Either<A, Nothing>()
  class Right<out B>(val value: B) : Either<Nothing, B>()
}

class Kleisli<out F, out D, out A>

/** Type Classes **/

interface Functor<F> {
  fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B>
}

@extension
class OptionFunctor : Functor<ForOption> {
  override fun <A, B> OptionOf<A>.map(f: (A) -> B): Option<B> =
    (this as Option<A>).map(f)
}

fun functorForOption(): Functor<ForOption> = OptionFunctor()

fun <F> Kind<F, Int>.addOne(FF: Functor<F> = `*`): Kind<F, Int> =
  with(FF) {
    map { it + 1 }
  }

fun testConversion(): Any =
  Option.Some(1).addOne()