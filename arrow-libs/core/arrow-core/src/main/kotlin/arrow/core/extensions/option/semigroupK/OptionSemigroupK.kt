package arrow.core.extensions.option.semigroupK

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.extensions.OptionSemigroupK
import arrow.typeclasses.Semigroup

/**
 * cached extension
 */
@PublishedApi()
internal val semigroupK_singleton: OptionSemigroupK = object :
  arrow.core.extensions.OptionSemigroupK {}

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
    "orElse { arg1 }",
    "arrow.core.orElse"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.combineK(arg1: Kind<ForOption, A>): Option<A> =
  arrow.core.Option.semigroupK().run {
    this@combineK.combineK<A>(arg1) as arrow.core.Option<A>
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
    "Semigroup.option<A>()",
    "arrow.core.option",
    "arrow.typeclasses.Semigroup"
  ),
  DeprecationLevel.WARNING
)
fun <A> algebra(): Semigroup<Kind<ForOption, A>> = arrow.core.Option
  .semigroupK()
  .algebra<A>() as arrow.typeclasses.Semigroup<arrow.Kind<arrow.core.ForOption, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0",
  level = DeprecationLevel.WARNING
)
inline fun Companion.semigroupK(): OptionSemigroupK = semigroupK_singleton
