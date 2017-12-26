package arrow

@instance(MapKW::class)
interface MapKWFunctorInstance<K> : Functor<MapKWKindPartial<K>> {
    override fun <A, B> map(fa: HK<MapKWKindPartial<K>, A>, f: (A) -> B): MapKW<K, B> = fa.ev().map(f)
}

@instance(MapKW::class)
interface MapKWFoldableInstance<K> : Foldable<MapKWKindPartial<K>> {

    override fun <A, B> foldLeft(fa: HK<MapKWKindPartial<K>, A>, b: B, f: (B, A) -> B): B = fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: HK<MapKWKindPartial<K>, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            fa.ev().foldRight(lb, f)
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
            if (a.ev().size < b.ev().size) a.ev().foldLeft<A>(b.ev(), { my, (k, b) -> my.updated(k, SG().maybeCombine(b, my.get(k))) })
            else b.ev().foldLeft<A>(a.ev(), { my, (k, a) -> my.updated(k, SG().maybeCombine(a, my.get(k))) })

}

@instance(MapKW::class)
interface MapKWMonoidInstance<K, A> : MapKWSemigroupInstance<K, A>, Monoid<MapKWKind<K, A>> {

    override fun empty(): MapKW<K, A> = emptyMap<K, A>().k()
}

@instance(MapKW::class)
interface MapKWEqInstance<K, A> : Eq<MapKW<K, A>> {

    fun EQK(): Eq<K>

    fun EQA(): Eq<A>

    override fun eqv(a: MapKW<K, A>, b: MapKW<K, A>): Boolean =
            if (SetKWEqInstanceImplicits.instance(EQK()).eqv(a.keys.k(), b.keys.k())) {
                a.keys.map { key ->
                    b[key]?.let {
                        EQA().eqv(a.getValue(key), it)
                    } ?: false
                }.fold(true) { b1, b2 -> b1 && b2 }
            } else false

}