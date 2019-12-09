@file:Suppress("UnusedImports")

package arrow.core.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForOption
import arrow.core.None
import arrow.core.Option
import arrow.core.OptionOf
import arrow.core.SequenceK
import arrow.core.Some
import arrow.core.Tuple2
import arrow.core.extensions.option.monad.map
import arrow.core.extensions.option.monad.monad
import arrow.core.fix
import arrow.core.identity
import arrow.core.k
import arrow.core.orElse
import arrow.extension
import arrow.typeclasses.Alternative
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Apply
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.FunctorFilter
import arrow.typeclasses.Hash
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadCombine
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadFilter
import arrow.typeclasses.MonadFx
import arrow.typeclasses.MonadSyntax
import arrow.typeclasses.Monoid
import arrow.typeclasses.MonoidK
import arrow.typeclasses.Monoidal
import arrow.typeclasses.Selective
import arrow.typeclasses.Semigroup
import arrow.typeclasses.SemigroupK
import arrow.typeclasses.Semigroupal
import arrow.typeclasses.Semiring
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse
import arrow.typeclasses.TraverseFilter
import arrow.core.extensions.traverse as optionTraverse
import arrow.core.extensions.traverseFilter as optionTraverseFilter
import arrow.core.select as optionSelect
import arrow.typeclasses.Semialign
import arrow.core.Ior
import arrow.core.some
import arrow.core.toT
import arrow.typeclasses.Unalign
import arrow.typeclasses.Align

@extension
interface OptionSemigroup<A> : Semigroup<Option<A>> {

  fun SG(): Semigroup<A>

  override fun Option<A>.combine(b: Option<A>): Option<A> =
    when (this) {
      is Some<A> -> when (b) {
        is Some<A> -> Some(SG().run { t.combine(b.t) })
        None -> this
      }
      None -> b
    }
}

@extension
interface OptionSemigroupal : Semigroupal<ForOption> {
  override fun <A, B> Kind<ForOption, A>.product(fb: Kind<ForOption, B>): Kind<ForOption, Tuple2<A, B>> =
    fb.fix().ap(this.map { a: A -> { b: B -> Tuple2(a, b) } })
}

@extension
interface OptionMonoidal : Monoidal<ForOption>, OptionSemigroupal {
  override fun <A> identity(): Kind<ForOption, A> = None
}

@extension
interface OptionMonoid<A> : Monoid<Option<A>>, OptionSemigroup<A> {
  override fun SG(): Semigroup<A>
  override fun empty(): Option<A> = None
}

@extension
interface OptionSemiring<A> : Semiring<Option<A>> {

  fun SG(): Semiring<A>
  override fun zero(): Option<A> = None
  override fun one(): Option<A> = None

  override fun Option<A>.combine(b: Option<A>): Option<A> =
    when (this) {
      is Some<A> -> when (b) {
        is Some<A> -> Some(SG().run { t.combine(b.t) })
        None -> this
      }
      None -> b
    }

  override fun Option<A>.combineMultiplicate(b: Option<A>): Option<A> =
    when (this) {
      is Some<A> -> when (b) {
        is Some<A> -> Some(SG().run { t.combineMultiplicate(b.t) })
        None -> this
      }
      None -> b
    }
}

@extension
interface OptionApplicativeError : ApplicativeError<ForOption, Unit>, OptionApplicative {
  override fun <A> raiseError(e: Unit): Option<A> =
    None

  override fun <A> OptionOf<A>.handleErrorWith(f: (Unit) -> OptionOf<A>): Option<A> =
    fix().orElse { f(Unit).fix() }
}

@extension
interface OptionMonadError : MonadError<ForOption, Unit>, OptionMonad {
  override fun <A> raiseError(e: Unit): OptionOf<A> =
    None

  override fun <A> OptionOf<A>.handleErrorWith(f: (Unit) -> OptionOf<A>): Option<A> =
    fix().orElse { f(Unit).fix() }
}

@extension
interface OptionEq<A> : Eq<Option<A>> {

  fun EQ(): Eq<A>

  override fun Option<A>.eqv(b: Option<A>): Boolean = when (this) {
    is Some -> when (b) {
      None -> false
      is Some -> EQ().run { t.eqv(b.t) }
    }
    None -> when (b) {
      None -> true
      is Some -> false
    }
  }
}

@extension
interface OptionShow<A> : Show<Option<A>> {
  override fun Option<A>.show(): String =
    toString()
}

@extension
interface OptionFunctor : Functor<ForOption> {
  override fun <A, B> OptionOf<A>.map(f: (A) -> B): Option<B> =
    fix().map(f)
}

@extension
interface OptionApply : Apply<ForOption> {
  override fun <A, B> OptionOf<A>.ap(ff: OptionOf<(A) -> B>): Option<B> =
    fix().ap(ff)

  override fun <A, B> OptionOf<A>.map(f: (A) -> B): Option<B> =
    fix().map(f)
}

@extension
interface OptionApplicative : Applicative<ForOption> {
  override fun <A, B> OptionOf<A>.ap(ff: OptionOf<(A) -> B>): Option<B> =
    fix().ap(ff)

  override fun <A, B> OptionOf<A>.map(f: (A) -> B): Option<B> =
    fix().map(f)

  override fun <A> just(a: A): Option<A> =
    Option.just(a)
}

@extension
interface OptionSelective : Selective<ForOption>, OptionApplicative {
  override fun <A, B> OptionOf<Either<A, B>>.select(f: OptionOf<(A) -> B>): Option<B> =
    fix().optionSelect(f)
}

@extension
interface OptionMonad : Monad<ForOption> {
  override fun <A, B> OptionOf<A>.ap(ff: OptionOf<(A) -> B>): Option<B> =
    fix().ap(ff)

  override fun <A, B> OptionOf<A>.flatMap(f: (A) -> OptionOf<B>): Option<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> OptionOf<Either<A, B>>): Option<B> =
    Option.tailRecM(a, f)

  override fun <A, B> OptionOf<A>.map(f: (A) -> B): Option<B> =
    fix().map(f)

  override fun <A> just(a: A): Option<A> =
    Option.just(a)

  override fun <A, B> OptionOf<Either<A, B>>.select(f: OptionOf<(A) -> B>): OptionOf<B> =
    fix().optionSelect(f)

  override val fx: MonadFx<ForOption>
    get() = OptionFxMonad
}

internal object OptionFxMonad : MonadFx<ForOption> {
  override val M: Monad<ForOption> = Option.monad()
  override fun <A> monad(c: suspend MonadSyntax<ForOption>.() -> A): Option<A> =
    super.monad(c).fix()
}

@extension
interface OptionFoldable : Foldable<ForOption> {
  override fun <A> OptionOf<A>.exists(p: (A) -> Boolean): Boolean =
    fix().exists(p)

  override fun <A, B> OptionOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> OptionOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)

  override fun <A> OptionOf<A>.forAll(p: (A) -> Boolean): Boolean =
    fix().forall(p)

  override fun <A> OptionOf<A>.isEmpty(): Boolean =
    fix().isEmpty()

  override fun <A> OptionOf<A>.nonEmpty(): Boolean =
    fix().nonEmpty()
}

@extension
interface OptionSemigroupK : SemigroupK<ForOption> {
  override fun <A> OptionOf<A>.combineK(y: OptionOf<A>): Option<A> =
    orElse { y.fix() }
}

@extension
interface OptionMonoidK : MonoidK<ForOption> {
  override fun <A> empty(): Option<A> =
    Option.empty()

  override fun <A> OptionOf<A>.combineK(y: OptionOf<A>): Option<A> =
    orElse { y.fix() }
}

fun <A, G, B> OptionOf<A>.traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Option<B>> = GA.run {
  fix().fold({ just(None) }, { f(it).map { Some(it) } })
}

fun <A, G> OptionOf<Kind<G, A>>.sequence(GA: Applicative<G>): Kind<G, Option<A>> =
  optionTraverse(GA, ::identity)

fun <A, G, B> OptionOf<A>.traverseFilter(GA: Applicative<G>, f: (A) -> Kind<G, Option<B>>): Kind<G, Option<B>> = GA.run {
  fix().fold({ just(None) }, f)
}

@extension
interface OptionTraverse : Traverse<ForOption> {
  override fun <A, B> OptionOf<A>.map(f: (A) -> B): Option<B> =
    fix().map(f)

  override fun <G, A, B> OptionOf<A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Option<B>> =
    optionTraverse(AP, f)

  override fun <A> OptionOf<A>.exists(p: (A) -> Boolean): Boolean =
    fix().exists(p)

  override fun <A, B> OptionOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> OptionOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)

  override fun <A> OptionOf<A>.forAll(p: (A) -> Boolean): Boolean =
    fix().forall(p)

  override fun <A> OptionOf<A>.isEmpty(): Boolean =
    fix().isEmpty()

  override fun <A> OptionOf<A>.nonEmpty(): Boolean =
    fix().nonEmpty()
}

@extension
interface OptionHash<A> : Hash<Option<A>>, OptionEq<A> {

  fun HA(): Hash<A>

  override fun EQ(): Eq<A> = HA()

  override fun Option<A>.hash(): Int = fold({
    None.hashCode()
  }, {
    HA().run { it.hash() }
  })
}

@extension
interface OptionFunctorFilter : FunctorFilter<ForOption> {
  override fun <A, B> Kind<ForOption, A>.filterMap(f: (A) -> Option<B>): Option<B> =
    fix().filterMap(f)

  override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Option<B> =
    fix().map(f)
}

fun <A> Option.Companion.fx(c: suspend MonadSyntax<ForOption>.() -> A): Option<A> =
  Option.monad().fx.monad(c).fix()

@extension
interface OptionMonadCombine : MonadCombine<ForOption>, OptionAlternative {
  override fun <A> empty(): Option<A> =
    Option.empty()

  override fun <A, B> Kind<ForOption, A>.filterMap(f: (A) -> Option<B>): Option<B> =
    fix().filterMap(f)

  override fun <A, B> Kind<ForOption, A>.ap(ff: Kind<ForOption, (A) -> B>): Option<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForOption, A>.flatMap(f: (A) -> Kind<ForOption, B>): Option<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, OptionOf<Either<A, B>>>): Option<B> =
    Option.tailRecM(a, f)

  override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Option<B> =
    fix().map(f)

  override fun <A, B, Z> Kind<ForOption, A>.map2(fb: Kind<ForOption, B>, f: (Tuple2<A, B>) -> Z): Option<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): Option<A> =
    Option.just(a)

  override fun <A> Kind<ForOption, A>.some(): Option<SequenceK<A>> =
    fix().fold(
      { Option.empty() },
      {
        Sequence {
          object : Iterator<A> {
            override fun hasNext(): Boolean = true

            override fun next(): A = it
          }
        }.k().just().fix()
      }
    )

  override fun <A> Kind<ForOption, A>.many(): Option<SequenceK<A>> =
    fix().fold(
      { emptySequence<A>().k().just().fix() },
      {
        Sequence {
          object : Iterator<A> {
            override fun hasNext(): Boolean = true

            override fun next(): A = it
          }
        }.k().just().fix()
      }
    )
}

@extension
interface OptionTraverseFilter : TraverseFilter<ForOption> {
  override fun <A> Kind<ForOption, A>.filter(f: (A) -> Boolean): Option<A> =
    fix().filter(f)

  override fun <G, A, B> Kind<ForOption, A>.traverseFilter(AP: Applicative<G>, f: (A) -> Kind<G, Option<B>>): Kind<G, Option<B>> =
    optionTraverseFilter(AP, f)

  override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Option<B> =
    fix().map(f)

  override fun <G, A, B> Kind<ForOption, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Option<B>> =
    optionTraverse(AP, f)

  override fun <A> Kind<ForOption, A>.exists(p: (A) -> Boolean): Boolean =
    fix().exists(p)

  override fun <A, B> Kind<ForOption, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ForOption, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)

  override fun <A> OptionOf<A>.forAll(p: (A) -> Boolean): Boolean =
    fix().forall(p)

  override fun <A> Kind<ForOption, A>.isEmpty(): Boolean =
    fix().isEmpty()

  override fun <A> Kind<ForOption, A>.nonEmpty(): Boolean =
    fix().nonEmpty()
}

@extension
interface OptionMonadFilter : MonadFilter<ForOption> {
  override fun <A> empty(): Option<A> =
    Option.empty()

  override fun <A, B> Kind<ForOption, A>.filterMap(f: (A) -> Option<B>): Option<B> =
    fix().filterMap(f)

  override fun <A, B> Kind<ForOption, A>.ap(ff: Kind<ForOption, (A) -> B>): Option<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForOption, A>.flatMap(f: (A) -> Kind<ForOption, B>): Option<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, OptionOf<Either<A, B>>>): Option<B> =
    Option.tailRecM(a, f)

  override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Option<B> =
    fix().map(f)

  override fun <A, B, Z> Kind<ForOption, A>.map2(fb: Kind<ForOption, B>, f: (Tuple2<A, B>) -> Z): Option<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): Option<A> =
    Option.just(a)
}

@extension
interface OptionAlternative : Alternative<ForOption>, OptionApplicative {
  override fun <A> empty(): Kind<ForOption, A> = None
  override fun <A> Kind<ForOption, A>.orElse(b: Kind<ForOption, A>): Kind<ForOption, A> =
    if (fix().isEmpty()) b
    else this
}

@extension
interface OptionEqK : EqK<ForOption> {
  override fun <A> Kind<ForOption, A>.eqK(other: Kind<ForOption, A>, EQ: Eq<A>) =
    (this.fix() to other.fix()).let { (a, b) ->
      when (a) {
        is None -> {
          when (b) {
            is None -> true
            is Some -> false
          }
        }
        is Some -> {
          when (b) {
            is None -> false
            is Some -> EQ.run { a.t.eqv(b.t) }
          }
        }
      }
    }
}

@extension
interface OptionSemialign : Semialign<ForOption>, OptionFunctor {
  override fun <A, B> align(a: Kind<ForOption, A>, b: Kind<ForOption, B>): Kind<ForOption, Ior<A, B>> =
    Ior.fromOptions(a.fix(), b.fix())
}

@extension
interface OptionAlign : Align<ForOption>, OptionSemialign {
  override fun <A> empty(): Kind<ForOption, A> = Option.empty()
}

@extension
interface OptionUnalign : Unalign<ForOption>, OptionSemialign {
  override fun <A, B> unalign(ior: Kind<ForOption, Ior<A, B>>): Tuple2<Kind<ForOption, A>, Kind<ForOption, B>> =
    when (val a = ior.fix()) {
      is None -> None toT None
      is Some -> when (val b = a.t) {
        is Ior.Left -> b.value.some() toT None
        is Ior.Right -> None toT b.value.some()
        is Ior.Both -> b.leftValue.some() toT b.rightValue.some()
      }
    }
}
