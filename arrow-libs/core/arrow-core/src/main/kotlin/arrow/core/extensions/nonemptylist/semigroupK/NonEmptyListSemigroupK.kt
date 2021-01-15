package arrow.core.extensions.nonemptylist.semigroupK

import arrow.Kind
import arrow.core.ForNonEmptyList
import arrow.core.NonEmptyList
import arrow.core.NonEmptyList.Companion
import arrow.core.extensions.NonEmptyListSemigroupK
import arrow.typeclasses.Semigroup
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val semigroupK_singleton: NonEmptyListSemigroupK = object :
    arrow.core.extensions.NonEmptyListSemigroupK {}

@JvmName("combineK")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "combineK(arg1)",
  "arrow.core.combineK"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForNonEmptyList, A>.combineK(arg1: Kind<ForNonEmptyList, A>): NonEmptyList<A> =
    arrow.core.NonEmptyList.semigroupK().run {
  this@combineK.combineK<A>(arg1) as arrow.core.NonEmptyList<A>
}

@JvmName("algebra")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "algebra()",
  "arrow.core.NonEmptyList.algebra"
  ),
  DeprecationLevel.WARNING
)
fun <A> algebra(): Semigroup<Kind<ForNonEmptyList, A>> = arrow.core.NonEmptyList
   .semigroupK()
   .algebra<A>() as arrow.typeclasses.Semigroup<arrow.Kind<arrow.core.ForNonEmptyList, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.semigroupK(): NonEmptyListSemigroupK = semigroupK_singleton
