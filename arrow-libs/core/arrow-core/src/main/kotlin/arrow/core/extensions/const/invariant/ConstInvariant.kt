package arrow.core.extensions.const.invariant

import arrow.Kind
import arrow.core.Const
import arrow.core.Const.Companion
import arrow.core.ForConst
import arrow.core.extensions.ConstInvariant
import kotlin.Any
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val invariant_singleton: ConstInvariant<Any?> = object : ConstInvariant<Any?> {}

@JvmName("imap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "retag()",
    "arrow.core.retag"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<Kind<ForConst, A>, A>.imap(arg1: Function1<A, B>, arg2: Function1<B, A>): Const<A, B> =
  arrow.core.Const.invariant<A>().run {
    this@imap.imap<A, B>(arg1, arg2) as arrow.core.Const<A, B>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Invariant typeclass is deprecated. Use concrete methods on Const",
  level = DeprecationLevel.WARNING
)
inline fun <A> Companion.invariant(): ConstInvariant<A> = invariant_singleton as
  arrow.core.extensions.ConstInvariant<A>
