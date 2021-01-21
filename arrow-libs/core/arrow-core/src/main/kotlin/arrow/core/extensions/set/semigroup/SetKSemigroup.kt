package arrow.core.extensions.set.semigroup

import arrow.core.extensions.SetKSemigroup
import kotlin.Any
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.Set
import kotlin.jvm.JvmName

@JvmName("plus")
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
operator fun <A> Set<A>.plus(arg1: Set<A>): Set<A> =
  arrow.core.extensions.set.semigroup.Set.semigroup<A>().run {
    arrow.core.SetK(this@plus).plus(arrow.core.SetK(arg1)) as kotlin.collections.Set<A>
  }

@JvmName("maybeCombine")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith("arg1?.plus(this)"),
  DeprecationLevel.WARNING
)
fun <A> Set<A>.maybeCombine(arg1: Set<A>): Set<A> =
  arrow.core.extensions.set.semigroup.Set.semigroup<A>().run {
    arrow.core.SetK(this@maybeCombine).maybeCombine(arrow.core.SetK(arg1)) as
      kotlin.collections.Set<A>
  }

/**
 * cached extension
 */
@PublishedApi()
internal val semigroup_singleton: SetKSemigroup<Any?> = object : SetKSemigroup<Any?> {}

object Set {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(
    "@extension kinded projected functions are deprecated",
    ReplaceWith(
      "Semigroup.set<A>()",
      "arrow.core.set",
      "arrow.typeclasses.Semigroup"
    ),
    DeprecationLevel.WARNING
  )
  inline fun <A> semigroup(): SetKSemigroup<A> = semigroup_singleton as
    arrow.core.extensions.SetKSemigroup<A>
}
