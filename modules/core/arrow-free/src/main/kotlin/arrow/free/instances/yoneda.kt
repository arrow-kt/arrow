package arrow.free.instances

import arrow.*
import arrow.free.*
import arrow.typeclasses.Functor

@instance(Yoneda::class)
interface YonedaFunctorInstance<U> : Functor<YonedaPartialOf<U>> {
  override fun <A, B> Kind<YonedaPartialOf<U>, A>.map(f: (A) -> B): Yoneda<U, B> = fix().map(f)
}

class YonedaContext<U> : YonedaFunctorInstance<U>

class YonedaContextPartiallyApplied<U> {
  infix fun <A> syntax(f: YonedaContext<U>.() -> A): A =
    f(YonedaContext())
}

fun <U> Yoneda(): YonedaContextPartiallyApplied<U> =
  YonedaContextPartiallyApplied()