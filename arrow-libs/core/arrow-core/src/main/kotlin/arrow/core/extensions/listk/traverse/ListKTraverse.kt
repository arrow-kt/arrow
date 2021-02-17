package arrow.core.extensions.listk.traverse

import arrow.Kind
import arrow.core.ForListK
import arrow.core.ListK
import arrow.core.ListK.Companion
import arrow.core.extensions.ListKTraverse
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monad
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val traverse_singleton: ListKTraverse = object : arrow.core.extensions.ListKTraverse {}

@JvmName("traverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with traverseEither or traverseValidated from arrow.core.*")
fun <G, A, B> Kind<ForListK, A>.traverse(arg1: Applicative<G>, arg2: Function1<A, Kind<G, B>>):
  Kind<G, Kind<ForListK, B>> = arrow.core.ListK.traverse().run {
    this@traverse.traverse<G, A, B>(arg1, arg2) as arrow.Kind<G, arrow.Kind<arrow.core.ForListK, B>>
  }

@JvmName("sequence")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with sequenceEither or sequenceValidated from arrow.core.*")
fun <G, A> Kind<ForListK, Kind<G, A>>.sequence(arg1: Applicative<G>): Kind<G, Kind<ForListK, A>> =
  arrow.core.ListK.traverse().run {
    this@sequence.sequence<G, A>(arg1) as arrow.Kind<G, arrow.Kind<arrow.core.ForListK, A>>
  }

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("map(arg1)"))
fun <A, B> Kind<ForListK, A>.map(arg1: Function1<A, B>): ListK<B> =
  arrow.core.ListK.traverse().run {
    this@map.map<A, B>(arg1) as arrow.core.ListK<B>
  }

@JvmName("flatTraverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with flatTraverseEither or flatTraverseValidated from arrow.core.*")
fun <G, A, B> Kind<ForListK, A>.flatTraverse(
  arg1: Monad<ForListK>,
  arg2: Applicative<G>,
  arg3: Function1<A, Kind<G, Kind<ForListK, B>>>
): Kind<G, Kind<ForListK, B>> = arrow.core.ListK.traverse().run {
  this@flatTraverse.flatTraverse<G, A, B>(arg1, arg2, arg3) as arrow.Kind<G,
    arrow.Kind<arrow.core.ForListK, B>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Traverse typeclasses is deprecated. Use concrete methods on Iterable")
inline fun Companion.traverse(): ListKTraverse = traverse_singleton
