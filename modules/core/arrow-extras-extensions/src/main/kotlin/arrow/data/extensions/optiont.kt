package arrow.data.extensions

import arrow.Kind
import arrow.core.*
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.option.foldable.foldable
import arrow.core.extensions.option.traverse.traverse
import arrow.data.*
import arrow.data.extensions.optiont.monad.monad
import arrow.extension
import arrow.typeclasses.*
import arrow.typeclasses.suspended.monad.Fx
import arrow.undocumented

@extension
interface OptionTFunctor<F> : Functor<OptionTPartialOf<F>> {

  fun FF(): Functor<F>

  override fun <A, B> OptionTOf<F, A>.map(f: (A) -> B): OptionT<F, B> = fix().map(FF(), f)

}

@extension
interface OptionTApplicative<F> : Applicative<OptionTPartialOf<F>>, OptionTFunctor<F> {

  fun AF(): Applicative<F>

  override fun FF(): Functor<F> = AF()

  override fun <A> just(a: A): OptionT<F, A> = OptionT(AF().just(Option(a)))

  override fun <A, B> OptionTOf<F, A>.map(f: (A) -> B): OptionT<F, B> = fix().map(AF(), f)

  override fun <A, B> OptionTOf<F, A>.ap(ff: OptionTOf<F, (A) -> B>): OptionT<F, B> =
    fix().ap(AF(), ff)
}

@extension
interface OptionTMonad<F> : Monad<OptionTPartialOf<F>>, OptionTApplicative<F> {

  fun MF(): Monad<F>

  override fun AF(): Applicative<F> = MF()

  override fun <A, B> OptionTOf<F, A>.map(f: (A) -> B): OptionT<F, B> = fix().map(FF(), f)

  override fun <A, B> OptionTOf<F, A>.flatMap(f: (A) -> OptionTOf<F, B>): OptionT<F, B> = fix().flatMap(MF()) { f(it).fix() }

  override fun <A, B> OptionTOf<F, A>.ap(ff: OptionTOf<F, (A) -> B>): OptionT<F, B> =
    fix().ap(MF(), ff)

  override fun <A, B> tailRecM(a: A, f: (A) -> OptionTOf<F, Either<A, B>>): OptionT<F, B> =
    OptionT.tailRecM(MF(), a, f)

}

@extension
interface OptionTApplicativeError<F, E> : ApplicativeError<OptionTPartialOf<F>, E>, OptionTApplicative<F> {

  fun AE(): ApplicativeError<F, E>

  override fun AF(): Applicative<F> = AE()

  override fun <A> raiseError(e: E): OptionT<F, A> =
    OptionT(AE().raiseError(e))

  override fun <A> OptionTOf<F, A>.handleErrorWith(f: (E) -> OptionTOf<F, A>): OptionT<F, A> = AE().run {
    OptionT(value().handleErrorWith { f(it).value() })
  }

}

@extension
interface OptionTMonadError<F, E> : MonadError<OptionTPartialOf<F>, E>, OptionTMonad<F>, OptionTApplicativeError<F, E> {

  fun ME(): MonadError<F, E>

  override fun AF(): Applicative<F> = ME()

  override fun AE(): ApplicativeError<F, E> = ME()

  override fun MF(): Monad<F> = ME()
}

@extension
@undocumented
interface OptionTMonadThrow<F> : MonadThrow<OptionTPartialOf<F>>, OptionTMonadError<F, Throwable> {
  override fun ME(): MonadError<F, Throwable>
}

fun <F, A, B> OptionTOf<F, A>.foldLeft(FF: Foldable<F>, b: B, f: (B, A) -> B): B = FF.compose(Option.foldable()).foldLC(value(), b, f)

fun <F, A, B> OptionTOf<F, A>.foldRight(FF: Foldable<F>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = FF.compose(Option.foldable()).run {
  value().foldRC(lb, f)
}

fun <F, G, A, B> OptionTOf<F, A>.traverse(FF: Traverse<F>, GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, OptionT<F, B>> {
  val fa = ComposedTraverse(FF, Option.traverse(), Option.applicative()).run { value().traverseC(f, GA) }
  val mapper: (Kind<Nested<F, ForOption>, B>) -> OptionT<F, B> = { OptionT(FF.run { it.unnest().map { it.fix() } }) }
  return GA.run { fa.map(mapper) }
}

fun <F, G, A> OptionTOf<F, Kind<G, A>>.sequence(FF: Traverse<F>, GA: Applicative<G>): Kind<G, OptionT<F, A>> =
  traverse(FF, GA, ::identity)

@extension
interface OptionTFoldable<F> : Foldable<OptionTPartialOf<F>> {

  fun FFF(): Foldable<F>

  override fun <A, B> OptionTOf<F, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(FFF(), b, f)

  override fun <A, B> OptionTOf<F, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(FFF(), lb, f)

}

@extension
interface OptionTTraverse<F> : Traverse<OptionTPartialOf<F>>, OptionTFoldable<F> {

  fun FFT(): Traverse<F>

  override fun FFF(): Foldable<F> = FFT()

  override fun <G, A, B> OptionTOf<F, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, OptionT<F, B>> =
    fix().traverse(FFT(), AP, f)

}

@extension
interface OptionTSemigroupK<F> : SemigroupK<OptionTPartialOf<F>> {

  fun MF(): Monad<F>

  override fun <A> OptionTOf<F, A>.combineK(y: OptionTOf<F, A>): OptionT<F, A> = fix().orElse(MF(), { y.fix() })
}

@extension
interface OptionTMonoidK<F> : MonoidK<OptionTPartialOf<F>>, OptionTSemigroupK<F> {

  override fun MF(): Monad<F>

  override fun <A> empty(): OptionT<F, A> = OptionT(MF().just(None))
}

@extension
interface OptionTContravariantInstance<F> : Contravariant<OptionTPartialOf<F>> {
  fun CF(): Contravariant<F>

  override fun <A, B> Kind<OptionTPartialOf<F>, A>.contramap(f: (B) -> A): Kind<OptionTPartialOf<F>, B> =
    OptionT(
      CF().run { value().contramap<Option<A>, Option<B>> { it.map(f) } }
    )
}

@extension
interface OptionTDivideInstance<F> : Divide<OptionTPartialOf<F>>, OptionTContravariantInstance<F> {
  fun DF(): Divide<F>
  override fun CF(): Contravariant<F> = DF()

  override fun <A, B, Z> divide(fa: Kind<OptionTPartialOf<F>, A>, fb: Kind<OptionTPartialOf<F>, B>, f: (Z) -> Tuple2<A, B>): Kind<OptionTPartialOf<F>, Z> =
    OptionT(
      DF().divide(fa.value(), fb.value()) { opt ->
        opt.map(f).fold({
          none<A>() toT none()
        }, { (a, b) ->
          a.some() toT b.some()
        })
      }
    )
}

@extension
interface OptionTDivisibleInstance<F> : Divisible<OptionTPartialOf<F>>, OptionTDivideInstance<F> {
  fun DFF(): Divisible<F>
  override fun DF(): Divide<F> = DFF()

  override fun <A> conquer(): Kind<OptionTPartialOf<F>, A> =
    OptionT(DFF().conquer())
}

@extension
interface OptionTDecidableInstance<F> : Decidable<OptionTPartialOf<F>>, OptionTDivisibleInstance<F> {
  fun DFFF(): Decidable<F>
  override fun DFF(): Divisible<F> = DFFF()

  override fun <A, B, Z> choose(fa: Kind<OptionTPartialOf<F>, A>, fb: Kind<OptionTPartialOf<F>, B>, f: (Z) -> Either<A, B>): Kind<OptionTPartialOf<F>, Z> =
    OptionT(
      DFFF().choose(fa.value(), fb.value()) { opt ->
        opt.map(f).fold({
          none<A>().left()
        }, { either ->
          either.fold({ a ->
            a.some().left()
          }, { b ->
            b.some().right()
          })
        })
      }
    )
}

@extension
interface OptionTFx<F> : Fx<OptionTPartialOf<F>> {

  fun M(): Monad<F>

  override fun monad(): Monad<OptionTPartialOf<F>> =
    OptionT.monad(M())

}