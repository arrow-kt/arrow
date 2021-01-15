package arrow.core.extensions.nonemptylist.eqK

import arrow.Kind
import arrow.core.ForNonEmptyList
import arrow.core.NonEmptyList.Companion
import arrow.core.extensions.NonEmptyListEqK
import arrow.typeclasses.Eq
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val eqK_singleton: NonEmptyListEqK = object : arrow.core.extensions.NonEmptyListEqK {}

@JvmName("eqK")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "eqK(arg1, arg2)",
  "arrow.core.eqK"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForNonEmptyList, A>.eqK(arg1: Kind<ForNonEmptyList, A>, arg2: Eq<A>): Boolean =
    arrow.core.NonEmptyList.eqK().run {
  this@eqK.eqK<A>(arg1, arg2) as kotlin.Boolean
}

@JvmName("liftEq")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "liftEq(arg0)",
  "arrow.core.NonEmptyList.liftEq"
  ),
  DeprecationLevel.WARNING
)
fun <A> liftEq(arg0: Eq<A>): Eq<Kind<ForNonEmptyList, A>> = arrow.core.NonEmptyList
   .eqK()
   .liftEq<A>(arg0) as arrow.typeclasses.Eq<arrow.Kind<arrow.core.ForNonEmptyList, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.eqK(): NonEmptyListEqK = eqK_singleton
