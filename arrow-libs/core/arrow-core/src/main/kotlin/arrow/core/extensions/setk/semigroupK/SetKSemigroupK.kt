package arrow.core.extensions.setk.semigroupK

import arrow.Kind
import arrow.core.ForSetK
import arrow.core.SetK
import arrow.core.SetK.Companion
import arrow.core.extensions.SetKSemigroupK
import arrow.typeclasses.Semigroup
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val semigroupK_singleton: SetKSemigroupK = object : arrow.core.extensions.SetKSemigroupK {}

@JvmName("combineK")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith("this + arg1"),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForSetK, A>.combineK(arg1: Kind<ForSetK, A>): SetK<A> =
  arrow.core.SetK.semigroupK().run {
    this@combineK.combineK<A>(arg1) as arrow.core.SetK<A>
  }

@JvmName("algebra")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
fun <A> algebra(): Semigroup<Kind<ForSetK, A>> = arrow.core.SetK
  .semigroupK()
  .algebra<A>() as arrow.typeclasses.Semigroup<arrow.Kind<arrow.core.ForSetK, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Semigroup is no longer support for Set. Use Set#plus from Kotlin Std instead.",
  level = DeprecationLevel.WARNING
)
inline fun Companion.semigroupK(): SetKSemigroupK = semigroupK_singleton
