package arrow.instances

import arrow.Kind
import arrow.core.*
import arrow.data.*
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.extension
import arrow.instances.option.applicative.applicative
import arrow.instances.option.foldable.foldable
import arrow.instances.option.traverse.traverse
import arrow.typeclasses.*

@extension
interface OptionTContravariantInstance<F> : Contravariant<OptionTPartialOf<F>> {

  fun CF(): Contravariant<F>

  override fun <A, B> Kind<OptionTPartialOf<F>, A>.contramap(f: (B) -> A): Kind<OptionTPartialOf<F>, B> =
    OptionT(
      CF().run { fix().value.contramap<Option<A>, Option<B>> { it.map(f) } }
    )
}

@extension
interface OptionTDivideInstance<F> : Divide<OptionTPartialOf<F>>, OptionTContravariantInstance<F> {

  override fun CF(): Divide<F>

  override fun <A, B, Z> divide(fa: Kind<OptionTPartialOf<F>, A>, fb: Kind<OptionTPartialOf<F>, B>, f: (Z) -> Tuple2<A, B>): Kind<OptionTPartialOf<F>, Z> =
    OptionT(
      CF().divide(fa.value(), fb.value()) { opt ->
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

  override fun CF(): Divisible<F>

  override fun <A> conquer(): Kind<OptionTPartialOf<F>, A> =
    OptionT(CF().conquer())
}

@extension
interface OptionTDecidableInstance<F> : Decidable<OptionTPartialOf<F>>, OptionTDivisibleInstance<F> {

  override fun CF(): Decidable<F>

  override fun <A, B, Z> choose(fa: Kind<OptionTPartialOf<F>, A>, fb: Kind<OptionTPartialOf<F>, B>, f: (Z) -> Either<A, B>): Kind<OptionTPartialOf<F>, Z> =
    OptionT(
      CF().choose(fa.value(), fb.value()) { opt ->
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
interface OptionTFunctorInstance<F> : Functor<OptionTPartialOf<F>> {

  fun FF(): Functor<F>

  override fun <A, B> Kind<OptionTPartialOf<F>, A>.map(f: (A) -> B): OptionT<F, B> = fix().map(FF(), f)

}

@extension
interface OptionTApplicativeInstance<F> : Applicative<OptionTPartialOf<F>>, OptionTFunctorInstance<F> {

  fun MF(): Monad<F>

  override fun FF(): Functor<F> = MF()

  override fun <A> just(a: A): OptionT<F, A> = OptionT(MF().just(Option(a)))

  override fun <A, B> Kind<OptionTPartialOf<F>, A>.map(f: (A) -> B): OptionT<F, B> = fix().map(FF(), f)

  override fun <A, B> Kind<OptionTPartialOf<F>, A>.ap(ff: Kind<OptionTPartialOf<F>, (A) -> B>): OptionT<F, B> =
    fix().ap(MF(), ff)
}

@extension
interface OptionTMonadInstance<F> : Monad<OptionTPartialOf<F>>, OptionTApplicativeInstance<F> {

  override fun MF(): Monad<F>

  override fun <A, B> Kind<OptionTPartialOf<F>, A>.map(f: (A) -> B): OptionT<F, B> = fix().map(FF(), f)

  override fun <A, B> Kind<OptionTPartialOf<F>, A>.flatMap(f: (A) -> Kind<OptionTPartialOf<F>, B>): OptionT<F, B> = fix().flatMap(MF(), { f(it).fix() })

  override fun <A, B> Kind<OptionTPartialOf<F>, A>.ap(ff: Kind<OptionTPartialOf<F>, (A) -> B>): OptionT<F, B> =
    fix().ap(MF(), ff)

  override fun <A, B> tailRecM(a: A, f: (A) -> OptionTOf<F, Either<A, B>>): OptionT<F, B> =
    OptionT.tailRecM(MF(), a, f)

}

fun <F, A, B> OptionTOf<F, A>.foldLeft(FF: Foldable<F>, b: B, f: (B, A) -> B): B = FF.compose(Option.foldable()).foldLC(fix().value, b, f)

fun <F, A, B> OptionTOf<F, A>.foldRight(FF: Foldable<F>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = FF.compose(Option.foldable()).run {
  fix().value.foldRC(lb, f)
}

fun <F, G, A, B> OptionTOf<F, A>.traverse(FF: Traverse<F>, GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, OptionT<F, B>> {
  val fa = ComposedTraverse(FF, Option.traverse(), Option.applicative()).run { fix().value.traverseC(f, GA) }
  val mapper: (Kind<Nested<F, ForOption>, B>) -> OptionT<F, B> = { OptionT(FF.run { it.unnest().map { it.fix() } }) }
  return GA.run { fa.map(mapper) }
}

fun <F, G, A> OptionTOf<F, Kind<G, A>>.sequence(FF: Traverse<F>, GA: Applicative<G>): Kind<G, OptionT<F, A>> =
  traverse(FF, GA, ::identity)

@extension
interface OptionTFoldableInstance<F> : Foldable<OptionTPartialOf<F>> {

  fun FFF(): Foldable<F>

  override fun <A, B> Kind<OptionTPartialOf<F>, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(FFF(), b, f)

  override fun <A, B> Kind<OptionTPartialOf<F>, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(FFF(), lb, f)

}

@extension
interface OptionTTraverseInstance<F> : Traverse<OptionTPartialOf<F>>, OptionTFoldableInstance<F> {

  fun FFT(): Traverse<F>

  override fun FFF(): Foldable<F> = FFT()

  override fun <G, A, B> Kind<OptionTPartialOf<F>, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, OptionT<F, B>> =
    fix().traverse(FFT(), AP, f)

}

@extension
interface OptionTSemigroupKInstance<F> : SemigroupK<OptionTPartialOf<F>> {

  fun MF(): Monad<F>

  override fun <A> Kind<OptionTPartialOf<F>, A>.combineK(y: Kind<OptionTPartialOf<F>, A>): OptionT<F, A> = fix().orElse(MF(), { y.fix() })
}

@extension
interface OptionTMonoidKInstance<F> : MonoidK<OptionTPartialOf<F>>, OptionTSemigroupKInstance<F> {

  override fun MF(): Monad<F>

  override fun <A> empty(): OptionT<F, A> = OptionT(MF().just(None))
}

class OptionTContext<F>(val MF: Monad<F>) : OptionTMonadInstance<F>, OptionTMonoidKInstance<F> {

  override fun MF(): Monad<F> = MF

  override fun <A, B> Kind<OptionTPartialOf<F>, A>.map(f: (A) -> B): OptionT<F, B> =
    fix().map(f)
}

class OptionTContextPartiallyApplied<F>(val MF: Monad<F>) {
  @Deprecated(ExtensionsDSLDeprecated)
  infix fun <A> extensions(f: OptionTContext<F>.() -> A): A =
    f(OptionTContext(MF))
}

fun <F> ForOptionT(MF: Monad<F>): OptionTContextPartiallyApplied<F> =
  OptionTContextPartiallyApplied(MF)