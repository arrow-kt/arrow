package arrow.instances

import arrow.Kind
import arrow.core.*
import arrow.data.*
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.extension
import arrow.instances.option.applicative.applicative
import arrow.instances.option.foldable.foldable
import arrow.instances.option.traverse.traverse
import arrow.instances.optiont.applicative.ap
import arrow.instances.optiont.monad.ap
import arrow.typeclasses.*

@extension
interface OptionTFunctorInstance<F> : Functor<OptionTPartialOf<F>> {

  fun FF(): Functor<F>

  override fun <A, B> OptionTOf<F, A>.map(f: (A) -> B): OptionT<F, B> = fix().map(FF(), f)

}

@extension
interface OptionTApplicativeInstance<F> : Applicative<OptionTPartialOf<F>>, OptionTFunctorInstance<F> {

  fun AF(): Applicative<F>

  override fun FF(): Functor<F> = AF()

  override fun <A> just(a: A): OptionT<F, A> = OptionT(AF().just(Option(a)))

  override fun <A, B> OptionTOf<F, A>.map(f: (A) -> B): OptionT<F, B> = fix().map(AF(), f)

  override fun <A, B> OptionTOf<F, A>.ap(ff: OptionTOf<F, (A) -> B>): OptionT<F, B> =
    fix().ap(AF(), ff)
}

@extension
interface OptionTMonadInstance<F> : Monad<OptionTPartialOf<F>>, OptionTApplicativeInstance<F> {

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
interface OptionTApplicativeErrorInstance<F, E> : ApplicativeError<OptionTPartialOf<F>, E>, OptionTApplicativeInstance<F> {

  fun AE(): ApplicativeError<F, E>

  override fun AF(): Applicative<F> = AE()

  override fun <A> raiseError(e: E): OptionT<F, A> =
    OptionT(AE().raiseError(e))

  override fun <A> OptionTOf<F, A>.handleErrorWith(f: (E) -> OptionTOf<F, A>): OptionT<F, A> = AE().run {
    OptionT(value().handleErrorWith { f(it).value() })
  }

}

@extension
interface OptionTMonadError<F, E> : MonadError<OptionTPartialOf<F>, E>, OptionTMonadInstance<F>, OptionTApplicativeErrorInstance<F, E> {

  fun ME(): MonadError<F, E>

  override fun AF(): Applicative<F> = ME()

  override fun AE(): ApplicativeError<F, E> = ME()

  override fun MF(): Monad<F> = ME()
}

@extension
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
interface OptionTFoldableInstance<F> : Foldable<OptionTPartialOf<F>> {

  fun FFF(): Foldable<F>

  override fun <A, B> OptionTOf<F, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(FFF(), b, f)

  override fun <A, B> OptionTOf<F, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(FFF(), lb, f)

}

@extension
interface OptionTTraverseInstance<F> : Traverse<OptionTPartialOf<F>>, OptionTFoldableInstance<F> {

  fun FFT(): Traverse<F>

  override fun FFF(): Foldable<F> = FFT()

  override fun <G, A, B> OptionTOf<F, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, OptionT<F, B>> =
    fix().traverse(FFT(), AP, f)

}

@extension
interface OptionTSemigroupKInstance<F> : SemigroupK<OptionTPartialOf<F>> {

  fun MF(): Monad<F>

  override fun <A> OptionTOf<F, A>.combineK(y: OptionTOf<F, A>): OptionT<F, A> = fix().orElse(MF(), { y.fix() })
}

@extension
interface OptionTMonoidKInstance<F> : MonoidK<OptionTPartialOf<F>>, OptionTSemigroupKInstance<F> {

  override fun MF(): Monad<F>

  override fun <A> empty(): OptionT<F, A> = OptionT(MF().just(None))
}

class OptionTContext<F>(val MF: Monad<F>) : OptionTMonadInstance<F>, OptionTMonoidKInstance<F> {

  override fun MF(): Monad<F> = MF

  override fun <A, B> OptionTOf<F, A>.map(f: (A) -> B): OptionT<F, B> =
    fix().map(f)
}

class OptionTContextPartiallyApplied<F>(val MF: Monad<F>) {
  @Deprecated(ExtensionsDSLDeprecated)
  infix fun <A> extensions(f: OptionTContext<F>.() -> A): A =
    f(OptionTContext(MF))
}

fun <F> ForOptionT(MF: Monad<F>): OptionTContextPartiallyApplied<F> =
  OptionTContextPartiallyApplied(MF)
