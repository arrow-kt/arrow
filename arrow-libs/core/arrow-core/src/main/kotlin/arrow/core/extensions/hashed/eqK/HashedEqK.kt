package arrow.core.extensions.hashed.eqK

import arrow.Kind
import arrow.core.ForHashed
import arrow.core.Hashed.Companion
import arrow.core.extensions.HashedEqK
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
internal val eqK_singleton: HashedEqK = object : arrow.core.extensions.HashedEqK {}

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
fun <A> Kind<ForHashed, A>.eqK(arg1: Kind<ForHashed, A>, arg2: Eq<A>): Boolean =
  arrow.core.Hashed.eqK().run {
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
    "arrow.core.Hashed.liftEq"
  ),
  DeprecationLevel.WARNING
)
fun <A> liftEq(arg0: Eq<A>): Eq<Kind<ForHashed, A>> = arrow.core.Hashed
  .eqK()
  .liftEq<A>(arg0) as arrow.typeclasses.Eq<arrow.Kind<arrow.core.ForHashed, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.eqK(): HashedEqK = eqK_singleton
