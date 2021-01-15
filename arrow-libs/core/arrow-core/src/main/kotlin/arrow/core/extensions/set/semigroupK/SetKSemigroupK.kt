package arrow.core.extensions.set.semigroupK

import arrow.Kind
import arrow.core.ForSetK
import arrow.core.extensions.SetKSemigroupK
import arrow.typeclasses.Semigroup
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.Set
import kotlin.jvm.JvmName

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
fun <A> Set<A>.combineK(arg1: Set<A>): Set<A> =
    arrow.core.extensions.set.semigroupK.Set.semigroupK().run {
  arrow.core.SetK(this@combineK).combineK<A>(arrow.core.SetK(arg1)) as kotlin.collections.Set<A>
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
  "arrow.core.extensions.set.semigroupK.Set.algebra"
  ),
  DeprecationLevel.WARNING
)
fun <A> algebra(): Semigroup<Kind<ForSetK, A>> = arrow.core.extensions.set.semigroupK.Set
   .semigroupK()
   .algebra<A>() as arrow.typeclasses.Semigroup<arrow.Kind<arrow.core.ForSetK, A>>

/**
 * cached extension
 */
@PublishedApi()
internal val semigroupK_singleton: SetKSemigroupK = object : arrow.core.extensions.SetKSemigroupK {}

object Set {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun semigroupK(): SetKSemigroupK = semigroupK_singleton}
