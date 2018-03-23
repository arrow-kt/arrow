package arrow.optics.instances

import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.instance
import arrow.optics.Traversal
import arrow.optics.fromTraversable
import arrow.optics.typeclasses.Each

@instance(Either::class)
interface EitherEachInstance<L> : Each<EitherPartialOf<L>, L> {
    override fun each(): Traversal<EitherPartialOf<L>, L> =
            Traversal.fromTraversable()
}
