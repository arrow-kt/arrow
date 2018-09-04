package arrow.instances

import arrow.Kind
import arrow.core.*
import arrow.data.*
import arrow.instance
import arrow.typeclasses.*
import arrow.data.combineK as listCombineK
import kotlin.collections.plus as listPlus

@instance(ListK::class)
interface ListKSemigroupInstance<A> : Semigroup<ListK<A>> {
  override fun ListK<A>.combine(b: ListK<A>): ListK<A> =
    (this.listPlus(b)).k()
}

@instance(ListK::class)
interface ListKMonoidInstance<A> : ListKSemigroupInstance<A>, Monoid<ListK<A>> {
  override fun empty(): ListK<A> =
    emptyList<A>().k()
}

@instance(ListK::class)
interface ListKEqInstance<A> : Eq<ListKOf<A>> {

  fun EQ(): Eq<A>

  override fun ListKOf<A>.eqv(b: ListKOf<A>): Boolean =
    fix().zip(b.fix()) { aa, bb -> EQ().run { aa.eqv(bb) } }.fold(true) { acc, bool ->
      acc && bool
    }
}

@instance(ListK::class)
interface ListKShowInstance<A> : Show<ListKOf<A>> {
  override fun ListKOf<A>.show(): String =
    toString()
}

@instance(ListK::class)
interface ListKFunctorInstance : Functor<ForListK> {
  override fun <A, B> Kind<ForListK, A>.map(f: (A) -> B): ListK<B> =
    fix().map(f)
}

@instance(ListK::class)
interface ListKApplicativeInstance : Applicative<ForListK> {
  override fun <A, B> Kind<ForListK, A>.apPipe(ff: Kind<ForListK, (A) -> B>): ListK<B> =
    fix().apPipe(ff)

  override fun <A, B> Kind<ForListK, A>.map(f: (A) -> B): ListK<B> =
    fix().map(f)

  override fun <A, B, Z> Kind<ForListK, A>.map2(fb: Kind<ForListK, B>, f: (Tuple2<A, B>) -> Z): ListK<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): ListK<A> =
    ListK.just(a)
}

@instance(ListK::class)
interface ListKMonadInstance : Monad<ForListK> {
  override fun <A, B> Kind<ForListK, A>.apPipe(ff: Kind<ForListK, (A) -> B>): ListK<B> =
    fix().apPipe(ff)

  override fun <A, B> Kind<ForListK, A>.flatMap(f: (A) -> Kind<ForListK, B>): ListK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, ListKOf<Either<A, B>>>): ListK<B> =
    ListK.tailRecM(a, f)

  override fun <A, B> Kind<ForListK, A>.map(f: (A) -> B): ListK<B> =
    fix().map(f)

  override fun <A, B, Z> Kind<ForListK, A>.map2(fb: Kind<ForListK, B>, f: (Tuple2<A, B>) -> Z): ListK<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): ListK<A> =
    ListK.just(a)
}

@instance(ListK::class)
interface ListKFoldableInstance : Foldable<ForListK> {
  override fun <A, B> Kind<ForListK, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ForListK, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)

  override fun <A> Kind<ForListK, A>.isEmpty(): kotlin.Boolean =
    fix().isEmpty()
}

@instance(ListK::class)
interface ListKTraverseInstance : Traverse<ForListK> {
  override fun <A, B> Kind<ForListK, A>.map(f: (A) -> B): ListK<B> =
    fix().map(f)

  override fun <G, A, B> Kind<ForListK, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, ListK<B>> =
    fix().traverse(AP, f)

  override fun <A, B> Kind<ForListK, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ForListK, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)

  override fun <A> Kind<ForListK, A>.isEmpty(): Boolean =
    fix().isEmpty()
}

@instance(ListK::class)
interface ListKSemigroupKInstance : SemigroupK<ForListK> {
  override fun <A> Kind<ForListK, A>.combineK(y: Kind<ForListK, A>): ListK<A> =
    fix().listCombineK(y)
}

@instance(ListK::class)
interface ListKMonoidKInstance : MonoidK<ForListK> {
  override fun <A> empty(): ListK<A> =
    ListK.empty()

  override fun <A> Kind<ForListK, A>.combineK(y: Kind<ForListK, A>): ListK<A> =
    fix().listCombineK(y)
}

object ListKContext : ListKMonadInstance, ListKTraverseInstance, ListKMonoidKInstance {
  override fun <A, B> Kind<ForListK, A>.map(f: (A) -> B): ListK<B> =
    fix().map(f)
}

infix fun <A> ForListK.Companion.extensions(f: ListKContext.() -> A): A =
  f(ListKContext)
