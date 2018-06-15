package arrow.free

import arrow.Kind
import arrow.higherkind
import arrow.typeclasses.Functor

@higherkind
abstract class Yoneda<F, A> : YonedaOf<F, A>, YonedaKindedJ<F, A> {

  abstract operator fun <B> invoke(f: (A) -> B): Kind<F, B>

  fun lower(): Kind<F, A> = invoke { a -> a }

  fun <B> map(ff: (A) -> B): Yoneda<F, B> =
    object : Yoneda<F, B>() {
      override fun <C> invoke(f: (B) -> C): Kind<F, C> = this@Yoneda { f(ff(it)) }
    }

  fun toCoyoneda(): Coyoneda<F, A, A> = Coyoneda(lower(), listOf({ a: Any? -> a }))

  companion object {
    operator fun <U, A> invoke(fa: Kind<U, A>, FF: Functor<U>): Yoneda<U, A> =
      object : Yoneda<U, A>(), Functor<U> by FF {
        override fun <B> invoke(f: (A) -> B): Kind<U, B> = fa.map(f)
      }
  }
}