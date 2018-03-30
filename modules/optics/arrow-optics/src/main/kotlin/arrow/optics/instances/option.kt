package arrow.optics.instances

import arrow.core.Option
import arrow.core.OptionOf
import arrow.core.traverse
import arrow.instance
import arrow.optics.Traversal
import arrow.optics.typeclasses.Each

@instance(Option::class)
interface OptionEachInstance<A> : Each<OptionOf<A>, A> {
  override fun each(): Traversal<OptionOf<A>, A> =
    Traversal.fromTraversable(Option.traverse())
}