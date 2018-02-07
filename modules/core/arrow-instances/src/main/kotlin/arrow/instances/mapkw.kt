package arrow.instances

import arrow.*
import arrow.core.*
import arrow.data.*
import arrow.typeclasses.*

@instance(MapKW::class)
interface MapKWFunctorInstance<K> : Functor<MapKWPartialOf<K>> {
    override fun <A, B> map(fa: Kind<MapKWPartialOf<K>, A>, f: (A) -> B): MapKW<K, B> = fa.reify().map(f)
}

@instance(MapKW::class)
interface MapKWFoldableInstance<K> : Foldable<MapKWPartialOf<K>> {

    override fun <A, B> foldLeft(fa: Kind<MapKWPartialOf<K>, A>, b: B, f: (B, A) -> B): B = fa.reify().foldLeft(b, f)

    override fun <A, B> foldRight(fa: Kind<MapKWPartialOf<K>, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            fa.reify().foldRight(lb, f)
}

@instance(MapKW::class)
interface MapKWTraverseInstance<K> : MapKWFoldableInstance<K>, Traverse<MapKWPartialOf<K>> {

    override fun <G, A, B> traverse(fa: Kind<MapKWPartialOf<K>, A>, f: (A) -> Kind<G, B>, GA: Applicative<G>): Kind<G, Kind<MapKWPartialOf<K>, B>> =
            fa.reify().traverse(f, GA)
}

@instance(MapKW::class)
interface MapKWSemigroupInstance<K, A> : Semigroup<MapKWOf<K, A>> {

    fun SG(): Semigroup<A>

    override fun combine(a: MapKWOf<K, A>, b: MapKWOf<K, A>): MapKW<K, A> =
            if (a.reify().size < b.reify().size) a.reify().foldLeft<A>(b.reify(), { my, (k, b) -> my.updated(k, SG().maybeCombine(b, my.get(k))) })
            else b.reify().foldLeft<A>(a.reify(), { my, (k, a) -> my.updated(k, SG().maybeCombine(a, my.get(k))) })

}

@instance(MapKW::class)
interface MapKWMonoidInstance<K, A> : MapKWSemigroupInstance<K, A>, Monoid<MapKWOf<K, A>> {

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