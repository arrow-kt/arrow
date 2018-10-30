package arrow.optics.instances

import arrow.Kind
import arrow.core.Try
import arrow.extension
import arrow.instances.traverse
import arrow.optics.Traversal
import arrow.optics.typeclasses.Each
import arrow.typeclasses.Applicative

/**
 * [Traversal] for [Try] that has focus in each [Try.Success].
 *
 * @receiver [Try.Companion] to make it statically available.
 * @return [Traversal] with source [Try] and focus in every [Try.Success] of the source.
 */
fun <A> Try.Companion.traversal(): Traversal<Try<A>, A> = object : Traversal<Try<A>, A> {
  override fun <F> modifyF(FA: Applicative<F>, s: Try<A>, f: (A) -> Kind<F, A>): Kind<F, Try<A>> =
    s.traverse(FA, f)
}

/**
 * [Each] instance definition for [Try].
 */
@extension
interface TryEachInstance<A> : Each<Try<A>, A> {
  override fun each(): Traversal<Try<A>, A> =
    Try.traversal()
}
