package consumer

import arrow.Kind
import arrow.`*`
import arrow.extension

/** Type Classes **/

interface Monad<F> {
  fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B>
  fun <A, B> Kind<F, A>.flatMap(f: (A) -> Kind<F, B>): Kind<F, B>
  fun <A> fx(f: () -> A): Kind<F, A>
  operator fun <A> Kind<F, A>.not(): A =
    TODO("Should have been replaced by Arrow Meta Comprehensions Plugin")
}

//@extension fun MonadForOptionFun(): Monad<ForOption> = object : Monad<ForOption> {
//  override fun <A, B> OptionOf<A>.map(f: (A) -> B): Option<B> =
//    (this as Option<A>).map(f)
//}
//
//@extension val MonadForOptionVal: Monad<ForOption> = object : Monad<ForOption> {
//  override fun <A, B> OptionOf<A>.map(f: (A) -> B): Option<B> =
//    (this as Option<A>).map(f)
//}
//
//@extension class MonadForOptionClass: Monad<ForOption> {
//  override fun <A, B> OptionOf<A>.map(f: (A) -> B): Option<B> =
//    (this as Option<A>).map(f)
//}

@extension
object MonadForOption : Monad<ForOption> {
  override fun <A, B> OptionOf<A>.map(f: (A) -> B): Option<B> =
    (this as Option<A>).map(f)

  override fun <A, B> OptionOf<A>.flatMap(f: (A) -> OptionOf<B>): Option<B> =
    (this as Option<A>).flatMap(f)

  override fun <A> fx(f: () -> A): Option<A> =
    Option.fx(f)
}

fun <F> Kind<F, Int>.addOne(FF: Monad<F> = `*`): Kind<F, Int> =
  map { it + 1 }

class Service {
  fun <F> Kind<F, Int>.addOne(FF: Monad<F> = `*`): Kind<F, Int> =
    map { it + 1 }
}

object Typeclasses {
  fun resolution1() {
    println("Typeclasses.resolution1: ${Option.Some(1).addOne()}")
  }
}