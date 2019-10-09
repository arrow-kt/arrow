package arrow.extreme

import arrow.higherkind

//metadebug

@higherkind
sealed class Option<out A>() {

  fun <B> fold(ifSome: (A) -> B, ifNone: () -> B): B =
    when (this) {
      is Some -> ifSome(a)
      None -> ifNone()
    }

  fun <B> map(f: (A) -> B): Option<B> =
    flatMap { a -> Some(f(a)) }

  fun <B, C> map2(fb: Option<B>, f: (A, B) -> C): Option<C> =
    flatMap { a ->
      fb.flatMap { b ->
        Some(f(a, b))
      }
    }

  fun <B> flatMap(f: (A) -> Option<B>): Option<B> =
    fold({ a -> f(a) }, { None })


  companion object {
    fun <A> just(a: A): Option<A> =
      Some(a)
  }

}

data class Some<out A>(val a: A) : Option<A>()
object None : Option<Nothing>()

@higherkind
class Id<out A>(val value: A)

val x: IdOf<Int> = Id(1)


