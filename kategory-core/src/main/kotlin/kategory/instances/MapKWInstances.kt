package kategory

@instance(MapKW::class)
interface MapKWFunctorInstance<K> : Functor<MapKWKindPartial<K>> {
    override fun <A, B> map(fa: HK<MapKWKindPartial<K>, A>, f: (A) -> B): MapKW<K, B> = fa.ev().map(f)
}

@instance(MapKW::class)
interface MapKWFoldableInstance<K> : Foldable<MapKWKindPartial<K>> {

    override fun <A, B> foldL(fa: HK<MapKWKindPartial<K>, A>, b: B, f: (B, A) -> B): B = fa.ev().foldL(b, f)

    override fun <A, B> foldR(fa: HK<MapKWKindPartial<K>, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            fa.ev().foldR(lb, f)
}

@instance(MapKW::class)
interface MapKWTraverseInstance<K> : MapKWFoldableInstance<K>, Traverse<MapKWKindPartial<K>> {

    override fun <G, A, B> traverse(fa: HK<MapKWKindPartial<K>, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, HK<MapKWKindPartial<K>, B>> =
            fa.ev().traverse(f, GA)
}


@instance(MapKW::class)
interface MapKWSemigroupInstance<K, A> : Semigroup<MapKWKind<K, A>> {

    fun SG(): Semigroup<A>

    override fun combine(a: MapKWKind<K, A>, b: MapKWKind<K, A>): MapKW<K, A> =
            if (a.ev().size < b.ev().size) a.ev().foldLeft<A> (b.ev(), { my, (k, b) -> my.updated(k, SG().maybeCombine(b, my.get(k))) })
            else b.ev().foldLeft<A> (a.ev(), { my, (k, a) -> my.updated(k, SG().maybeCombine(a, my.get(k))) })

}

@instance(MapKW::class)
interface MapKWMonoidInstance<K, A> : MapKWSemigroupInstance<K, A>, Monoid<MapKWKind<K, A>> {

    override fun empty(): MapKW<K, A> = emptyMap<K, A>().k()
}
