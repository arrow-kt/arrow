package arrow.core.extensions.function1.divisible

import arrow.Kind
import arrow.core.ForFunction1
import arrow.core.Function1.Companion
import arrow.core.extensions.Function1Divisible
import arrow.typeclasses.Conested
import arrow.typeclasses.Monoid
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("conquer")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "conquer(MOO)",
  "arrow.core.Function1.conquer"
  ),
  DeprecationLevel.WARNING
)
fun <O, A> conquer(MOO: Monoid<O>): Kind<Conested<ForFunction1, O>, A> = arrow.core.Function1
   .divisible<O>(MOO)
   .conquer<A>() as arrow.Kind<arrow.typeclasses.Conested<arrow.core.ForFunction1, O>, A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <O> Companion.divisible(MOO: Monoid<O>): Function1Divisible<O> = object :
    arrow.core.extensions.Function1Divisible<O> { override fun MOO(): arrow.typeclasses.Monoid<O> =
    MOO }
