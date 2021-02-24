package arrow.core.extensions.listk.monadPlus

import arrow.Kind
import arrow.core.ForListK
import arrow.core.ListK
import arrow.core.ListK.Companion
import arrow.core.extensions.ListKMonadPlus
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monadPlus_singleton: ListKMonadPlus = object : arrow.core.extensions.ListKMonadPlus {}

@JvmName("zeroM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("emptyList()"))
fun <A> zeroM(): ListK<A> = arrow.core.ListK
  .monadPlus()
  .zeroM<A>() as arrow.core.ListK<A>

@JvmName("plusM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("this + arg1"))
fun <A> Kind<ForListK, A>.plusM(arg1: Kind<ForListK, A>): ListK<A> =
  arrow.core.ListK.monadPlus().run {
    this@plusM.plusM<A>(arg1) as arrow.core.ListK<A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("MonadPlus typeclasses is deprecated. Use concrete methods on List")
inline fun Companion.monadPlus(): ListKMonadPlus = monadPlus_singleton
