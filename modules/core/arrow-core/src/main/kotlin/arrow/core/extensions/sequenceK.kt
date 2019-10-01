package arrow.core.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForSequenceK
import arrow.core.Option
import arrow.core.SequenceK
import arrow.core.SequenceKOf
import arrow.core.Tuple2
import arrow.core.extensions.sequencek.monad.map
import arrow.core.extensions.sequencek.monad.monad
import arrow.core.fix
import arrow.core.k
import arrow.extension
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
import arrow.typeclasses.Semigroup
import arrow.typeclasses.SemigroupK
import arrow.typeclasses.Semigroupal
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse
import arrow.core.combineK as sequenceCombineK

@extension
class SequenceKSemigroup<A> : Semigroup<SequenceK<A>> {
  override fun SequenceK<A>.combine(b: SequenceK<A>): SequenceK<A> = (this.sequence + b.sequence).k()
}

@extension
object SequenceKSemigroupal : Semigroupal<ForSequenceK> {
  override fun <A, B> SequenceKOf<A>.product(fb: SequenceKOf<B>): SequenceKOf<Tuple2<A, B>> =
    fb.fix().ap(map { a: A -> { b: B -> Tuple2(a, b) } })
}

@extension
object SequenceKMonoidal : Monoidal<ForSequenceK>, SequenceKSemigroupal {
  override fun <A> identity(): SequenceKOf<A> = SequenceK.empty()
}

@extension
class SequenceKMonoid<A> : Monoid<SequenceK<A>> {
  override fun SequenceK<A>.combine(b: SequenceK<A>): SequenceK<A> = (this.sequence + b.sequence).k()

  override fun empty(): SequenceK<A> = emptySequence<A>().k()
}

@extension
class SequenceKEq<A> : Eq<SequenceK<A>> {

  fun EQ(): Eq<A>

  override fun SequenceK<A>.eqv(b: SequenceK<A>): Boolean =
    zip(b) { aa, bb -> EQ().run { aa.eqv(bb) } }.fold(true) { acc, bool ->
      acc && bool
    }
}

@extension
class SequenceKShow<A> : Show<SequenceK<A>> {
  override fun SequenceK<A>.showed(): String =
    toString()
}

@extension
object SequenceKFunctor : Functor<ForSequenceK> {
  override fun <A, B> SequenceKOf<A>.map(f: (A) -> B): SequenceK<B> =
    fix().map(f)
}

@extension
object SequenceKApply : Apply<ForSequenceK> {
  override fun <A, B> SequenceKOf<A>.ap(ff: SequenceKOf<(A) -> B>): SequenceK<B> =
    fix().ap(ff)

  override fun <A, B> SequenceKOf<A>.map(f: (A) -> B): SequenceK<B> =
    fix().map(f)

  override fun <A, B, Z> SequenceKOf<A>.map2(fb: SequenceKOf<B>, f: (Tuple2<A, B>) -> Z): SequenceK<Z> =
    fix().map2(fb, f)
}

@extension
object SequenceKApplicative : Applicative<ForSequenceK> {
  override fun <A, B> SequenceKOf<A>.ap(ff: SequenceKOf<(A) -> B>): SequenceK<B> =
    fix().ap(ff)

  override fun <A, B> SequenceKOf<A>.map(f: (A) -> B): SequenceK<B> =
    fix().map(f)

  override fun <A, B, Z> SequenceKOf<A>.map2(fb: SequenceKOf<B>, f: (Tuple2<A, B>) -> Z): SequenceK<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): SequenceK<A> =
    SequenceK.just(a)
}

@extension
object SequenceKMonad : Monad<ForSequenceK> {
  override fun <A, B> SequenceKOf<A>.ap(ff: SequenceKOf<(A) -> B>): SequenceK<B> =
    fix().ap(ff)

  override fun <A, B> SequenceKOf<A>.flatMap(f: (A) -> SequenceKOf<B>): SequenceK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> SequenceKOf<Either<A, B>>): SequenceK<B> =
    SequenceK.tailRecM(a, f)

  override fun <A, B> SequenceKOf<A>.map(f: (A) -> B): SequenceK<B> =
    fix().map(f)

  override fun <A, B, Z> SequenceKOf<A>.map2(fb: SequenceKOf<B>, f: (Tuple2<A, B>) -> Z): SequenceK<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): SequenceK<A> =
    SequenceK.just(a)
}

@extension
object SequenceKFoldable : Foldable<ForSequenceK> {
  override fun <A, B> SequenceKOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> SequenceKOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)
}

@extension
object SequenceKTraverse : Traverse<ForSequenceK> {
  override fun <A, B> SequenceKOf<A>.map(f: (A) -> B): SequenceK<B> =
    fix().map(f)

  override fun <G, A, B> SequenceKOf<A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, SequenceK<B>> =
    fix().traverse(AP, f)

  override fun <A, B> SequenceKOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> SequenceKOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)
}

@extension
object SequenceKSemigroupK : SemigroupK<ForSequenceK> {
  override fun <A> SequenceKOf<A>.combineK(y: SequenceKOf<A>): SequenceK<A> =
    fix().sequenceCombineK(y)
}

@extension
object SequenceKMonoidK : MonoidK<ForSequenceK> {
  override fun <A> empty(): SequenceK<A> =
    SequenceK.empty()

  override fun <A> SequenceKOf<A>.combineK(y: SequenceKOf<A>): SequenceK<A> =
    fix().sequenceCombineK(y)
}

@extension
class SequenceKHash<A> : Hash<SequenceK<A>>, SequenceKEq<A> {
  fun HA(): Hash<A>

  override fun EQ(): Eq<A> = HA()

  override fun SequenceK<A>.hashed(): Int = foldLeft(1) { hash, a ->
    31 * hash + HA().run { a.hashed() }
  }
}

@extension
class SequenceKFunctorFilter : FunctorFilter<ForSequenceK> {
  override fun <A, B> SequenceKOf<A>.filterMap(f: (A) -> Option<B>): SequenceK<B> =
    fix().filterMap(f)

  override fun <A, B> SequenceKOf<A>.map(f: (A) -> B): SequenceK<B> =
    fix().map(f)
}

@extension
class SequenceKMonadFilter : MonadFilter<ForSequenceK> {
  override fun <A> empty(): SequenceK<A> =
    SequenceK.empty()

  override fun <A, B> SequenceKOf<A>.filterMap(f: (A) -> Option<B>): SequenceK<B> =
    fix().filterMap(f)

  override fun <A, B> SequenceKOf<A>.ap(ff: SequenceKOf<(A) -> B>): SequenceK<B> =
    fix().ap(ff)

  override fun <A, B> SequenceKOf<A>.flatMap(f: (A) -> SequenceKOf<B>): SequenceK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> SequenceKOf<Either<A, B>>): SequenceK<B> =
    SequenceK.tailRecM(a, f)

  override fun <A, B> SequenceKOf<A>.map(f: (A) -> B): SequenceK<B> =
    fix().map(f)

  override fun <A, B, Z> SequenceKOf<A>.map2(fb: SequenceKOf<B>, f: (Tuple2<A, B>) -> Z): SequenceK<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): SequenceK<A> =
    SequenceK.just(a)
}

@extension
class SequenceKMonadCombine : MonadCombine<ForSequenceK> {
  override fun <A> empty(): SequenceK<A> =
    SequenceK.empty()

  override fun <A, B> SequenceKOf<A>.filterMap(f: (A) -> Option<B>): SequenceK<B> =
    fix().filterMap(f)

  override fun <A, B> SequenceKOf<A>.ap(ff: SequenceKOf<(A) -> B>): SequenceK<B> =
    fix().ap(ff)

  override fun <A, B> SequenceKOf<A>.flatMap(f: (A) -> SequenceKOf<B>): SequenceK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> SequenceKOf<Either<A, B>>): SequenceK<B> =
    SequenceK.tailRecM(a, f)

  override fun <A, B> SequenceKOf<A>.map(f: (A) -> B): SequenceK<B> =
    fix().map(f)

  override fun <A, B, Z> SequenceKOf<A>.map2(fb: SequenceKOf<B>, f: (Tuple2<A, B>) -> Z): SequenceK<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): SequenceK<A> =
    SequenceK.just(a)

  override fun <A> SequenceKOf<A>.combineK(y: SequenceKOf<A>): SequenceK<A> =
    fix().sequenceCombineK(y)
}

fun <A> SequenceK.Companion.fx(c: suspend MonadSyntax<ForSequenceK>.() -> A): SequenceK<A> =
  SequenceK.monad().fx.monad(c).fix()
