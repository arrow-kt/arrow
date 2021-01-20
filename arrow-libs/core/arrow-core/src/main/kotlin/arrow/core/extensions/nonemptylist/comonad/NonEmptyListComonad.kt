package arrow.core.extensions.nonemptylist.comonad

import arrow.Kind
import arrow.core.ForNonEmptyList
import arrow.core.NonEmptyList
import arrow.core.NonEmptyList.Companion
import arrow.core.extensions.NonEmptyListComonad

/**
 * cached extension
 */
@PublishedApi()
internal val comonad_singleton: NonEmptyListComonad = object :
    arrow.core.extensions.NonEmptyListComonad {}

@JvmName("coflatMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "fix().coflatMap(arg1)",
  "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForNonEmptyList, A>.coflatMap(arg1: Function1<Kind<ForNonEmptyList, A>, B>):
    NonEmptyList<B> = arrow.core.NonEmptyList.comonad().run {
  this@coflatMap.coflatMap<A, B>(arg1) as arrow.core.NonEmptyList<B>
}

@JvmName("extract")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "fix().extract()",
  "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForNonEmptyList, A>.extract(): A = arrow.core.NonEmptyList.comonad().run {
  this@extract.extract<A>() as A
}

@JvmName("duplicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "fix().coflatMap<NonEmptyListOf<A>>(::identity)",
  "arrow.core.fix", "arrow.core.identity"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForNonEmptyList, A>.duplicate(): NonEmptyList<NonEmptyList<A>> =
    arrow.core.NonEmptyList.comonad().run {
  this@duplicate.duplicate<A>() as arrow.core.NonEmptyList<arrow.core.NonEmptyList<A>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Comonad typeclass is deprecated. Use concrete methods on NonEmptyList",
  level = DeprecationLevel.WARNING)
inline fun Companion.comonad(): NonEmptyListComonad = comonad_singleton
