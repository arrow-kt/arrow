package arrow.core.extensions.const.divisible

import arrow.core.Const
import arrow.core.Const.Companion
import arrow.core.extensions.ConstDivisibleInstance
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
  "arrow.core.Const.conquer"
  ),
  DeprecationLevel.WARNING
)
fun <O, A> conquer(MOO: Monoid<O>): Const<O, A> = arrow.core.Const
   .divisible<O>(MOO)
   .conquer<A>() as arrow.core.Const<O, A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <O> Companion.divisible(MOO: Monoid<O>): ConstDivisibleInstance<O> = object :
    arrow.core.extensions.ConstDivisibleInstance<O> { override fun MOO():
    arrow.typeclasses.Monoid<O> = MOO }
