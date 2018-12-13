package arrow.instances

import arrow.Kind
import arrow.core.Eval
import arrow.data.*
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.typeclasses.*

interface SortedMapKFunctorInstance<A : Comparable<A>> : Functor<SortedMapKPartialOf<A>> {
  override fun <B, C> SortedMapKOf<A, B>.map(f: (B) -> C): SortedMapK<A, C> =
    fix().map(f)
}

fun <A : Comparable<A>> SortedMapK.Companion.functor(): SortedMapKFunctorInstance<A> =
  object : arrow.instances.SortedMapKFunctorInstance<A> {}

interface SortedMapKFoldableInstance<A : Comparable<A>> : Foldable<SortedMapKPartialOf<A>> {
  override fun <B, C> SortedMapKOf<A, B>.foldLeft(b: C, f: (C, B) -> C): C =
    fix().foldLeft(b, f)

  override fun <B, C> SortedMapKOf<A, B>.foldRight(lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fix().foldRight(lb, f)
}

fun <A : Comparable<A>> SortedMapK.Companion.foldable(): SortedMapKFoldableInstance<A> =
  object : SortedMapKFoldableInstance<A> {}

interface SortedMapKTraverseInstance<A : Comparable<A>> : Traverse<SortedMapKPartialOf<A>>, SortedMapKFoldableInstance<A> {
  override fun <G, B, C> SortedMapKOf<A, B>.traverse(AP: Applicative<G>, f: (B) -> Kind<G, C>): Kind<G, Kind<SortedMapKPartialOf<A>, C>> =
    fix().traverse(AP, f)
}

fun <A : Comparable<A>> SortedMapK.Companion.traverse(): SortedMapKTraverseInstance<A> =
  object : SortedMapKTraverseInstance<A> {}

interface SortedMapKSemigroupInstance<A : Comparable<A>, B> : Semigroup<SortedMapKOf<A, B>> {
  fun SG(): Semigroup<B>

  override fun SortedMapKOf<A, B>.combine(b: SortedMapKOf<A, B>): SortedMapKOf<A, B> =
    if (this.fix().size < b.fix().size) this.fix().foldLeft<B>(b.fix()) { my, (k, b) ->
      my.updated(k, SG().run { b.maybeCombine(my[k]) })
    }
    else b.fix().foldLeft<B>(this.fix()) { my: SortedMapK<A, B>, (k, a) -> my.updated(k, SG().run { a.maybeCombine(my[k]) }) }
}

fun <A : Comparable<A>, B> SortedMapK.Companion.semigroup(SB: Semigroup<B>): SortedMapKSemigroupInstance<A, B> =
  object : SortedMapKSemigroupInstance<A, B> {
    override fun SG(): Semigroup<B> = SB
  }

interface SortedMapKMonoidInstance<A : Comparable<A>, B> : Monoid<SortedMapKOf<A, B>>, SortedMapKSemigroupInstance<A, B> {
  override fun empty(): SortedMapK<A, B> = sortedMapOf<A, B>().k()
}

fun <A : Comparable<A>, B> SortedMapK.Companion.monoid(SB: Semigroup<B>): SortedMapKMonoidInstance<A, B> =
  object : SortedMapKMonoidInstance<A, B> {
    override fun SG(): Semigroup<B> = SB
  }

interface SortedMapKShowInstance<A : Comparable<A>, B> : Show<SortedMapKOf<A, B>> {
  override fun SortedMapKOf<A, B>.show(): String =
    toString()
}

fun <A : Comparable<A>, B> SortedMapK.Companion.show(): SortedMapKShowInstance<A, B> =
  object : SortedMapKShowInstance<A, B> {}

class SortedMapKContext<K : Comparable<K>> : SortedMapKTraverseInstance<K>

class SortedMapKContextPartiallyApplied<K : Comparable<K>> {
  @Deprecated(ExtensionsDSLDeprecated)
  infix fun <A> extensions(f: SortedMapKContext<K>.() -> A): A =
    f(SortedMapKContext())
}

fun <K : Comparable<K>> ForSortedMapK(): SortedMapKContextPartiallyApplied<K> =
  SortedMapKContextPartiallyApplied()