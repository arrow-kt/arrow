package arrow.core.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForSequenceK
import arrow.core.Ior
import arrow.core.None
import arrow.core.Option
import arrow.core.SequenceK
import arrow.core.SequenceKOf
import arrow.core.Tuple2
import arrow.core.extensions.eval.applicative.applicative
import arrow.core.extensions.sequence.foldable.firstOption
import arrow.core.extensions.sequence.foldable.foldLeft
import arrow.core.extensions.sequence.foldable.foldRight
import arrow.core.extensions.sequence.foldable.isEmpty
import arrow.core.extensions.sequence.monadFilter.filterMap
import arrow.core.extensions.sequencek.foldable.firstOption
import arrow.core.extensions.sequencek.monad.map
import arrow.core.extensions.sequencek.monad.monad
import arrow.core.fix
import arrow.core.k
import arrow.core.some
import arrow.core.toOption
import arrow.core.toT
import arrow.extension
import arrow.typeclasses.Align
import arrow.typeclasses.Alternative
import arrow.typeclasses.Applicative
import arrow.typeclasses.Apply
import arrow.typeclasses.Eq
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.FunctorFilter
import arrow.typeclasses.Hash
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadCombine
import arrow.typeclasses.MonadFilter
import arrow.typeclasses.MonadSyntax
import arrow.typeclasses.Monoid
import arrow.typeclasses.MonoidK
import arrow.typeclasses.Monoidal
import arrow.typeclasses.Semialign
import arrow.typeclasses.Semigroup
import arrow.typeclasses.SemigroupK
import arrow.typeclasses.Semigroupal
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse
import arrow.typeclasses.Unalign
import arrow.core.combineK as sequenceCombineK

@extension
interface SequenceKSemigroup<A> : Semigroup<SequenceK<A>> {
  override fun SequenceK<A>.combine(b: SequenceK<A>): SequenceK<A> = (this.sequence + b.sequence).k()
}

@extension
interface SequenceKSemigroupal : Semigroupal<ForSequenceK> {
  override fun <A, B> Kind<ForSequenceK, A>.product(fb: Kind<ForSequenceK, B>): Kind<ForSequenceK, Tuple2<A, B>> =
    fb.fix().ap(this.map { a: A -> { b: B -> Tuple2(a, b) } })
}

@extension
interface SequenceKMonoidal : Monoidal<ForSequenceK>, SequenceKSemigroupal {
  override fun <A> identity(): Kind<ForSequenceK, A> = SequenceK.empty()
}

@extension
interface SequenceKMonoid<A> : Monoid<SequenceK<A>>, SequenceKSemigroup<A> {
  override fun empty(): SequenceK<A> = emptySequence<A>().k()
}

@extension
interface SequenceKEq<A> : Eq<SequenceK<A>> {

  fun EQ(): Eq<A>

  /**
   * This only evaluates up to the first element that differs or to the first element at the index where the other
   *  sequence is empty
   */
  override fun SequenceK<A>.eqv(b: SequenceK<A>): Boolean = object : SequenceKSemialign {}.run {
    alignWith(this@eqv, b) { ior ->
      ior.fold({ false }, { false }, { l, r -> EQ().run { l.eqv(r) } })
    }.firstOption { it.not() }.isEmpty()
  }
}

@extension
interface SequenceKShow<A> : Show<SequenceK<A>> {
  override fun SequenceK<A>.show(): String =
    toString()
}

@extension
interface SequenceKFunctor : Functor<ForSequenceK> {
  override fun <A, B> Kind<ForSequenceK, A>.map(f: (A) -> B): SequenceK<B> =
    fix().map(f)
}

@extension
interface SequenceKApply : Apply<ForSequenceK> {
  override fun <A, B> Kind<ForSequenceK, A>.ap(ff: Kind<ForSequenceK, (A) -> B>): SequenceK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForSequenceK, A>.map(f: (A) -> B): SequenceK<B> =
    fix().map(f)

  override fun <A, B, Z> Kind<ForSequenceK, A>.map2(fb: Kind<ForSequenceK, B>, f: (Tuple2<A, B>) -> Z): SequenceK<Z> =
    fix().map2(fb, f)
}

@extension
interface SequenceKApplicative : Applicative<ForSequenceK> {
  override fun <A, B> Kind<ForSequenceK, A>.ap(ff: Kind<ForSequenceK, (A) -> B>): SequenceK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForSequenceK, A>.map(f: (A) -> B): SequenceK<B> =
    fix().map(f)

  override fun <A, B, Z> Kind<ForSequenceK, A>.map2(fb: Kind<ForSequenceK, B>, f: (Tuple2<A, B>) -> Z): SequenceK<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): SequenceK<A> =
    SequenceK.just(a)
}

@extension
interface SequenceKMonad : Monad<ForSequenceK> {
  override fun <A, B> Kind<ForSequenceK, A>.ap(ff: Kind<ForSequenceK, (A) -> B>): SequenceK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForSequenceK, A>.flatMap(f: (A) -> Kind<ForSequenceK, B>): SequenceK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, SequenceKOf<Either<A, B>>>): SequenceK<B> =
    SequenceK.tailRecM(a, f)

  override fun <A, B> Kind<ForSequenceK, A>.map(f: (A) -> B): SequenceK<B> =
    fix().map(f)

  override fun <A, B, Z> Kind<ForSequenceK, A>.map2(fb: Kind<ForSequenceK, B>, f: (Tuple2<A, B>) -> Z): SequenceK<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): SequenceK<A> =
    SequenceK.just(a)
}

@extension
interface SequenceKFoldable : Foldable<ForSequenceK> {
  override fun <A, B> Kind<ForSequenceK, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ForSequenceK, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)

  // overrides for laziness
  override fun <A, B> Kind<ForSequenceK, A>.reduceLeftToOption(f: (A) -> B, g: (B, A) -> B): Option<B> =
    fix().firstOption().map { fix().drop(1).foldLeft(f(it), g) }

  override fun <A, B> Kind<ForSequenceK, A>.reduceRightToOption(f: (A) -> B, g: (A, Eval<B>) -> Eval<B>): Eval<Option<B>> =
    fix().firstOption().traverse(Eval.applicative()) { fix().drop(1).foldRight(Eval.now(f(it)), g) }.fix()

  override fun <A> Kind<ForSequenceK, A>.get(idx: Long): Option<A> =
    if (idx < 0) None
    else fix().drop(idx.toInt()).firstOption()

  override fun <A> Kind<ForSequenceK, A>.firstOption(): Option<A> = fix().firstOrNull().toOption()

  override fun <A> Kind<ForSequenceK, A>.firstOption(predicate: (A) -> Boolean): Option<A> =
    fix().firstOrNull(predicate).toOption()
}

@extension
interface SequenceKTraverse : Traverse<ForSequenceK>, SequenceKFoldable {
  override fun <G, A, B> Kind<ForSequenceK, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, SequenceK<B>> =
    fix().traverse(AP, f)
}

@extension
interface SequenceKSemigroupK : SemigroupK<ForSequenceK> {
  override fun <A> Kind<ForSequenceK, A>.combineK(y: Kind<ForSequenceK, A>): SequenceK<A> =
    fix().sequenceCombineK(y)
}

@extension
interface SequenceKMonoidK : MonoidK<ForSequenceK> {
  override fun <A> empty(): SequenceK<A> =
    SequenceK.empty()

  override fun <A> Kind<ForSequenceK, A>.combineK(y: Kind<ForSequenceK, A>): SequenceK<A> =
    fix().sequenceCombineK(y)
}

@extension
interface SequenceKHash<A> : Hash<SequenceK<A>>, SequenceKEq<A> {
  fun HA(): Hash<A>

  override fun EQ(): Eq<A> = HA()

  override fun SequenceK<A>.hash(): Int = foldLeft(1) { hash, a ->
    31 * hash + HA().run { a.hash() }
  }
}

@extension
interface SequenceKFunctorFilter : FunctorFilter<ForSequenceK>, SequenceKFunctor {
  override fun <A, B> Kind<ForSequenceK, A>.filterMap(f: (A) -> Option<B>): SequenceK<B> =
    fix().filterMap(f)
}

@extension
interface SequenceKMonadFilter : MonadFilter<ForSequenceK> {
  override fun <A> empty(): SequenceK<A> =
    SequenceK.empty()

  override fun <A, B> Kind<ForSequenceK, A>.filterMap(f: (A) -> Option<B>): SequenceK<B> =
    fix().filterMap(f)

  override fun <A, B> Kind<ForSequenceK, A>.ap(ff: Kind<ForSequenceK, (A) -> B>): SequenceK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForSequenceK, A>.flatMap(f: (A) -> Kind<ForSequenceK, B>): SequenceK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, SequenceKOf<Either<A, B>>>): SequenceK<B> =
    SequenceK.tailRecM(a, f)

  override fun <A, B> Kind<ForSequenceK, A>.map(f: (A) -> B): SequenceK<B> =
    fix().map(f)

  override fun <A, B, Z> Kind<ForSequenceK, A>.map2(fb: Kind<ForSequenceK, B>, f: (Tuple2<A, B>) -> Z): SequenceK<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): SequenceK<A> =
    SequenceK.just(a)
}

@extension
interface SequenceKMonadCombine : MonadCombine<ForSequenceK>, SequenceKAlternative {
  override fun <A> empty(): SequenceK<A> =
    SequenceK.empty()

  override fun <A, B> Kind<ForSequenceK, A>.filterMap(f: (A) -> Option<B>): SequenceK<B> =
    fix().filterMap(f)

  override fun <A, B> Kind<ForSequenceK, A>.ap(ff: Kind<ForSequenceK, (A) -> B>): SequenceK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForSequenceK, A>.flatMap(f: (A) -> Kind<ForSequenceK, B>): SequenceK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, SequenceKOf<Either<A, B>>>): SequenceK<B> =
    SequenceK.tailRecM(a, f)

  override fun <A, B> Kind<ForSequenceK, A>.map(f: (A) -> B): SequenceK<B> =
    fix().map(f)

  override fun <A, B, Z> Kind<ForSequenceK, A>.map2(fb: Kind<ForSequenceK, B>, f: (Tuple2<A, B>) -> Z): SequenceK<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): SequenceK<A> =
    SequenceK.just(a)
}

fun <A> SequenceK.Companion.fx(c: suspend MonadSyntax<ForSequenceK>.() -> A): SequenceK<A> =
  SequenceK.monad().fx.monad(c).fix()

@extension
interface SequenceKAlternative : Alternative<ForSequenceK>, SequenceKApplicative {
  override fun <A> empty(): Kind<ForSequenceK, A> = emptySequence<A>().k()
  override fun <A> Kind<ForSequenceK, A>.orElse(b: Kind<ForSequenceK, A>): Kind<ForSequenceK, A> =
    (this.fix() + b.fix()).k()

  override fun <A> Kind<ForSequenceK, A>.some(): SequenceK<SequenceK<A>> =
    if (this.fix().isEmpty()) SequenceK.empty()
    else map {
      Sequence {
        object : Iterator<A> {
          override fun hasNext(): Boolean = true

          override fun next(): A = it
        }
      }.k()
    }.k()

  override fun <A> Kind<ForSequenceK, A>.many(): SequenceK<SequenceK<A>> =
    if (this.fix().isEmpty()) sequenceOf(emptySequence<A>().k()).k()
    else map {
      Sequence {
        object : Iterator<A> {
          override fun hasNext(): Boolean = true

          override fun next(): A = it
        }
      }.k()
    }.k()
}

@extension
interface SequenceKSemialign : Semialign<ForSequenceK>, SequenceKFunctor {
  override fun <A, B> align(a: Kind<ForSequenceK, A>, b: Kind<ForSequenceK, B>): Kind<ForSequenceK, Ior<A, B>> =
    object : Sequence<Ior<A, B>> {
      override fun iterator(): Iterator<Ior<A, B>> = object : Iterator<Ior<A, B>> {

        val leftIterator = a.fix().iterator()
        val rightIterator = b.fix().iterator()

        override fun hasNext(): Boolean = leftIterator.hasNext() || rightIterator.hasNext()

        fun <X> Iterator<X>.tryNext(): Option<X> = if (hasNext()) next().some() else Option.empty()

        override fun next(): Ior<A, B> =
          Ior.fromOptions(leftIterator.tryNext(), rightIterator.tryNext()).orNull()!!
      }
    }.k()
}

@extension
interface SequenceKAlign : Align<ForSequenceK>, SequenceKSemialign {
  override fun <A> empty(): Kind<ForSequenceK, A> = emptySequence<A>().k()
}

@extension
interface SequenceKUnalign : Unalign<ForSequenceK>, SequenceKSemialign {
  override fun <A, B> unalign(ior: Kind<ForSequenceK, Ior<A, B>>): Tuple2<Kind<ForSequenceK, A>, Kind<ForSequenceK, B>> =
    ior.fix().let { seq ->
      val ls = seq.sequence.filterMap { it.toLeftOption() }.k()
      val rs = seq.sequence.filterMap { it.toOption() }.k()

      ls toT rs
    }
}
