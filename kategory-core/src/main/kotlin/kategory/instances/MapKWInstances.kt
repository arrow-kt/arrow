package kategory

interface MapKWMonoid<K, A> : Monoid<MapKW<K, A>> {

    fun SG(): Semigroup<A>

    override fun empty(): MapKW<K, A> = emptyMap<K, A>().k()

    override fun combine(a: MapKW<K, A>, b: MapKW<K, A>): MapKW<K, A> =
        if (a.size < b.size) a.foldLeft<A> (b, { my, (k, b) -> my.updated(k, SG().maybeCombine(b, my.get(k))) })
        else b.foldLeft<A> (a, { my, (k, a) -> my.updated(k, SG().maybeCombine(a, my.get(k))) })

    companion object {
        operator fun <K, A> invoke(SG: Semigroup<A>): MapKWMonoid<K, A> = object : MapKWMonoid<K, A> {
            override fun SG(): Semigroup<A> = SG
        }
    }

}

interface MapKWHKTraverseInstance<K> : kategory.Traverse<MapKWKindPartial<K>> {
    override fun <A, B> map(fa: HK<MapKWKindPartial<K>, A>, f: (A) -> (B)): MapKW<K, B> =
            fa.ev().map(f)

    override fun <G, A, B> traverse(fa: HK<MapKWKindPartial<K>, A>, f: kotlin.Function1<A, kategory.HK<G, B>>, GA: kategory.Applicative<G>): kategory.HK<G, kategory.MapKW<K, B>> =
            fa.ev().traverse(f, GA)

    override fun <A, B> foldL(fa: HK<MapKWKindPartial<K>, A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.ev().foldL(b, f)

    override fun <A, B> foldR(fa: HK<MapKWKindPartial<K>, A>, lb: kategory.Eval<B>, f: kotlin.Function2<A, kategory.Eval<B>, kategory.Eval<B>>): kategory.Eval<B> =
            fa.ev().foldR(lb, f)

    override fun <A> isEmpty(fa: HK<MapKWKindPartial<K>, A>): kotlin.Boolean =
            fa.ev().isEmpty()
}