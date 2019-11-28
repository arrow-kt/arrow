package arrow.core.extensions

import arrow.Kind
import arrow.core.Eval
import arrow.core.Ior
import arrow.core.MapK
import arrow.core.MapKOf
import arrow.core.MapKPartialOf
import arrow.core.Option
import arrow.core.SetK
import arrow.core.Tuple2
import arrow.core.extensions.list.functorFilter.flattenOption
import arrow.core.extensions.mapk.eq.eq
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.set.foldable.foldLeft
import arrow.core.extensions.setk.eq.eq
import arrow.core.extensions.setk.hash.hash
import arrow.core.fix
import arrow.core.identity
import arrow.core.k
import arrow.core.sequence
import arrow.core.toMap
import arrow.core.toOption
import arrow.core.toT
import arrow.core.updated
import arrow.extension
import arrow.typeclasses.Align
import arrow.typeclasses.Applicative
import arrow.typeclasses.Apply
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.FunctorFilter
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semialign
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse
import arrow.typeclasses.Unalign
import arrow.undocumented

@extension
@undocumented
interface MapKFunctor<K> : Functor<MapKPartialOf<K>> {
  override fun <A, B> Kind<MapKPartialOf<K>, A>.map(f: (A) -> B): MapK<K, B> = fix().map(f)
}

@extension
@undocumented
interface MapKFoldable<K> : Foldable<MapKPartialOf<K>> {

  override fun <A, B> Kind<MapKPartialOf<K>, A>.foldLeft(b: B, f: (B, A) -> B): B = fix().foldLeft(b, f)

  override fun <A, B> Kind<MapKPartialOf<K>, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)
}

@extension
@undocumented
interface MapKTraverse<K> : Traverse<MapKPartialOf<K>>, MapKFoldable<K> {

  override fun <G, A, B> MapKOf<K, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, MapKOf<K, B>> =
    fix().traverse(AP, f)
}

@extension
interface MapKSemigroup<K, A> : Semigroup<MapK<K, A>> {

  fun SG(): Semigroup<A>

  override fun MapK<K, A>.combine(b: MapK<K, A>): MapK<K, A> = with(SG()) {
    if (fix().size < b.fix().size) fix().foldLeft<A>(b.fix()) { my, (k, b) -> my.updated(k, b.maybeCombine(my[k])) }
    else b.fix().foldLeft<A>(fix()) { my, (k, a) -> my.updated(k, a.maybeCombine(my[k])) }
  }
}

@extension
interface MapKFunctorFilter<K> : FunctorFilter<MapKPartialOf<K>> {
  override fun <A, B> Kind<MapKPartialOf<K>, A>.filterMap(f: (A) -> Option<B>): Kind<MapKPartialOf<K>, B> =
    fix().map(f).sequence(Option.applicative()).fix().fold({ emptyMap<K, B>().k() }, ::identity)

  override fun <A, B> Kind<MapKPartialOf<K>, A>.map(f: (A) -> B): Kind<MapKPartialOf<K>, B> =
    fix().map(f)
}

@extension
interface MapKApply<K> : Apply<MapKPartialOf<K>> {
  override fun <A, B> Kind<MapKPartialOf<K>, A>.ap(ff: Kind<MapKPartialOf<K>, (A) -> B>): Kind<MapKPartialOf<K>, B> =
    fix().ap(ff.fix())

  override fun <A, B> Kind<MapKPartialOf<K>, A>.map(f: (A) -> B): Kind<MapKPartialOf<K>, B> =
    fix().map(f)
}

@extension
interface MapKMonoid<K, A> : Monoid<MapK<K, A>>, MapKSemigroup<K, A> {

  override fun SG(): Semigroup<A>

  override fun empty(): MapK<K, A> = emptyMap<K, A>().k()
}

@extension
interface MapKEq<K, A> : Eq<MapK<K, A>> {

  fun EQK(): Eq<K>

  fun EQA(): Eq<A>

  override fun MapK<K, A>.eqv(b: MapK<K, A>): Boolean =
    if (SetK.eq(EQK()).run { keys.k().eqv(b.keys.k()) }) {
      keys.map { key ->
        b[key]?.let {
          EQA().run { getValue(key).eqv(it) }
        } ?: false
      }.fold(true) { b1, b2 -> b1 && b2 }
    } else false
}

@extension
interface MapKShow<K, A> : Show<MapK<K, A>> {
  override fun MapK<K, A>.show(): String =
    toString()
}

@extension
interface MapKHash<K, A> : Hash<MapK<K, A>>, MapKEq<K, A> {
  fun HK(): Hash<K>
  fun HA(): Hash<A>

  override fun EQK(): Eq<K> = HK()
  override fun EQA(): Eq<A> = HA()

  // Somewhat mirrors HashMap.Node.hashCode in that the combinator there between key and value is xor
  override fun MapK<K, A>.hash(): Int =
    SetK.hash(HK()).run { keys.k().hash() } xor foldLeft(1) { hash, a ->
      31 * hash + HA().run { a.hash() }
    }
}

@extension
interface MapKSemialign<K> : Semialign<MapKPartialOf<K>>, MapKFunctor<K> {
  override fun <A, B> align(
    a: Kind<MapKPartialOf<K>, A>,
    b: Kind<MapKPartialOf<K>, B>
  ): Kind<MapKPartialOf<K>, Ior<A, B>> {
    val l = a.fix()
    val r = b.fix()
    val keys = l.keys + r.keys

    return keys.map { key ->
      Ior.fromOptions(l[key].toOption(), r[key].toOption()).map { key toT it }
    }.flattenOption().toMap().k()
  }
}

@extension
interface MapKAlign<K> : Align<MapKPartialOf<K>>, MapKSemialign<K> {
  override fun <A> empty(): Kind<MapKPartialOf<K>, A> = emptyMap<K, A>().k()
}

@extension
interface MapKUnalign<K> : Unalign<MapKPartialOf<K>>, MapKSemialign<K> {
  override fun <A, B> unalign(ior: Kind<MapKPartialOf<K>, Ior<A, B>>): Tuple2<Kind<MapKPartialOf<K>, A>, Kind<MapKPartialOf<K>, B>> =
    ior.fix().let { map ->
      map.entries.foldLeft(emptyMap<K, A>() toT emptyMap<K, B>()) { (ls, rs), (k, v) ->
        v.fold(
          { a -> ls.plus(k to a) toT rs },
          { b -> ls toT rs.plus(k to b) },
          { a, b -> ls.plus(k to a) toT rs.plus(k to b) })
      }.bimap({ it.k() }, { it.k() })
    }
}

@extension
interface MapKEqK<K> : EqK<MapKPartialOf<K>> {

  fun EQK(): Eq<K>

  override fun <A> Kind<MapKPartialOf<K>, A>.eqK(other: Kind<MapKPartialOf<K>, A>, EQ: Eq<A>): Boolean =
    MapK.eq(EQK(), EQ).run {
      this@eqK.fix().eqv(other.fix())
    }
}
