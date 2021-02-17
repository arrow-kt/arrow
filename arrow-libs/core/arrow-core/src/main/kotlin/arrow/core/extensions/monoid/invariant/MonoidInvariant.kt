package arrow.core.extensions.monoid.invariant

import arrow.Kind
import arrow.core.extensions.MonoidInvariant
import arrow.typeclasses.ForMonoid
import arrow.typeclasses.Monoid
import arrow.typeclasses.Monoid.Companion
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
internal val invariant_singleton: MonoidInvariant<Any?> = object : MonoidInvariant<Any?> {}

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
    "imap(arg1, arg2)",
    "arrow.core.imap"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForMonoid, A>.imap(arg1: Function1<A, B>, arg2: Function1<B, A>): Monoid<B> =
  arrow.typeclasses.Monoid.invariant<A>().run {
    this@imap.imap<A, B>(arg1, arg2) as arrow.typeclasses.Monoid<B>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A> Companion.invariant(): MonoidInvariant<A> = invariant_singleton as
  arrow.core.extensions.MonoidInvariant<A>
