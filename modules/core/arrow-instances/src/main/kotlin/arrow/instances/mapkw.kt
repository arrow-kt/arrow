package arrow.instances

import arrow.*
import arrow.core.*
import arrow.data.*
import arrow.typeclasses.*

@instance(MapK::class)
interface MapKFunctorInstance<K> : Functor<MapKPartialOf<K>> {
    override fun <A, B> map(fa: Kind<MapKPartialOf<K>, A>, f: (A) -> B): MapK<K, B> = fa.extract().map(f)
}

@instance(MapK::class)
interface MapKFoldableInstance<K> : Foldable<MapKPartialOf<K>> {

    override fun <A, B> foldLeft(fa: Kind<MapKPartialOf<K>, A>, b: B, f: (B, A) -> B): B = fa.extract().foldLeft(b, f)

    override fun <A, B> foldRight(fa: Kind<MapKPartialOf<K>, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            fa.extract().foldRight(lb, f)
}

@instance(MapK::class)
interface MapKTraverseInstance<K> : MapKFoldableInstance<K>, Traverse<MapKPartialOf<K>> {

    override fun <G, A, B> traverse(fa: Kind<MapKPartialOf<K>, A>, f: (A) -> Kind<G, B>, GA: Applicative<G>): Kind<G, Kind<MapKPartialOf<K>, B>> =
            fa.extract().traverse(f, GA)
}

@instance(MapK::class)
interface MapKSemigroupInstance<K, A> : Semigroup<MapKOf<K, A>> {

    fun SG(): Semigroup<A>

    override fun combine(a: MapKOf<K, A>, b: MapKOf<K, A>): MapK<K, A> =
            if (a.extract().size < b.extract().size) a.extract().foldLeft<A>(b.extract(), { my, (k, b) -> my.updated(k, SG().maybeCombine(b, my.get(k))) })
            else b.extract().foldLeft<A>(a.extract(), { my, (k, a) -> my.updated(k, SG().maybeCombine(a, my.get(k))) })

}

@instance(MapK::class)
interface MapKMonoidInstance<K, A> : MapKSemigroupInstance<K, A>, Monoid<MapKOf<K, A>> {

    override fun empty(): MapK<K, A> = emptyMap<K, A>().k()
}

@instance(MapK::class)
interface MapKEqInstance<K, A> : Eq<MapK<K, A>> {

    fun EQK(): Eq<K>

    fun EQA(): Eq<A>

    override fun eqv(a: MapK<K, A>, b: MapK<K, A>): Boolean =
            if (SetKEqInstanceImplicits.instance(EQK()).eqv(a.keys.k(), b.keys.k())) {
                a.keys.map { key ->
                    b[key]?.let {
                        EQA().eqv(a.getValue(key), it)
                    } ?: false
                }.fold(true) { b1, b2 -> b1 && b2 }
            } else false

}

@instance(MapK::class)
interface MapKShowInstance<K, A> : Show<MapK<K, A>> {
    override fun show(a: MapK<K, A>): String =
            a.toString()
}
