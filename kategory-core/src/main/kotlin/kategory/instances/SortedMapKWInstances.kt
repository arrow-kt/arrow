package kategory

@instance(SortedMapKW::class)
interface SortedMapKWFunctorInstance<K: Comparable<K>> : Functor<SortedMapKWKindPartial<K>> {
    override fun <A, B> map(fa: HK<SortedMapKWKindPartial<K>, A>, f: (A) -> B): SortedMapKW<K, B> =
            fa.ev().map(f)
}

@instance(SortedMapKW::class)
interface SortedMapKWFoldableInstance<K: Comparable<K>> : Foldable<SortedMapKWKindPartial<K>> {
    override fun <A, B> foldL(fa: HK<SortedMapKWKindPartial<K>, A>, b: B, f: (B, A) -> B): B =
            fa.ev().foldL(b, f)

    override fun <A, B> foldR(fa: HK<SortedMapKWKindPartial<K>, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            fa.ev().foldR(lb, f)
}

@instance(SortedMapKW::class)
interface SortedMapKWTraverseInstance<K: Comparable<K>> : SortedMapKWFoldableInstance<K>, Traverse<SortedMapKWKindPartial<K>> {
    override fun <G, A, B> traverse(fa: HK<SortedMapKWKindPartial<K>, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, HK<SortedMapKWKindPartial<K>, B>> =
            fa.ev().traverse(f, GA)
}

@instance(SortedMapKW::class)
interface SortedMapKWSemigroupInstance<K: Comparable<K>, A> : Semigroup<SortedMapKWKind<K, A>> {
    fun SG(): Semigroup<A>

    override fun combine(a: SortedMapKWKind<K, A>, b: SortedMapKWKind<K, A>): SortedMapKWKind<K, A> =
            if (a.ev().size < b.ev().size) a.ev().foldLeft<A> (b.ev(), { my, (k, b) ->
                my.updated(k, SG().maybeCombine(b, my[k]))
            })
            else b.ev().foldLeft<A> (a.ev(), { my: SortedMapKW<K, A>, (k, a) -> my.updated(k, SG().maybeCombine(a, my[k])) })
}

@instance(SortedMapKW::class)
interface SortedMapKWMonoidInstance<K: Comparable<K>, A> : SortedMapKWSemigroupInstance<K, A>, Monoid<SortedMapKWKind<K, A>> {
    override fun empty(): SortedMapKW<K, A> = sortedMapOf<K, A>().k()
}
