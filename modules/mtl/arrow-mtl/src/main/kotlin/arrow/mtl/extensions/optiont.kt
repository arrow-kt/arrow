package arrow.mtl.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForOption
import arrow.core.None
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.option.foldable.foldable
import arrow.core.extensions.option.traverse.traverse
import arrow.core.extensions.option.traverseFilter.traverseFilter
import arrow.core.fix
import arrow.core.identity
import arrow.core.left
import arrow.core.none
import arrow.core.right
import arrow.core.some
import arrow.core.toT
import arrow.extension
import arrow.mtl.OptionT
import arrow.mtl.OptionTOf
import arrow.mtl.OptionTPartialOf
import arrow.mtl.extensions.optiont.monad.monad
import arrow.mtl.fix
import arrow.mtl.typeclasses.ComposedTraverse
import arrow.mtl.typeclasses.Nested
import arrow.mtl.typeclasses.compose
import arrow.mtl.typeclasses.unnest
import arrow.mtl.value
import arrow.typeclasses.Alternative
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Contravariant
import arrow.typeclasses.Decidable
import arrow.typeclasses.Divide
import arrow.typeclasses.Divisible
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.FunctorFilter
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadSyntax
import arrow.typeclasses.MonadThrow
import arrow.typeclasses.MonoidK
import arrow.typeclasses.SemigroupK
import arrow.typeclasses.Traverse
import arrow.typeclasses.TraverseFilter
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
  val fa = ComposedTraverse(FF, Option.traverse()).run { value().traverseC(f, GA) }
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
interface OptionTFunctorFilter<F> : FunctorFilter<OptionTPartialOf<F>>, OptionTFunctor<F> {

  override fun FF(): Functor<F>

  override fun <A, B> OptionTOf<F, A>.filterMap(f: (A) -> Option<B>): OptionT<F, B> =
    fix().filterMap(FF(), f)
}

@extension
interface OptionTTraverseFilter<F> :
  TraverseFilter<OptionTPartialOf<F>>,
  OptionTTraverse<F> {

  override fun FFT(): Traverse<F> = FFF()

  override fun FFF(): TraverseFilter<F>

  override fun <G, A, B> Kind<OptionTPartialOf<F>, A>.traverseFilter(AP: Applicative<G>, f: (A) -> Kind<G, Option<B>>): Kind<G, OptionT<F, B>> =
    fix().traverseFilter(f, AP, FFF())
}

fun <F, A> OptionT.Companion.fx(M: Monad<F>, c: suspend MonadSyntax<OptionTPartialOf<F>>.() -> A): OptionT<F, A> =
  OptionT.monad(M).fx.monad(c).fix()

fun <F, G, A, B> OptionT<F, A>.traverseFilter(f: (A) -> Kind<G, Option<B>>, GA: Applicative<G>, FF: Traverse<F>): Kind<G, OptionT<F, B>> {
  val fa = ComposedTraverseFilter(FF, Option.traverseFilter()).traverseFilterC(value(), f, GA)
  val mapper: (Kind<Nested<F, ForOption>, B>) -> OptionT<F, B> = { nested -> OptionT(FF.run { nested.unnest().map { it.fix() } }) }
  return GA.run { fa.map(mapper) }
}

@extension
interface OptionTAlternative<F> : Alternative<OptionTPartialOf<F>>, OptionTApplicative<F> {
  override fun AF(): Applicative<F> = MF()
  fun MF(): Monad<F>
  override fun <A> empty(): Kind<OptionTPartialOf<F>, A> = OptionT.none(AF())
  override fun <A> Kind<OptionTPartialOf<F>, A>.orElse(b: Kind<OptionTPartialOf<F>, A>): Kind<OptionTPartialOf<F>, A> =
    OptionT(
      MF().fx.monad {
        val l = !value()
        if (l.isEmpty()) !b.value()
        else l
      }
    )
}
