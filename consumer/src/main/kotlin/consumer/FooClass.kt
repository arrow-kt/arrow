package consumer

import arrow.bind
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

  fun <B> flatMap(f: (A) -> Option<B>): Option<B> =
    when (this) {
      None -> None
      is Some -> f(value)
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
  fun <A, B> Kind<F, A>.flatMap(f: (A) -> Kind<F, B>): Kind<F, B>
}

//@extension fun functorForOptionFun(): Functor<ForOption> = object : Functor<ForOption> {
//  override fun <A, B> OptionOf<A>.map(f: (A) -> B): Option<B> =
//    (this as Option<A>).map(f)
//}
//
//@extension val FunctorForOptionVal: Functor<ForOption> = object : Functor<ForOption> {
//  override fun <A, B> OptionOf<A>.map(f: (A) -> B): Option<B> =
//    (this as Option<A>).map(f)
//}
//
//@extension class FunctorForOptionClass: Functor<ForOption> {
//  override fun <A, B> OptionOf<A>.map(f: (A) -> B): Option<B> =
//    (this as Option<A>).map(f)
//}

@extension
object FunctorForOption : Functor<ForOption> {
  override fun <A, B> OptionOf<A>.map(f: (A) -> B): Option<B> =
    (this as Option<A>).map(f)

  override fun <A, B> OptionOf<A>.flatMap(f: (A) -> OptionOf<B>): Option<B> =
    (this as Option<A>).flatMap(f)
}

fun <F> Kind<F, Int>.addOne(FF: Functor<F> = `*`): Kind<F, Int> =
  map { it + 1 }

class Service {
  fun <F> Kind<F, Int>.addOne(FF: Functor<F> = `*`): Kind<F, Int> =
    map { it + 1 }
}

fun testConversion(): Option<Int> {
  val x = Option.Some(1).addOne().bind()
  val y = Option.Some(1).addOne().bind()
  return Option.Some(x + y)
}
