package arrow.core.extensions.const.contravariant

import arrow.Kind
import arrow.core.Const
import arrow.core.Const.Companion
import arrow.core.ForConst
import arrow.core.extensions.ConstContravariant
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
internal val contravariant_singleton: ConstContravariant<Any?> = object : ConstContravariant<Any?> {}

@JvmName("contramap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "contramap(arg1)",
    "arrow.core.contramap"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<Kind<ForConst, A>, A>.contramap(arg1: Function1<B, A>): Const<A, B> =
  arrow.core.Const.contravariant<A>().run {
    this@contramap.contramap<A, B>(arg1) as arrow.core.Const<A, B>
  }

@JvmName("lift1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
fun <A, B> lift(arg0: Function1<A, B>): Function1<Kind<Kind<ForConst, A>, B>, Kind<Kind<ForConst,
  A>, A>> = arrow.core.Const
  .contravariant<A>()
  .lift<A, B>(arg0) as kotlin.Function1<arrow.Kind<arrow.Kind<arrow.core.ForConst, A>, B>,
  arrow.Kind<arrow.Kind<arrow.core.ForConst, A>, A>>

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
fun <A, B> Kind<Kind<ForConst, A>, A>.imap(arg1: Function1<A, B>, arg2: Function1<B, A>): Const<A, B> =
  arrow.core.Const.contravariant<A>().run {
    this@imap.imap<A, B>(arg1, arg2) as arrow.core.Const<A, B>
  }

@JvmName("narrow")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "narrow()",
    "arrow.core.narrow"
  ),
  DeprecationLevel.WARNING
)
fun <A, B : A> Kind<Kind<ForConst, A>, A>.narrow(): Const<A, B> =
  arrow.core.Const.contravariant<A>().run {
    this@narrow.narrow<A, B>() as arrow.core.Const<A, B>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Contravariant typeclass is deprecated. Use concrete methods on Const",
  level = DeprecationLevel.WARNING
)
inline fun <A> Companion.contravariant(): ConstContravariant<A> =
  contravariant_singleton as arrow.core.extensions.ConstContravariant<A>
