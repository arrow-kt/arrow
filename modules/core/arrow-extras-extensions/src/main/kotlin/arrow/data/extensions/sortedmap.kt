package arrow.data.extensions

import arrow.Kind
import arrow.core.Eval
import arrow.data.*

import arrow.typeclasses.*

interface SortedMapKFunctor<A : Comparable<A>> : Functor<SortedMapKPartialOf<A>> {
  override fun <B, C> SortedMapKOf<A, B>.map(f: (B) -> C): SortedMapK<A, C> =
    fix().map(f)
}

fun <A : Comparable<A>> SortedMapK.Companion.functor(): SortedMapKFunctor<A> =
  object : SortedMapKFunctor<A> {}

interface SortedMapKFoldable<A : Comparable<A>> : Foldable<SortedMapKPartialOf<A>> {
  override fun <B, C> SortedMapKOf<A, B>.foldLeft(b: C, f: (C, B) -> C): C =
    fix().foldLeft(b, f)

  override fun <B, C> SortedMapKOf<A, B>.foldRight(lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fix().foldRight(lb, f)
}

fun <A : Comparable<A>> SortedMapK.Companion.foldable(): SortedMapKFoldable<A> =
  object : SortedMapKFoldable<A> {}

interface SortedMapKTraverse<A : Comparable<A>> : Traverse<SortedMapKPartialOf<A>>, SortedMapKFoldable<A> {
  override fun <G, B, C> SortedMapKOf<A, B>.traverse(AP: Applicative<G>, f: (B) -> Kind<G, C>): Kind<G, Kind<SortedMapKPartialOf<A>, C>> =
    fix().traverse(AP, f)
}

fun <A : Comparable<A>> SortedMapK.Companion.traverse(): SortedMapKTraverse<A> =
  object : SortedMapKTraverse<A> {}

interface SortedMapKSemigroup<A : Comparable<A>, B> : Semigroup<SortedMapK<A, B>> {
  fun SG(): Semigroup<B>

  override fun SortedMapK<A, B>.combine(b: SortedMapK<A, B>): SortedMapK<A, B> =
    if (this.fix().size < b.fix().size) this.fix().foldLeft<B>(b.fix()) { my, (k, b) ->
      my.updated(k, SG().run { b.maybeCombine(my[k]) })
    }
    else b.fix().foldLeft<B>(this.fix()) { my: SortedMapK<A, B>, (k, a) -> my.updated(k, SG().run { a.maybeCombine(my[k]) }) }
}

fun <A : Comparable<A>, B> SortedMapK.Companion.semigroup(SB: Semigroup<B>): SortedMapKSemigroup<A, B> =
  object : SortedMapKSemigroup<A, B> {
    override fun SG(): Semigroup<B> = SB
  }

interface SortedMapKMonoid<A : Comparable<A>, B> : Monoid<SortedMapK<A, B>>, SortedMapKSemigroup<A, B> {
  override fun empty(): SortedMapK<A, B> = sortedMapOf<A, B>().k()
}

fun <A : Comparable<A>, B> SortedMapK.Companion.monoid(SB: Semigroup<B>): SortedMapKMonoid<A, B> =
  object : SortedMapKMonoid<A, B> {
    override fun SG(): Semigroup<B> = SB
  }

interface SortedMapKShow<A : Comparable<A>, B> : Show<SortedMapKOf<A, B>> {
  override fun SortedMapKOf<A, B>.show(): String =
    toString()
}

fun <A : Comparable<A>, B> SortedMapK.Companion.show(): SortedMapKShow<A, B> =
  object : SortedMapKShow<A, B> {}
