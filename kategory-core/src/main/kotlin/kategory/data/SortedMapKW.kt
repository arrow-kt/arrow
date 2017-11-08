package kategory

import java.util.*

@higherkind
data class SortedMapKW<K: Comparable<K>, A>(val map: SortedMap<K, A>) : SortedMapKWKind<K, A>, SortedMap<K, A> by map {

    fun <B> map(f: (A) -> B): SortedMapKW<K, B> =
            this.map.map { it.key to f(it.value) }.toMap().toSortedMap().k()

    fun <B, Z> map2(fb: SortedMapKW<K, B>, f: (A, B) -> Z): SortedMapKW<K, Z> =
            if (fb.isEmpty()) sortedMapOf<K, Z>().k()
            else this.map.flatMap { (k, a) ->
                fb.getOption(k).map { Tuple2(k, f(a, it)) }.k().asIterable()
            }.k()

    fun <B, Z> map2Eval(fb: Eval<SortedMapKW<K, B>>, f: (A, B) -> Z): Eval<SortedMapKW<K, Z>> =
            if (fb.isEmpty()) Eval.now(sortedMapOf<K, Z>().k())
            else fb.map { b -> this.map2(b, f) }

    fun <B> ap(ff: SortedMapKW<K, (A) -> B>): SortedMapKW<K, B> =
            ff.flatMap { this.map(it) }

    fun <B, Z> ap2(f: SortedMapKW<K, (A, B) -> Z>, fb: SortedMapKW<K, B>): SortedMap<K, Z> =
            f.map.flatMap { (k, f) ->
                this.flatMap { a -> fb.flatMap { b -> sortedMapOf(k to f(a, b)).k() } }
                        .getOption(k).map { Tuple2(k, it) }.k().asIterable()
            }.k()

    fun <B> flatMap(f: (A) -> SortedMapKW<K, B>): SortedMapKW<K, B> =
            this.map.flatMap { (k, v) ->
                f(v).getOption(k).map { Tuple2(k, it) }.k().asIterable()
            }.k()

    fun <B> foldR(b: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            this.map.values.iterator().iterateRight(b)(f)

    fun <B> foldL(b: B, f: (B, A) -> B): B = this.map.values.fold(b, f)

    fun <B> foldLeft(b: SortedMapKW<K, B>, f: (SortedMapKW<K, B>, Tuple2<K, A>) -> SortedMapKW<K, B>): SortedMapKW<K, B> =
            this.map.foldLeft(b) { m: SortedMap<K, B>, (k, v) -> f(m.k(), Tuple2(k, v)) }.k()

    fun <G, B> traverse(f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, SortedMapKW<K, B>> =
        Foldable.iterateRight(this.map.iterator(), Eval.always { GA.pure(sortedMapOf<K, B>().k()) })({
            kv, lbuf ->
            GA.map2Eval(f(kv.value), lbuf) { (mapOf(kv.key to it.a).k() + it.b).toSortedMap().k() }
        }).value()

    companion object
}

fun <K: Comparable<K>, A> SortedMap<K, A>.k(): SortedMapKW<K, A> = SortedMapKW(this)

fun <K: Comparable<K>, A> Option<Tuple2<K, A>>.k(): SortedMapKW<K, A> = when (this) {
    is Some -> sortedMapOf(this.value.a to this.value.b).k()
    is None -> sortedMapOf<K, A>().k()
}

fun <K: Comparable<K>, A> List<Map.Entry<K, A>>.k(): SortedMapKW<K, A> =
        this.map { it.key to it.value }.toMap().toSortedMap().k()

fun <K, A> SortedMap<K, A>.getOption(k: K): Option<A> = Option.fromNullable(this[k])

fun <K: Comparable<K>, A> SortedMapKW<K, A>.updated(k: K, value: A): SortedMapKW<K, A> {
    val sortedMutableMap = this.toSortedMap()
    sortedMutableMap.put(k, value)

    return sortedMutableMap.k()
}

fun <K, A, B> SortedMap<K, A>.foldLeft(b: SortedMap<K, B>, f: (SortedMap<K, B>, Map.Entry<K, A>) -> SortedMap<K, B>): SortedMap<K, B> {
    var result = b
    this.forEach { result = f(result, it) }
    return result
}

fun <K: Comparable<K>, A, B> SortedMap<K, A>.foldRight(b: SortedMap<K, B>, f: (Map.Entry<K, A>, SortedMap<K, B>) -> SortedMap<K, B>): SortedMap<K, B> =
        this.entries.reversed().k().map.foldLeft(b) { x: SortedMap<K, B>, y -> f(y, x) }
