package arrow.core.extensions.list.traverse

import arrow.Kind
import arrow.core.ForListK
import arrow.core.extensions.ListKTraverse
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monad
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("traverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with traverseEither or traverseValidated from arrow.core.*")
fun <G, A, B> List<A>.traverse(arg1: Applicative<G>, arg2: Function1<A, Kind<G, B>>): Kind<G,
    Kind<ForListK, B>> = arrow.core.extensions.list.traverse.List.traverse().run {
  arrow.core.ListK(this@traverse).traverse<G, A, B>(arg1, arg2) as arrow.Kind<G,
    arrow.Kind<arrow.core.ForListK, B>>
}

@JvmName("sequence")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with sequenceEither or sequenceValidated from arrow.core.*")
fun <G, A> List<Kind<G, A>>.sequence(arg1: Applicative<G>): Kind<G, Kind<ForListK, A>> =
    arrow.core.extensions.list.traverse.List.traverse().run {
  arrow.core.ListK(this@sequence).sequence<G, A>(arg1) as arrow.Kind<G,
    arrow.Kind<arrow.core.ForListK, A>>
}

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("map(arg1)"))
fun <A, B> List<A>.map(arg1: Function1<A, B>): List<B> =
    arrow.core.extensions.list.traverse.List.traverse().run {
  arrow.core.ListK(this@map).map<A, B>(arg1) as kotlin.collections.List<B>
}

@JvmName("flatTraverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with flatTraverseEither or flatTraverseValidated from arrow.core.*")
fun <G, A, B> List<A>.flatTraverse(
  arg1: Monad<ForListK>,
  arg2: Applicative<G>,
  arg3: Function1<A, Kind<G, Kind<ForListK, B>>>
): Kind<G, Kind<ForListK, B>> = arrow.core.extensions.list.traverse.List.traverse().run {
  arrow.core.ListK(this@flatTraverse).flatTraverse<G, A, B>(arg1, arg2, arg3) as arrow.Kind<G,
    arrow.Kind<arrow.core.ForListK, B>>
}

/**
 * cached extension
 */
@PublishedApi()
internal val traverse_singleton: ListKTraverse = object : arrow.core.extensions.ListKTraverse {}

@Deprecated("Receiver List object is deprecated, prefer to turn List functions into top-level functions")
object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("Traverse typeclasses is deprecated. Use concrete methods on Iterable")
  inline fun traverse(): ListKTraverse = traverse_singleton}
