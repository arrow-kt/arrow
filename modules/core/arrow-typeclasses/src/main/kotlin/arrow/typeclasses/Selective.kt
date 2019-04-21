package arrow.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.andThen
import arrow.core.left
import arrow.core.right

/**
 * ank_macro_hierarchy(arrow.typeclasses.Selective)
 */
interface Selective<F> : Applicative<F> {
  fun <A, B> Kind<F, Either<A, B>>.select(f: Kind<F, (A) -> B>): Kind<F, B>

  private fun Kind<F, Boolean>.selector(): Kind<F, Either<Unit, Unit>> =
    map { bool -> if (bool) Unit.left() else Unit.right() }

  fun <A, B, C> Kind<F, Either<A, B>>.branch(fl: Kind<F, (A) -> C>, fr: Kind<F, (B) -> C>): Kind<F, C> {
    val nested: Kind<F, Either<A, Either<B, Nothing>>> = map { it.map(::Left) }
    val ffl: Kind<F, (A) -> Either<Nothing, C>> = fl.map { it.andThen(::Right) }
    return nested.select(ffl).select(fr)
  }

  fun <A> Kind<F, Boolean>.whenS(x: Kind<F, () -> Unit>): Kind<F, Unit> =
    selector().select(x.map { f -> { _: Unit -> f() } })

  fun <A> Kind<F, Boolean>.ifS(fl: Kind<F, A>, fr: Kind<F, A>): Kind<F, A> =
    selector().branch(fl.map { { _: Unit -> it } }, fr.map { { _: Unit -> it } })

  fun <A> Kind<F, Boolean>.orS(f: Kind<F, Boolean>): Kind<F, Boolean> =
    ifS(just(true), f)

  fun <A> Kind<F, Boolean>.andS(f: Kind<F, Boolean>): Kind<F, Boolean> =
    ifS(f, just(false))
}
