package arrow

@higherkind
data class MapKW<K, out A>(val map: Map<K, A>) : MapKWKind<K, A>, Map<K, A> by map {

    fun <B> map(f: (A) -> B): MapKW<K, B> = this.map.map { it.key to f(it.value) }.toMap().k()

    fun <B, Z> map2(fb: MapKW<K, B>, f: (A, B) -> Z): MapKW<K, Z> =
            if (fb.isEmpty()) emptyMap<K, Z>().k()
            else this.map.flatMap {
                (k, a) ->
                fb.getOption(k).map { Tuple2(k, f(a, it)) }.k().asIterable()
            }.k()

    fun <B, Z> map2Eval(fb: Eval<MapKW<K, B>>, f: (A, B) -> Z): Eval<MapKW<K, Z>> =
            if (fb.isEmpty()) Eval.now(emptyMap<K, Z>().k())
            else fb.map { b -> this.map2(b, f) }

    fun <B> ap(ff: MapKW<K, (A) -> B>): MapKW<K, B> =
            ff.flatMap { this.map(it) }

    fun <B, Z> ap2(f: MapKW<K, (A, B) -> Z>, fb: MapKW<K, B>): Map<K, Z> =
            f.map.flatMap {
                (k, f) ->
                this.flatMap { a -> fb.flatMap { b -> mapOf(Tuple2(k, f(a, b))).k() } }
                        .getOption(k).map { Tuple2(k, it) }.k().asIterable()
            }.k()

    fun <B> flatMap(f: (A) -> MapKW<K, B>): MapKW<K, B> =
            this.map.flatMap {
                (k, v) ->
                f(v).getOption(k).map { Tuple2(k, it) }.k().asIterable()
            }.k()

    fun <B> foldRight(b: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = this.map.values.iterator().iterateRight(b)(f)

    fun <B> foldLeft(b: B, f: (B, A) -> B): B = this.map.values.fold(b, f)

    fun <B> foldLeft(b: MapKW<K, B>, f: (MapKW<K, B>, Tuple2<K, A>) -> MapKW<K, B>): MapKW<K, B> =
            this.map.foldLeft(b) { m, (k, v) -> f(m.k(), Tuple2(k, v)) }.k()

    fun <G, B> traverse(f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, MapKW<K, B>> =
            Foldable.iterateRight(this.map.iterator(), Eval.always { GA.pure(emptyMap<K, B>().k()) })({ kv, lbuf ->
                GA.map2Eval(f(kv.value), lbuf) { (mapOf(kv.key to it.a).k() + it.b).k() }
            }).value()

    companion object
}

fun <K, A> Map<K, A>.k(): MapKW<K, A> = MapKW(this)

fun <K, A> Option<Tuple2<K, A>>.k(): MapKW<K, A> =
        when (this) {
            is Some -> mapOf(this.t).k()
            is None -> emptyMap<K, A>().k()
        }

fun <K, A> List<Map.Entry<K, A>>.k(): MapKW<K, A> = this.map { it.key to it.value }.toMap().k()

fun <K, A> Map<K, A>.getOption(k: K): Option<A> = Option.fromNullable(this[k])

fun <K, A> MapKW<K, A>.updated(k: K, value: A): MapKW<K, A> {
    val mutableMap = this.toMutableMap()
    mutableMap.put(k, value)

    return mutableMap.toMap().k()
}

fun <K, A, B> Map<K, A>.foldLeft(b: Map<K, B>, f: (Map<K, B>, Map.Entry<K, A>) -> Map<K, B>): Map<K, B> {
    var result = b
    this.forEach { result = f(result, it) }
    return result
}

fun <K, A, B> Map<K, A>.foldRight(b: Map<K, B>, f: (Map.Entry<K, A>, Map<K, B>) -> Map<K, B>): Map<K, B> =
    this.entries.reversed().k().map.foldLeft(b) { x, y -> f(y, x) }
