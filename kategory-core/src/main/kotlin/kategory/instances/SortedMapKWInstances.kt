package kategory

import kategory.*
import kategory.data.SortedMapKW
import kategory.data.k

@instance(SortedMapKW::class)
interface SortedMapKWFunctorInstance<K: Comparable<K>> : Functor<SortedMapKWKindPartial<K>> {
    override fun <A, B> map(fa: HK<SortedMapKWKindPartial<K>, A>, f: (A) -> B): SortedMapKW<K, B> =
            fa.ev().map(f)
}

@instance(SortedMapKW::class)
interface SortedMapKWFoldableInstance<K> : Foldable<SortedMapKWKindPartial<K>> {
    override fun <A, B> foldL(fa: HK<SortedMapKWKindPartial<K>, A>, b: B, f: (B, A) -> B): B =
            fa.ev().foldL(b, f)

    override fun <A, B> foldR(fa: HK<SortedMapKWKindPartial<K>, A>, lb: Eval<B>, f: (B, Eval<B>) -> Eval<B>): Eval<B> =
            fa.ev().foldR(lb, f)
}

@instance(SortedMapKW::class)
interface SortedMapKWTraverseInstance<K> : SortedMapKWFoldableInstance<K>, Traverse<SortedMapKWKindPartial<K>> {
    override fun <G, A, B> traverse(fa: HK<SortedMapKWKindPartial<K>, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, HK<SortedMapKWKindPartial<K>, B>> =
            fa.ev().traverse(f, GA)
}

@instance(SortedMapKW::class)
interface SortedMapKWSemigroupInstance<K, A> : Semigroup<SortedMapKWKind<K, A>> {
    fun SG(): Semigroup<A>

    override fun combine(a: SortedMapKWKind<K, A>, b: SortedMapKWKind<K, A>): SortedMapKW<K, A> =
            if (a.ev().size < b.ev().size) a.ev().foldLeft<A> (b.ev(), { my, (k, b) ->
                my.updated(k, SG().maybeCombine(b, my.get(k)))
            })
            else b.ev().foldLeft<A> (a.ev(), { my, (k, a) -> my.updated(k, SG().maybeCombine(a, my.get(k))) })
}

@instance(SortedMapKW::class)
interface SortedMapKWMonoidInstance<K: Comparable<K>, A> : SortedMapKWSemigroupInstance<K, A>, Monoid<SortedMapKWKind<K, A>> {
    override fun empty(): SortedMapKW<K, A> = emptyMap<K, A>().toSortedMap().k()
}