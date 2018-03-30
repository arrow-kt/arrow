package arrow.data

import arrow.Kind
import arrow.core.*
import arrow.instances.*
import arrow.typeclasses.*

object EitherTFunctorInstanceImplicits {
  fun <F, L> instance(FF: Functor<F>): EitherTFunctorInstance<F, L> = object : EitherTFunctorInstance<F, L> {
    override fun FF(): Functor<F> = FF
  }
}

object EitherTApplicativeInstanceImplicits {
  fun <F, L> instance(MF: Monad<F>): EitherTApplicativeInstance<F, L> = object : EitherTApplicativeInstance<F, L> {
    override fun FF(): Functor<F> = MF

    override fun MF(): Monad<F> = MF
  }
}

object EitherTMonadInstanceImplicits {

  fun <F, L> instance(MF: Monad<F>): EitherTMonadInstance<F, L> = object : EitherTMonadInstance<F, L> {
    override fun FF(): Functor<F> = MF

    override fun MF(): Monad<F> = MF
  }
}

object EitherTApplicativeErrorInstanceImplicits {

  fun <F, L> instance(MF: Monad<F>): EitherTApplicativeErrorInstance<F, L> = object : EitherTApplicativeErrorInstance<F, L> {
    override fun FF(): Functor<F> = MF

    override fun MF(): Monad<F> = MF
  }
}

object EitherTMonadErrorInstanceImplicits {

  fun <F, L> instance(MF: Monad<F>): EitherTMonadErrorInstance<F, L> = object : EitherTMonadErrorInstance<F, L> {
    override fun FF(): Functor<F> = MF

    override fun MF(): Monad<F> = MF
  }
}

fun <F, A, B, C> EitherT<F, A, B>.foldLeft(b: C, f: (C, B) -> C, FF: Foldable<F>): C = FF.compose(Either.foldable<A>()).foldLC(value, b, f)

fun <F, A, B, C> EitherT<F, A, B>.foldRight(lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>, FF: Foldable<F>): Eval<C> = FF.compose(Either.foldable<A>()).run {
  value.foldRC(lb, f)
}

fun <F, A, B, G, C> EitherT<F, A, B>.traverse(f: (B) -> Kind<G, C>, GA: Applicative<G>, FF: Traverse<F>): Kind<G, EitherT<F, A, C>> {
  val fa: Kind<G, Kind<Nested<F, EitherPartialOf<A>>, C>> = ComposedTraverse(FF, Either.traverse(), Either.monad<A>()).traverseC(value, f, GA)
  return GA.run { fa.map({ EitherT(FF.run { it.unnest().map({ it.fix() }) }) }) }
}

object EitherTFoldableInstanceImplicits {

  fun <F, L> instance(FF: Foldable<F>): EitherTFoldableInstance<F, L> = object : EitherTFoldableInstance<F, L> {
    override fun FFF(): Foldable<F> = FF
  }
}

object EitherTTraverseInstanceImplicits {

  fun <F, L> instance(TF: Traverse<F>): EitherTTraverseInstance<F, L> = object : EitherTTraverseInstance<F, L> {
    override fun FFF(): Foldable<F> = TF

    override fun FF(): Functor<F> = TF

    override fun TF(): Traverse<F> = TF
  }
}

object EitherTSemigroupKInstanceImplicits {

  fun <F, L> instance(MF: Monad<F>): EitherTSemigroupKInstance<F, L> = object : EitherTSemigroupKInstance<F, L> {
    override fun MF(): Monad<F> = MF
  }
}

fun <F, L> EitherT.Companion.functor(FF: Functor<F>): Functor<EitherTPartialOf<F, L>> =
  EitherTFunctorInstanceImplicits.instance(FF)

fun <F, L> EitherT.Companion.applicative(MF: Monad<F>): Applicative<EitherTPartialOf<F, L>> =
  EitherTApplicativeInstanceImplicits.instance(MF)

fun <F, L> EitherT.Companion.monad(MF: Monad<F>): Monad<EitherTPartialOf<F, L>> =
  EitherTMonadInstanceImplicits.instance(MF)

fun <F, L> EitherT.Companion.applicativeError(MF: Monad<F>): ApplicativeError<EitherTPartialOf<F, L>, L> =
  EitherTApplicativeErrorInstanceImplicits.instance(MF)

fun <F, L> EitherT.Companion.monadError(MF: Monad<F>): MonadError<EitherTPartialOf<F, L>, L> =
  EitherTMonadErrorInstanceImplicits.instance(MF)

fun <F, A> EitherT.Companion.traverse(FF: Traverse<F>): Traverse<EitherTPartialOf<F, A>> =
  EitherTTraverseInstanceImplicits.instance(FF)

fun <F, A> EitherT.Companion.foldable(FF: Traverse<F>): Foldable<EitherTPartialOf<F, A>> =
  EitherTFoldableInstanceImplicits.instance(FF)

fun <F, L> EitherT.Companion.semigroupK(MF: Monad<F>): SemigroupK<EitherTPartialOf<F, L>> =
  EitherTSemigroupKInstanceImplicits.instance(MF)
