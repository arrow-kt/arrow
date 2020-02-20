package arrow.optics.extensions

import arrow.Kind
import arrow.core.Option
import arrow.extension
import arrow.core.extensions.option.traverse.traverse
import arrow.optics.Traversal
import arrow.optics.typeclasses.Each
import arrow.typeclasses.Applicative

/**
 * [Traversal] for [Option] that has focus in each [arrow.core.Some].
 *
 * @receiver [Option.Companion] to make it statically available.
 * @return [Traversal] with source [Option] and focus in every [arrow.core.Some] of the source.
 */
fun <A> Option.Companion.traversal(): Traversal<Option<A>, A> = object : Traversal<Option<A>, A> {
  override fun <F> modifyF(FA: Applicative<F>, s: Option<A>, f: (A) -> Kind<F, A>): Kind<F, Option<A>> = with(Option.traverse()) {
    s.traverse(FA, f)
  }
}

/**
 * [Each] instance definition for [Option].
 */
@extension
interface OptionEach<A> : Each<Option<A>, A> {
  override fun each(): Traversal<Option<A>, A> =
    Option.traversal()
}
