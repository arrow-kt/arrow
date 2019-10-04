package arrow.core.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForListK
import arrow.core.ListK
import arrow.core.ListKOf
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.listk.monad.monad
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
import arrow.core.combineK as listCombineK
import kotlin.collections.plus as listPlus

@extension
class ListKSemigroup<A> : Semigroup<ListK<A>> {
  override fun ListK<A>.combine(b: ListK<A>): ListK<A> =
    (this.listPlus(b)).k()
}

@extension
class ListKMonoid<A> : Monoid<ListK<A>>, ListKSemigroup<A> {
  override fun empty(): ListK<A> =
    emptyList<A>().k()
}

@extension
class ListKEq<A> : Eq<ListKOf<A>> {

  fun EQ(): Eq<A>

  override fun ListKOf<A>.eqv(b: ListKOf<A>): Boolean =
    if (fix().size == b.fix().size) fix().zip(b.fix()) { aa, bb ->
      EQ().run { aa.eqv(bb) }
    }.fold(true) { acc, bool ->
      acc && bool
    }
    else false
}

@extension
class ListKShow<A> : Show<ListKOf<A>> {
  override fun ListKOf<A>.showed(): String =
    toString()
}

@extension
class ListKFunctor : Functor<ForListK> {
  override fun <A, B> ListKOf<A>.map(f: (A) -> B): ListK<B> =
    fix().map(f)
}

@extension
class ListKApply : Apply<ForListK> {
  override fun <A, B> ListKOf<A>.ap(ff: ListKOf<(A) -> B>): ListK<B> =
    fix().ap(ff)

  override fun <A, B> ListKOf<A>.map(f: (A) -> B): ListK<B> =
    fix().map(f)

  override fun <A, B, Z> ListKOf<A>.map2(fb: ListKOf<B>, f: (Tuple2<A, B>) -> Z): ListK<Z> =
    fix().map2(fb, f)
}

@extension
class ListKApplicative : Applicative<ForListK> {
  override fun <A, B> ListKOf<A>.ap(ff: ListKOf<(A) -> B>): ListK<B> =
    fix().ap(ff)

  override fun <A, B> ListKOf<A>.map(f: (A) -> B): ListK<B> =
    fix().map(f)

  override fun <A, B, Z> ListKOf<A>.map2(fb: ListKOf<B>, f: (Tuple2<A, B>) -> Z): ListK<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): ListK<A> =
    ListK.just(a)
}

@extension
class ListKMonad : Monad<ForListK> {
  override fun <A, B> ListKOf<A>.ap(ff: ListKOf<(A) -> B>): ListK<B> =
    fix().ap(ff)

  override fun <A, B> ListKOf<A>.flatMap(f: (A) -> ListKOf<B>): ListK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> ListKOf<Either<A, B>>): ListK<B> =
    ListK.tailRecM(a, f)

  override fun <A, B> ListKOf<A>.map(f: (A) -> B): ListK<B> =
    fix().map(f)

  override fun <A, B, Z> ListKOf<A>.map2(fb: ListKOf<B>, f: (Tuple2<A, B>) -> Z): ListK<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): ListK<A> =
    ListK.just(a)
}

@extension
class ListKFoldable : Foldable<ForListK> {
  override fun <A, B> ListKOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> ListKOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)

  override fun <A> ListKOf<A>.isEmpty(): kotlin.Boolean =
    fix().isEmpty()
}

@extension
class ListKTraverse : Traverse<ForListK> {
  override fun <A, B> ListKOf<A>.map(f: (A) -> B): ListK<B> =
    fix().map(f)

  override fun <G, A, B> ListKOf<A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, ListK<B>> =
    fix().traverse(AP, f)

  override fun <A, B> ListKOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> ListKOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)

  override fun <A> ListKOf<A>.isEmpty(): Boolean =
    fix().isEmpty()
}

@extension
class ListKSemigroupK : SemigroupK<ForListK> {
  override fun <A> ListKOf<A>.combineK(y: ListKOf<A>): ListK<A> =
    fix().listCombineK(y)
}

@extension
class ListKSemigroupal : Semigroupal<ForListK> {
  override fun <A, B> ListKOf<A>.product(fb: ListKOf<B>): ListKOf<Tuple2<A, B>> =
    fb.fix().ap(fix().map { a: A -> { b: B -> Tuple2(a, b) } })
}

@extension
class ListKMonoidal : Monoidal<ForListK>, ListKSemigroupal {
  override fun <A> identity(): ListKOf<A> = ListK.empty()
}

@extension
class ListKMonoidK : MonoidK<ForListK> {
  override fun <A> empty(): ListK<A> =
    ListK.empty()

  override fun <A> ListKOf<A>.combineK(y: ListKOf<A>): ListK<A> =
    fix().listCombineK(y)
}

@extension
class ListKHash<A> : Hash<ListKOf<A>>, ListKEq<A> {

  fun HA(): Hash<A>

  override fun EQ(): Eq<A> = HA()

  override fun ListKOf<A>.hashed(): Int = fix().foldLeft(1) { hash, a ->
    31 * hash + HA().run { a.hashed() }
  }
}

@extension
class ListKFunctorFilter : FunctorFilter<ForListK> {
  override fun <A, B> ListKOf<A>.filterMap(f: (A) -> Option<B>): ListK<B> =
    fix().filterMap(f)

  override fun <A, B> ListKOf<A>.map(f: (A) -> B): ListK<B> =
    fix().map(f)
}

fun <A> ListK.Companion.fx(c: suspend MonadSyntax<ForListK>.() -> A): ListK<A> =
  ListK.monad().fx.monad(c).fix()

@extension
class ListKMonadCombine : MonadCombine<ForListK> {
  override fun <A> empty(): ListK<A> =
    ListK.empty()

  override fun <A, B> ListKOf<A>.filterMap(f: (A) -> Option<B>): ListK<B> =
    fix().filterMap(f)

  override fun <A, B> ListKOf<A>.ap(ff: ListKOf<(A) -> B>): ListK<B> =
    fix().ap(ff)

  override fun <A, B> ListKOf<A>.flatMap(f: (A) -> ListKOf<B>): ListK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> ListKOf<Either<A, B>>): ListK<B> =
    ListK.tailRecM(a, f)

  override fun <A, B> ListKOf<A>.map(f: (A) -> B): ListK<B> =
    fix().map(f)

  override fun <A, B, Z> ListKOf<A>.map2(fb: ListKOf<B>, f: (Tuple2<A, B>) -> Z): ListK<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): ListK<A> =
    ListK.just(a)

  override fun <A> ListKOf<A>.combineK(y: ListKOf<A>): ListK<A> =
    fix().listCombineK(y)
}

@extension
class ListKMonadFilter : MonadFilter<ForListK> {
  override fun <A> empty(): ListK<A> =
    ListK.empty()

  override fun <A, B> ListKOf<A>.filterMap(f: (A) -> Option<B>): ListK<B> =
    fix().filterMap(f)

  override fun <A, B> ListKOf<A>.ap(ff: ListKOf<(A) -> B>): ListK<B> =
    fix().ap(ff)

  override fun <A, B> ListKOf<A>.flatMap(f: (A) -> ListKOf<B>): ListK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> ListKOf<Either<A, B>>): ListK<B> =
    ListK.tailRecM(a, f)

  override fun <A, B> ListKOf<A>.map(f: (A) -> B): ListK<B> =
    fix().map(f)

  override fun <A, B, Z> ListKOf<A>.map2(fb: ListKOf<B>, f: (Tuple2<A, B>) -> Z): ListK<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): ListK<A> =
    ListK.just(a)
}
