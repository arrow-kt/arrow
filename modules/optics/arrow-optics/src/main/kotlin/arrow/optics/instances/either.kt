package arrow.optics.instances

import arrow.core.Either
import arrow.core.EitherOf
import arrow.core.traverse
import arrow.instance
import arrow.optics.Traversal
import arrow.optics.typeclasses.Each

@instance(Either::class)
interface EitherEachInstance<L, R> : Each<EitherOf<L, R>, R> {
    override fun each(): Traversal<EitherOf<L, R>, R> =
            Traversal.fromTraversable(Either.traverse())
}
