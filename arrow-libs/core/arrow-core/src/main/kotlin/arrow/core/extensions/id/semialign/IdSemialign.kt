package arrow.core.extensions.id.semialign

import arrow.Kind
import arrow.core.ForId
import arrow.core.Id
import arrow.core.Id.Companion
import arrow.core.Ior
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.IdSemialign
import arrow.typeclasses.Semigroup
import kotlin.Deprecated
import kotlin.Function1
import kotlin.Function2
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val semialign_singleton: IdSemialign = object : arrow.core.extensions.IdSemialign {}

@JvmName("align")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "align(arg0, arg1)",
  "arrow.core.Id.align"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> align(arg0: Kind<ForId, A>, arg1: Kind<ForId, B>): Id<Ior<A, B>> = arrow.core.Id
   .semialign()
   .align<A, B>(arg0, arg1) as arrow.core.Id<arrow.core.Ior<A, B>>

@JvmName("alignWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "alignWith(arg0, arg1, arg2)",
  "arrow.core.Id.alignWith"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> alignWith(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Function1<Ior<A, B>, C>
): Id<C> = arrow.core.Id
   .semialign()
   .alignWith<A, B, C>(arg0, arg1, arg2) as arrow.core.Id<C>

@JvmName("salign")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "salign(arg1, arg2)",
  "arrow.core.salign"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForId, A>.salign(arg1: Semigroup<A>, arg2: Kind<ForId, A>): Id<A> =
    arrow.core.Id.semialign().run {
  this@salign.salign<A>(arg1, arg2) as arrow.core.Id<A>
}

@JvmName("padZip")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "padZip(arg1)",
  "arrow.core.padZip"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForId, A>.padZip(arg1: Kind<ForId, B>): Id<Tuple2<Option<A>, Option<B>>> =
    arrow.core.Id.semialign().run {
  this@padZip.padZip<A, B>(arg1) as arrow.core.Id<arrow.core.Tuple2<arrow.core.Option<A>,
    arrow.core.Option<B>>>
}

@JvmName("padZipWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "padZipWith(arg1, arg2)",
  "arrow.core.padZipWith"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Kind<ForId, A>.padZipWith(
  arg1: Kind<ForId, B>,
  arg2: Function2<Option<A>, Option<B>, C>
): Id<C> = arrow.core.Id.semialign().run {
  this@padZipWith.padZipWith<A, B, C>(arg1, arg2) as arrow.core.Id<C>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.semialign(): IdSemialign = semialign_singleton
