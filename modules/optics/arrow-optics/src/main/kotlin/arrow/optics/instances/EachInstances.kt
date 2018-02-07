package arrow.optics.instances

import arrow.core.Either
import arrow.core.EitherKindPartial
import arrow.core.Option
import arrow.core.OptionKind
import arrow.core.traverse
import arrow.data.ListKW
import arrow.data.ListKWKind
import arrow.data.MapKW
import arrow.data.MapKWKindPartial
import arrow.data.Try
import arrow.data.TryKind
import arrow.data.traverse
import arrow.instance
import arrow.optics.Traversal
import arrow.optics.fromTraversable
import arrow.optics.typeclasses.Each

@instance(Either::class)
interface EitherEachInstance<L> : Each<EitherKindPartial<L>, L> {
    override fun each(): Traversal<EitherKindPartial<L>, L> =
            Traversal.fromTraversable()
}

@instance(ListKW::class)
interface ListKWEachInstance<A> : Each<ListKWKind<A>, A> {
    override fun each(): Traversal<ListKWKind<A>, A> =
            Traversal.fromTraversable(ListKW.traverse())
}

@instance(MapKW::class)
interface MapKWEachInstance<K> : Each<MapKWKindPartial<K>, K> {
    override fun each(): Traversal<MapKWKindPartial<K>, K> =
            Traversal.fromTraversable()
}

@instance(Option::class)
interface OptionEachInstance<A> : Each<OptionKind<A>, A> {
    override fun each(): Traversal<OptionKind<A>, A> =
            Traversal.fromTraversable(Option.traverse())
}
