package arrow.optics.extensions

import arrow.Kind
import arrow.core.Try
import arrow.core.extensions.traverse
import arrow.extension
import arrow.optics.Traversal
import arrow.optics.typeclasses.Each
import arrow.typeclasses.Applicative

@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or a an effect handler like IO",
  ReplaceWith("Either.Companion.traversal()")
)
fun <A> Try.Companion.traversal(): Traversal<Try<A>, A> = object : Traversal<Try<A>, A> {
  override fun <F> modifyF(FA: Applicative<F>, s: Try<A>, f: (A) -> Kind<F, A>): Kind<F, Try<A>> =
    s.traverse(FA, f)
}

@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or a an effect handler like IO",
  ReplaceWith("EitherEach<Nothing, A>")
)
@extension
interface TryEach<A> : Each<Try<A>, A> {
  override fun each(): Traversal<Try<A>, A> =
    Try.traversal()
}
