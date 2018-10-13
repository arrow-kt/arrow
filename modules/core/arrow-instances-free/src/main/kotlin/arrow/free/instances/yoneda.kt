package arrow.free.instances

import arrow.*
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.free.*
import arrow.typeclasses.Functor

@extension
interface YonedaFunctorInstance<U> : Functor<YonedaPartialOf<U>> {
  override fun <A, B> Kind<YonedaPartialOf<U>, A>.map(f: (A) -> B): Yoneda<U, B> = fix().map(f)
}

class YonedaContext<U> : YonedaFunctorInstance<U>

class YonedaContextPartiallyApplied<U> {
  @Deprecated(ExtensionsDSLDeprecated)
  infix fun <A> extensions(f: YonedaContext<U>.() -> A): A =
    f(YonedaContext())
}

fun <U> ForYoneda(): YonedaContextPartiallyApplied<U> =
  YonedaContextPartiallyApplied()