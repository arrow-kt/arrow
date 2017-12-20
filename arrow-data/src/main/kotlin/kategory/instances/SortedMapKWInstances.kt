package arrow

@instance(SortedMapKW::class)
interface SortedMapKWFunctorInstance<A: Comparable<A>> : Functor<SortedMapKWKindPartial<A>> {
    override fun <B, C> map(fb: HK<SortedMapKWKindPartial<A>, B>, f: (B) -> C): SortedMapKW<A, C> =
            fb.ev().map(f)
}

@instance(SortedMapKW::class)
interface SortedMapKWFoldableInstance<A: Comparable<A>> : Foldable<SortedMapKWKindPartial<A>> {
    override fun <B, C> foldLeft(fb: HK<SortedMapKWKindPartial<A>, B>, c: C, f: (C, B) -> C): C =
            fb.ev().foldLeft(c, f)

    override fun <B, C> foldRight(fb: HK<SortedMapKWKindPartial<A>, B>, lc: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> =
            fb.ev().foldRight(lc, f)
}

@instance(SortedMapKW::class)
interface SortedMapKWTraverseInstance<A: Comparable<A>> : SortedMapKWFoldableInstance<A>, Traverse<SortedMapKWKindPartial<A>> {
    override fun <G, B, C> traverse(fb: HK<SortedMapKWKindPartial<A>, B>, f: (B) -> HK<G, C>, GA: Applicative<G>): HK<G, HK<SortedMapKWKindPartial<A>, C>> =
            fb.ev().traverse(f, GA)
}

@instance(SortedMapKW::class)
interface SortedMapKWSemigroupInstance<A: Comparable<A>, B> : Semigroup<SortedMapKWKind<A, B>> {
    fun SG(): Semigroup<B>

    override fun combine(a: SortedMapKWKind<A, B>, b: SortedMapKWKind<A, B>): SortedMapKWKind<A, B> =
            if (a.ev().size < b.ev().size) a.ev().foldLeft<B> (b.ev(), { my, (k, b) ->
                my.updated(k, SG().maybeCombine(b, my[k]))
            })
            else b.ev().foldLeft<B> (a.ev(), { my: SortedMapKW<A, B>, (k, a) -> my.updated(k, SG().maybeCombine(a, my[k])) })
}

@instance(SortedMapKW::class)
interface SortedMapKWMonoidInstance<A: Comparable<A>, B> : SortedMapKWSemigroupInstance<A, B>, Monoid<SortedMapKWKind<A, B>> {
    override fun empty(): SortedMapKW<A, B> = sortedMapOf<A, B>().k()
}
