package arrow.core.extensions.listk.semigroupK

import arrow.Kind
import arrow.core.ForListK
import arrow.core.ListK
import arrow.core.ListK.Companion
import arrow.core.extensions.ListKSemigroupK
import arrow.typeclasses.Semigroup
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val semigroupK_singleton: ListKSemigroupK = object : arrow.core.extensions.ListKSemigroupK
{}

@JvmName("combineK")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <A> Kind<ForListK, A>.combineK(arg1: Kind<ForListK, A>): ListK<A> =
  arrow.core.ListK.semigroupK().run {
    this@combineK.combineK<A>(arg1) as arrow.core.ListK<A>
  }

@JvmName("algebra")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("listMonoid<A>()", "arrow.core.listMonoid"))
fun <A> algebra(): Semigroup<Kind<ForListK, A>> = arrow.core.ListK
  .semigroupK()
  .algebra<A>() as arrow.typeclasses.Semigroup<arrow.Kind<arrow.core.ForListK, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
inline fun Companion.semigroupK(): ListKSemigroupK = semigroupK_singleton
