package arrow.optics.instances

import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.core.Option
import arrow.core.OptionOf
import arrow.core.traverse
import arrow.data.ListK
import arrow.data.ListKOf
import arrow.data.MapK
import arrow.data.MapKPartialOf
import arrow.data.traverse
import arrow.instance
import arrow.optics.Traversal
import arrow.optics.fromTraversable
import arrow.optics.typeclasses.Each

@instance(Either::class)
interface EitherEachInstance<L> : Each<EitherPartialOf<L>, L> {
    override fun each(): Traversal<EitherPartialOf<L>, L> =
            Traversal.fromTraversable()
}

@instance(ListK::class)
interface ListKEachInstance<A> : Each<ListKOf<A>, A> {
    override fun each(): Traversal<ListKOf<A>, A> =
            Traversal.fromTraversable(ListK.traverse())
}

@instance(MapK::class)
interface MapKEachInstance<K> : Each<MapKPartialOf<K>, K> {
    override fun each(): Traversal<MapKPartialOf<K>, K> =
            Traversal.fromTraversable()
}

@instance(Option::class)
interface OptionEachInstance<A> : Each<OptionOf<A>, A> {
    override fun each(): Traversal<OptionOf<A>, A> =
            Traversal.fromTraversable(Option.traverse())
}
