package arrow.optics.instances

import arrow.Kind
import arrow.core.Either
import arrow.core.fix
import arrow.core.traverse
import arrow.instance
import arrow.optics.Traversal
import arrow.optics.typeclasses.Each
import arrow.typeclasses.Applicative

fun <L, R> Either.Companion.traversal(): Traversal<Either<L, R>, R> = object : Traversal<Either<L, R>, R> {
  override fun <F> modifyF(FA: Applicative<F>, s: Either<L, R>, f: (R) -> Kind<F, R>): Kind<F, Either<L, R>> = with(Either.traverse<L>()) {
    FA.run { s.traverse(FA, f).map { it.fix() } }
  }
}

@instance(Either::class)
interface EitherEachInstance<L, R> : Each<Either<L, R>, R> {
  override fun each(): Traversal<Either<L, R>, R> =
    Either.traversal()
}
