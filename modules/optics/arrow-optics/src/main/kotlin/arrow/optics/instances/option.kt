package arrow.optics.instances

import arrow.Kind
import arrow.core.Option
import arrow.core.traverse
import arrow.instance
import arrow.optics.Traversal
import arrow.optics.typeclasses.Each
import arrow.typeclasses.Applicative

fun <A> Option.Companion.traversal(): Traversal<Option<A>, A> = object : Traversal<Option<A>, A> {
  override fun <F> modifyF(FA: Applicative<F>, s: Option<A>, f: (A) -> Kind<F, A>): Kind<F, Option<A>> = with(Option.traverse()) {
    s.traverse(FA, f)
  }
}

@instance(Option::class)
interface OptionEachInstance<A> : Each<Option<A>, A> {
  override fun each(): Traversal<Option<A>, A> =
    Option.traversal()
}