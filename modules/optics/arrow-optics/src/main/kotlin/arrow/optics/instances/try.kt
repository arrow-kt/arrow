package arrow.optics.instances

import arrow.core.Try
import arrow.core.TryOf
import arrow.core.traverse
import arrow.instance
import arrow.optics.Traversal
import arrow.optics.typeclasses.Each

@instance(Try::class)
interface TryEachInstance<A> : Each<TryOf<A>, A> {
    override fun each(): Traversal<TryOf<A>, A> =
            Traversal.fromTraversable(Try.traverse())
}
