package arrow.core.extensions

import arrow.Kind
import arrow.core.EQ
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForSequenceK
import arrow.core.GT
import arrow.core.Ior
import arrow.core.LT
import arrow.core.ListK
import arrow.core.None
import arrow.core.Option
import arrow.core.Ordering
import arrow.core.SequenceK
import arrow.core.SequenceKOf
import arrow.core.Tuple2
import arrow.core.extensions.eval.applicative.applicative
import arrow.core.extensions.listk.crosswalk.crosswalk
import arrow.core.extensions.sequence.foldable.firstOption
import arrow.core.extensions.sequence.foldable.foldLeft
import arrow.core.extensions.sequence.foldable.foldRight
import arrow.core.extensions.sequence.foldable.isEmpty
import arrow.core.extensions.sequence.monadFilter.filterMap
import arrow.core.extensions.sequencek.align.align
import arrow.core.extensions.sequencek.eq.eq
import arrow.core.extensions.sequencek.foldable.firstOption
import arrow.core.extensions.sequencek.monad.map
import arrow.core.extensions.sequencek.monad.monad
import arrow.core.fix
import arrow.core.k
import arrow.core.some
import arrow.core.toT
import arrow.typeclasses.Align
import arrow.typeclasses.Alternative
import arrow.typeclasses.Applicative
import arrow.typeclasses.Apply
import arrow.typeclasses.Crosswalk
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.FunctorFilter
import arrow.typeclasses.Hash
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadCombine
import arrow.typeclasses.MonadFilter
import arrow.typeclasses.MonadLogic
import arrow.typeclasses.MonadPlus
import arrow.typeclasses.MonadSyntax
import arrow.typeclasses.Monoid
import arrow.typeclasses.MonoidK
import arrow.typeclasses.Monoidal
import arrow.typeclasses.Order
import arrow.typeclasses.OrderDeprecation
import arrow.typeclasses.Repeat
import arrow.typeclasses.Semialign
import arrow.typeclasses.Semigroup
import arrow.typeclasses.SemigroupK
import arrow.typeclasses.Semigroupal
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse
import arrow.typeclasses.Unalign
import arrow.typeclasses.Unzip
import arrow.typeclasses.Zip
import arrow.typeclasses.hashWithSalt
import arrow.core.combineK as sequenceCombineK

@Deprecated(
  "Typeclass instance have been moved to the companion object of the typeclass.",
  ReplaceWith("Semigroup.sequence()", "arrow.core.sequence", "arrow.typeclasses.Semigroup"),
  DeprecationLevel.WARNING
)
interface SequenceKSemigroup<A> : Semigroup<SequenceK<A>> {
  override fun SequenceK<A>.combine(b: SequenceK<A>): SequenceK<A> = (this.sequence + b.sequence).k()
}

@Deprecated(
  message = "Semigroupal typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
interface SequenceKSemigroupal : Semigroupal<ForSequenceK> {
  override fun <A, B> Kind<ForSequenceK, A>.product(fb: Kind<ForSequenceK, B>): Kind<ForSequenceK, Tuple2<A, B>> =
    fb.fix().ap(this.map { a: A -> { b: B -> Tuple2(a, b) } })
}

@Deprecated(
  message = "Monoidal typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
interface SequenceKMonoidal : Monoidal<ForSequenceK>, SequenceKSemigroupal {
  override fun <A> identity(): Kind<ForSequenceK, A> = SequenceK.empty()
}

@Deprecated(
  "Typeclass instance have been moved to the companion object of the typeclass.",
  ReplaceWith("Monoid.sequence()", "arrow.core.sequence", "arrow.typeclasses.Monoid"),
  DeprecationLevel.WARNING
)
interface SequenceKMonoid<A> : Monoid<SequenceK<A>>, SequenceKSemigroup<A> {
  override fun empty(): SequenceK<A> = emptySequence<A>().k()
}

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

interface SequenceKShow<A> : Show<SequenceK<A>> {
  fun SA(): Show<A>
  override fun SequenceK<A>.show(): String = show(SA())
}

@Deprecated(
  message = "Functor typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
interface SequenceKFunctor : Functor<ForSequenceK> {
  override fun <A, B> Kind<ForSequenceK, A>.map(f: (A) -> B): SequenceK<B> =
    fix().map(f)
}

@Deprecated(
  message = "Apply typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
interface SequenceKApply : Apply<ForSequenceK> {
  override fun <A, B> Kind<ForSequenceK, A>.ap(ff: Kind<ForSequenceK, (A) -> B>): SequenceK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForSequenceK, A>.map(f: (A) -> B): SequenceK<B> =
    fix().map(f)

  override fun <A, B, Z> Kind<ForSequenceK, A>.map2(fb: Kind<ForSequenceK, B>, f: (Tuple2<A, B>) -> Z): SequenceK<Z> =
    fix().map2(fb, f)
}

@Deprecated(
  message = "Applicative typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
interface SequenceKApplicative : Applicative<ForSequenceK>, SequenceKApply {
  override fun <A, B> Kind<ForSequenceK, A>.ap(ff: Kind<ForSequenceK, (A) -> B>): SequenceK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForSequenceK, A>.map(f: (A) -> B): SequenceK<B> =
    fix().map(f)

  override fun <A, B, Z> Kind<ForSequenceK, A>.map2(fb: Kind<ForSequenceK, B>, f: (Tuple2<A, B>) -> Z): SequenceK<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): SequenceK<A> =
    SequenceK.just(a)
}

@Deprecated(
  message = "Monad typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
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

@Deprecated(
  message = "Foldable typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
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
}

@Deprecated(
  message = "Traverse typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
interface SequenceKTraverse : Traverse<ForSequenceK>, SequenceKFoldable {
  override fun <G, A, B> Kind<ForSequenceK, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, SequenceK<B>> =
    fix().traverse(AP, f)
}

@Deprecated(
  message = "SemigroupK typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
interface SequenceKSemigroupK : SemigroupK<ForSequenceK> {
  override fun <A> Kind<ForSequenceK, A>.combineK(y: Kind<ForSequenceK, A>): SequenceK<A> =
    fix().sequenceCombineK(y)
}

@Deprecated(
  message = "MonoidK typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
interface SequenceKMonoidK : MonoidK<ForSequenceK> {
  override fun <A> empty(): SequenceK<A> =
    SequenceK.empty()

  override fun <A> Kind<ForSequenceK, A>.combineK(y: Kind<ForSequenceK, A>): SequenceK<A> =
    fix().sequenceCombineK(y)
}

interface SequenceKHash<A> : Hash<SequenceK<A>> {
  fun HA(): Hash<A>

  // Not using ListK.hash here to avoid extra loop converting it
  override fun SequenceK<A>.hashWithSalt(salt: Int): Int =
    HA().run { foldLeft(salt toT 0) { (hash, sz), v -> v.hashWithSalt(hash) toT sz + 1 } }
      .let { (hash, size) -> hash.hashWithSalt(size) }
}

@Deprecated(OrderDeprecation)
interface SequenceKOrder<A> : Order<SequenceK<A>> {
  fun OA(): Order<A>
  override fun SequenceK<A>.compare(b: SequenceK<A>): Ordering =
    SequenceK.align().alignWith(this, b) { ior -> ior.fold({ GT }, { LT }, { a1, a2 -> OA().run { a1.compare(a2) } }) }
      // we cannot use fold(Ordering.monoid()) because that won't short circuit
      .fix().foldRight<Ordering>(Eval.now(EQ)) { v, acc ->
        if (v == EQ) acc // delegate to the remaining sequence
        else Eval.now(v) // We can short circuit here if an ordering has been found
      }.value()
}

@Deprecated(
  message = "FunctorFilter typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
interface SequenceKFunctorFilter : FunctorFilter<ForSequenceK>, SequenceKFunctor {
  override fun <A, B> Kind<ForSequenceK, A>.filterMap(f: (A) -> Option<B>): SequenceK<B> =
    fix().filterMap(f)
}

@Deprecated(
  message = "MonadFilter typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
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

@Deprecated(
  message = "MonadCombine typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
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

@Deprecated(
  message = "Alternative typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
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

@Deprecated(
  message = "Semialign typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
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

@Deprecated(
  message = "Align typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
interface SequenceKAlign : Align<ForSequenceK>, SequenceKSemialign {
  override fun <A> empty(): Kind<ForSequenceK, A> = emptySequence<A>().k()
}

@Deprecated(
  message = "Unalign typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
interface SequenceKUnalign : Unalign<ForSequenceK>, SequenceKSemialign {
  override fun <A, B> unalign(ior: Kind<ForSequenceK, Ior<A, B>>): Tuple2<Kind<ForSequenceK, A>, Kind<ForSequenceK, B>> =
    ior.fix().let { seq ->
      val ls = seq.sequence.filterMap { it.toLeftOption() }.k()
      val rs = seq.sequence.filterMap { it.toOption() }.k()

      ls toT rs
    }
}

@Deprecated(
  message = "Zip typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
interface SequenceKZip : Zip<ForSequenceK>, SequenceKSemialign {
  override fun <A, B> Kind<ForSequenceK, A>.zip(other: Kind<ForSequenceK, B>): Kind<ForSequenceK, Tuple2<A, B>> =
    object : Sequence<Tuple2<A, B>> {
      override fun iterator(): Iterator<Tuple2<A, B>> = object : Iterator<Tuple2<A, B>> {

        val leftIterator = this@zip.fix().iterator()
        val rightIterator = other.fix().iterator()

        override fun hasNext(): Boolean =
          leftIterator.hasNext() && rightIterator.hasNext()

        override fun next(): Tuple2<A, B> = leftIterator.next() toT rightIterator.next()
      }
    }.k()
}

@Deprecated(
  message = "Repeat typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
interface SequenceKRepeat : Repeat<ForSequenceK>, SequenceKZip {
  override fun <A> repeat(a: A): Kind<ForSequenceK, A> =
    object : Sequence<A> {
      override fun iterator(): Iterator<A> = object : Iterator<A> {
        override fun hasNext(): Boolean = true

        override fun next(): A = a
      }
    }.k()
}

@Deprecated(
  message = "Unzip typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
interface SequenceKUnzip : Unzip<ForSequenceK>, SequenceKZip {
  override fun <A, B> Kind<ForSequenceK, Tuple2<A, B>>.unzip(): Tuple2<Kind<ForSequenceK, A>, Kind<ForSequenceK, B>> =
    this.fix().let { seq ->
      (seq.map { it.a }.k() toT seq.map { it.b }.k())
    }
}

@Deprecated(
  message = "Crosswalk typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
interface SequenceKCrosswalk : Crosswalk<ForSequenceK>, SequenceKFunctor, SequenceKFoldable {
  override fun <F, A, B> crosswalk(ALIGN: Align<F>, a: Kind<ForSequenceK, A>, fa: (A) -> Kind<F, B>): Kind<F, Kind<ForSequenceK, B>> =
    a.fix().sequence.toList().k().let { list ->
      ListK.crosswalk().run {
        crosswalk(ALIGN, list, fa)
      }
    }.let { list ->
      ALIGN.run {
        list.map { it.fix().asSequence().k() }
      }
    }
}

@Deprecated(
  message = "EqK typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
interface SequenceKEqK : EqK<ForSequenceK> {
  override fun <A> Kind<ForSequenceK, A>.eqK(other: Kind<ForSequenceK, A>, EQ: Eq<A>): Boolean =
    SequenceK.eq(EQ).run { this@eqK.fix().eqv(other.fix()) }
}

@Deprecated(
  message = "MonadPlus typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
interface SequenceKMonadPlus : MonadPlus<ForSequenceK>, SequenceKMonad, SequenceKAlternative {
  override fun <A, B> Kind<ForSequenceK, A>.ap(ff: Kind<ForSequenceK, (A) -> B>): SequenceK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForSequenceK, A>.map(f: (A) -> B): SequenceK<B> =
    fix().map(f)

  override fun <A, B, Z> Kind<ForSequenceK, A>.map2(fb: Kind<ForSequenceK, B>, f: (Tuple2<A, B>) -> Z): SequenceK<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): SequenceK<A> =
    SequenceK.just(a)
}

@Deprecated(
  message = "MonadLogic typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
interface SequenceKMonadLogic : MonadLogic<ForSequenceK>, SequenceKMonadPlus {
  override fun <A> Kind<ForSequenceK, A>.splitM(): Kind<ForSequenceK, Option<Tuple2<Kind<ForSequenceK, A>, A>>> =
    SequenceK.just(firstOption().map { a -> fix().sequence.drop(1).k() toT a })
}
