package arrow.optics.instances

import arrow.Kind
import arrow.core.Try
import arrow.instance
import arrow.instances.traverse
import arrow.optics.Traversal
import arrow.optics.typeclasses.Each
import arrow.typeclasses.Applicative

fun <A> Try.Companion.traversal(): Traversal<Try<A>, A> = object : Traversal<Try<A>, A> {
  override fun <F> modifyF(FA: Applicative<F>, s: Try<A>, f: (A) -> Kind<F, A>): Kind<F, Try<A>> =
    s.traverse(FA, f)
}

@instance(Try::class)
interface TryEachInstance<A> : Each<Try<A>, A> {
  override fun each(): Traversal<Try<A>, A> =
    Try.traversal()
}
