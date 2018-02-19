package arrow.optics.instances

import arrow.data.Try
import arrow.data.TryOf
import arrow.data.traverse
import arrow.instance
import arrow.optics.Traversal
import arrow.optics.typeclasses.Each

@instance(Try::class)
interface TryEachInstance<A> : Each<TryOf<A>, A> {
    override fun each(): Traversal<TryOf<A>, A> =
            Traversal.fromTraversable(Try.traverse())
}