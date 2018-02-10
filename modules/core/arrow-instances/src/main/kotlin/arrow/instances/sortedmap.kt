package arrow.instances

import arrow.*
import arrow.core.*
import arrow.data.*
import arrow.typeclasses.*

@instance(SortedMapK::class)
interface SortedMapKFunctorInstance<A : Comparable<A>> : Functor<SortedMapKPartialOf<A>> {
    override fun <B, C> map(fb: Kind<SortedMapKPartialOf<A>, B>, f: (B) -> C): SortedMapK<A, C> =
            fb.fix().map(f)
}

@instance(SortedMapK::class)
interface SortedMapKFoldableInstance<A : Comparable<A>> : Foldable<SortedMapKPartialOf<A>> {
    override fun <B, C> foldLeft(fb: Kind<SortedMapKPartialOf<A>, B>, c: C, f: (C, B) -> C): C =
            fb.fix().foldLeft(c, f)

    override fun <B, C> foldRight(fb: Kind<SortedMapKPartialOf<A>, B>, lc: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> =
            fb.fix().foldRight(lc, f)
}

@instance(SortedMapK::class)
interface SortedMapKTraverseInstance<A : Comparable<A>> : SortedMapKFoldableInstance<A>, Traverse<SortedMapKPartialOf<A>> {
    override fun <G, B, C> traverse(fb: Kind<SortedMapKPartialOf<A>, B>, f: (B) -> Kind<G, C>, GA: Applicative<G>): Kind<G, Kind<SortedMapKPartialOf<A>, C>> =
            fb.fix().traverse(f, GA)
}

@instance(SortedMapK::class)
interface SortedMapKSemigroupInstance<A : Comparable<A>, B> : Semigroup<SortedMapKOf<A, B>> {
    fun SG(): Semigroup<B>

    override fun combine(a: SortedMapKOf<A, B>, b: SortedMapKOf<A, B>): SortedMapKOf<A, B> =
            if (a.fix().size < b.fix().size) a.fix().foldLeft<B>(b.fix(), { my, (k, b) ->
                my.updated(k, SG().maybeCombine(b, my[k]))
            })
            else b.fix().foldLeft<B>(a.fix(), { my: SortedMapK<A, B>, (k, a) -> my.updated(k, SG().maybeCombine(a, my[k])) })
}

@instance(SortedMapK::class)
interface SortedMapKMonoidInstance<A : Comparable<A>, B> : SortedMapKSemigroupInstance<A, B>, Monoid<SortedMapKOf<A, B>> {
    override fun empty(): SortedMapK<A, B> = sortedMapOf<A, B>().k()
}

@instance(SortedMapK::class)
interface SortedMapKShowInstance<A : Comparable<A>, B> : Show<SortedMapKOf<A, B>> {
    override fun show(a: SortedMapKOf<A, B>): String =
            a.toString()
}
