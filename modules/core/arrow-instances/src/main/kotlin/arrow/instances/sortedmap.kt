package arrow.instances

import arrow.*
import arrow.core.*
import arrow.data.*
import arrow.typeclasses.*

@instance(SortedMapKW::class)
interface SortedMapKWFunctorInstance<A : Comparable<A>> : Functor<SortedMapKWKindPartial<A>> {
    override fun <B, C> map(fb: Kind<SortedMapKWKindPartial<A>, B>, f: (B) -> C): SortedMapKW<A, C> =
            fb.reify().map(f)
}

@instance(SortedMapKW::class)
interface SortedMapKWFoldableInstance<A : Comparable<A>> : Foldable<SortedMapKWKindPartial<A>> {
    override fun <B, C> foldLeft(fb: Kind<SortedMapKWKindPartial<A>, B>, c: C, f: (C, B) -> C): C =
            fb.reify().foldLeft(c, f)

    override fun <B, C> foldRight(fb: Kind<SortedMapKWKindPartial<A>, B>, lc: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> =
            fb.reify().foldRight(lc, f)
}

@instance(SortedMapKW::class)
interface SortedMapKWTraverseInstance<A : Comparable<A>> : SortedMapKWFoldableInstance<A>, Traverse<SortedMapKWKindPartial<A>> {
    override fun <G, B, C> traverse(fb: Kind<SortedMapKWKindPartial<A>, B>, f: (B) -> Kind<G, C>, GA: Applicative<G>): Kind<G, Kind<SortedMapKWKindPartial<A>, C>> =
            fb.reify().traverse(f, GA)
}

@instance(SortedMapKW::class)
interface SortedMapKWSemigroupInstance<A : Comparable<A>, B> : Semigroup<SortedMapKWKind<A, B>> {
    fun SG(): Semigroup<B>

    override fun combine(a: SortedMapKWKind<A, B>, b: SortedMapKWKind<A, B>): SortedMapKWKind<A, B> =
            if (a.reify().size < b.reify().size) a.reify().foldLeft<B>(b.reify(), { my, (k, b) ->
                my.updated(k, SG().maybeCombine(b, my[k]))
            })
            else b.reify().foldLeft<B>(a.reify(), { my: SortedMapKW<A, B>, (k, a) -> my.updated(k, SG().maybeCombine(a, my[k])) })
}

@instance(SortedMapKW::class)
interface SortedMapKWMonoidInstance<A : Comparable<A>, B> : SortedMapKWSemigroupInstance<A, B>, Monoid<SortedMapKWKind<A, B>> {
    override fun empty(): SortedMapKW<A, B> = sortedMapOf<A, B>().k()
}
