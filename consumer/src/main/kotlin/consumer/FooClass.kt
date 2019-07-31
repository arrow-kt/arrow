package consumer

import arrow.Kind
import arrow.`*`
import arrow.extension

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

//fun functorForOption(): Functor<ForOption> = object : Functor<ForOption> {
//  override fun <A, B> OptionOf<A>.map(f: (A) -> B): Option<B> =
//    (this as Option<A>).map(f)
//}

//val FunctorForOption: Functor<ForOption> = object : Functor<ForOption> {
//  override fun <A, B> OptionOf<A>.map(f: (A) -> B): Option<B> =
//    (this as Option<A>).map(f)
//}

@extension class FunctorForOptionClass: Functor<ForOption> {
  override fun <A, B> OptionOf<A>.map(f: (A) -> B): Option<B> =
    (this as Option<A>).map(f)
}

@extension object FunctorForOption: Functor<ForOption> {
  override fun <A, B> OptionOf<A>.map(f: (A) -> B): Option<B> =
    (this as Option<A>).map(f)
}

fun <F> Kind<F, Int>.addOne(FF: Functor<F> = `*`): Kind<F, Int> =
  map { it + 1 }
//
//class Service {
//  fun <F> Kind<F, Int>.addOne(FF: Functor<F> = `*`): Kind<F, Int> =
//    map { it + 1 }
//} TODO support nested functions that are not top level in injections through arrow meta

fun testConversion(): Any =
  Option.Some(1).addOne()