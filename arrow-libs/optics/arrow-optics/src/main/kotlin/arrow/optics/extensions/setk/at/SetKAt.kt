package arrow.optics.extensions.setk.at

import arrow.core.SetK
import arrow.core.SetK.Companion
import arrow.optics.PLens
import arrow.optics.extensions.SetKAt
import kotlin.Any
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val at_singleton: SetKAt<Any?> = object : SetKAt<Any?> {}

@JvmName("at")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "arrow.optics.extensions package is being deprecated, and it will be removed in 0.13.",
  ReplaceWith(
    "this compose At.set<A>().at(i)",
    "arrow.optics.set", "arrow.optics.typeclasses.At", "arrow.optics.compose"
  ),
  DeprecationLevel.WARNING
)
fun <A, T> PLens<T, T, SetK<A>, SetK<A>>.at(i: A): PLens<T, T, Boolean, Boolean> =
  arrow.core.SetK.at<A>().run {
    this@at.at<T>(i) as arrow.optics.PLens<T, T, kotlin.Boolean, kotlin.Boolean>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "SetK is being deprecated. Use the instance for Set from the companion object of the typeclass.",
  ReplaceWith(
    "At.set<A>()",
    "arrow.optics.set", "arrow.optics.typeclasses.At"
  ),
  DeprecationLevel.WARNING
)
inline fun <A> Companion.at(): SetKAt<A> = at_singleton as arrow.optics.extensions.SetKAt<A>
