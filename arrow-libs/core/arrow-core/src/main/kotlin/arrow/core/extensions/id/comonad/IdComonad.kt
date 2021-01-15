package arrow.core.extensions.id.comonad

import arrow.Kind
import arrow.core.ForId
import arrow.core.Id
import arrow.core.Id.Companion
import arrow.core.extensions.IdComonad
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val comonad_singleton: IdComonad = object : arrow.core.extensions.IdComonad {}

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
  "coflatMap(arg1)",
  "arrow.core.coflatMap"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForId, A>.coflatMap(arg1: Function1<Kind<ForId, A>, B>): Id<B> =
    arrow.core.Id.comonad().run {
  this@coflatMap.coflatMap<A, B>(arg1) as arrow.core.Id<B>
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
  "extract()",
  "arrow.core.extract"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForId, A>.extract(): A = arrow.core.Id.comonad().run {
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
  "duplicate()",
  "arrow.core.duplicate"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForId, A>.duplicate(): Id<Id<A>> = arrow.core.Id.comonad().run {
  this@duplicate.duplicate<A>() as arrow.core.Id<arrow.core.Id<A>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.comonad(): IdComonad = comonad_singleton
