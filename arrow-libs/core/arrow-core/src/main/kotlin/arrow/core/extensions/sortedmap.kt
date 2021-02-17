package arrow.core.extensions

import arrow.Kind
import arrow.core.Eval
import arrow.core.Ior
import arrow.core.SetK
import arrow.core.SortedMapK
import arrow.core.SortedMapKOf
import arrow.core.SortedMapKPartialOf
import arrow.core.Tuple2
import arrow.core.extensions.list.functorFilter.flattenOption
import arrow.core.extensions.set.foldable.foldLeft
import arrow.core.extensions.setk.eq.eq
import arrow.core.extensions.setk.hash.hash
import arrow.core.extensions.sortedmapk.eq.eq
import arrow.core.fix
import arrow.core.getOption
import arrow.core.k
import arrow.core.toOption
import arrow.core.toT
import arrow.core.updated
import arrow.extension
import arrow.typeclasses.Align
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semialign
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse
import arrow.typeclasses.Unalign
import arrow.typeclasses.Unzip
import arrow.typeclasses.Zip

interface SortedMapKFunctor<A : Comparable<A>> : Functor<SortedMapKPartialOf<A>> {
  override fun <B, C> SortedMapKOf<A, B>.map(f: (B) -> C): SortedMapK<A, C> =
    fix().map(f)
}

fun <A : Comparable<A>> SortedMapK.Companion.functor(): SortedMapKFunctor<A> =
  object : SortedMapKFunctor<A> {}

interface SortedMapKFoldable<A : Comparable<A>> : Foldable<SortedMapKPartialOf<A>> {
  override fun <B, C> SortedMapKOf<A, B>.foldLeft(b: C, f: (C, B) -> C): C =
    fix().foldLeft(b, f)

  override fun <B, C> SortedMapKOf<A, B>.foldRight(lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fix().foldRight(lb, f)
}

fun <A : Comparable<A>> SortedMapK.Companion.foldable(): SortedMapKFoldable<A> =
  object : SortedMapKFoldable<A> {}

interface SortedMapKTraverse<A : Comparable<A>> : Traverse<SortedMapKPartialOf<A>>, SortedMapKFoldable<A> {
  override fun <G, B, C> SortedMapKOf<A, B>.traverse(AP: Applicative<G>, f: (B) -> Kind<G, C>): Kind<G, Kind<SortedMapKPartialOf<A>, C>> =
    fix().traverse(AP, f)
}

fun <A : Comparable<A>> SortedMapK.Companion.traverse(): SortedMapKTraverse<A> =
  object : SortedMapKTraverse<A> {}

interface SortedMapKSemigroup<A : Comparable<A>, B> : Semigroup<SortedMapK<A, B>> {
  fun SG(): Semigroup<B>

  override fun SortedMapK<A, B>.combine(b: SortedMapK<A, B>): SortedMapK<A, B> =
    if (this.fix().size < b.fix().size) this.fix().foldLeft<B>(b.fix()) { my, (k, b) ->
      my.updated(k, SG().run { b.maybeCombine(my[k]) })
    }
    else b.fix().foldLeft<B>(this.fix()) { my: SortedMapK<A, B>, (k, a) -> my.updated(k, SG().run { a.maybeCombine(my[k]) }) }
}

fun <A : Comparable<A>, B> SortedMapK.Companion.semigroup(SB: Semigroup<B>): SortedMapKSemigroup<A, B> =
  object : SortedMapKSemigroup<A, B> {
    override fun SG(): Semigroup<B> = SB
  }

interface SortedMapKMonoid<A : Comparable<A>, B> : Monoid<SortedMapK<A, B>>, SortedMapKSemigroup<A, B> {
  override fun empty(): SortedMapK<A, B> = sortedMapOf<A, B>().k()
}

fun <A : Comparable<A>, B> SortedMapK.Companion.monoid(SB: Semigroup<B>): SortedMapKMonoid<A, B> =
  object : SortedMapKMonoid<A, B> {
    override fun SG(): Semigroup<B> = SB
  }

interface SortedMapKShow<A : Comparable<A>, B> : Show<SortedMapKOf<A, B>> {
  fun SA(): Show<A>
  fun SB(): Show<B>
  override fun SortedMapKOf<A, B>.show(): String = fix().show(SA(), SB())
}

fun <A : Comparable<A>, B> SortedMapK.Companion.show(SA: Show<A>, SB: Show<B>): SortedMapKShow<A, B> =
  object : SortedMapKShow<A, B> {
    override fun SA(): Show<A> = SA
    override fun SB(): Show<B> = SB
  }

@extension
interface SortedMapKEq<K : Comparable<K>, A> : Eq<SortedMapK<K, A>> {
  fun EQK(): Eq<K>

  fun EQA(): Eq<A>

  override fun SortedMapK<K, A>.eqv(b: SortedMapK<K, A>): Boolean =
    if (SetK.eq(EQK()).run { keys.k().eqv(b.keys.k()) }) {
      keys.map { key ->
        b[key]?.let {
          EQA().run { getValue(key).eqv(it) }
        } ?: false
      }.fold(true) { b1, b2 -> b1 && b2 }
    } else false
}

@extension
interface SortedMapKHash<K : Comparable<K>, A> : Hash<SortedMapK<K, A>> {
  fun HK(): Hash<K>
  fun HA(): Hash<A>

  override fun SortedMapK<K, A>.hashWithSalt(salt: Int): Int =
    SetK.hash(HA()).run {
      values.toHashSet().k().hashWithSalt(salt)
    }.let { hash ->
      SetK.hash(HK()).run {
        keys.k().hashWithSalt(hash)
      }
    }
}

interface SortedMapKSemialign<K : Comparable<K>> : Semialign<SortedMapKPartialOf<K>>, SortedMapKFunctor<K> {
  override fun <A, B> align(
    a: Kind<SortedMapKPartialOf<K>, A>,
    b: Kind<SortedMapKPartialOf<K>, B>
  ): Kind<SortedMapKPartialOf<K>, Ior<A, B>> {
    val l = a.fix()
    val r = b.fix()
    val keys = l.keys + r.keys

    return keys.map { key ->
      Ior.fromOptions(l[key].toOption(), r[key].toOption()).map { key to it }
    }.flattenOption().toMap().toSortedMap().k()
  }
}

fun <K : Comparable<K>> SortedMapK.Companion.semialign(): SortedMapKSemialign<K> =
  object : SortedMapKSemialign<K> {}

interface SortedMapKAlign<K : Comparable<K>> : Align<SortedMapKPartialOf<K>>, SortedMapKSemialign<K> {
  override fun <A> empty(): Kind<SortedMapKPartialOf<K>, A> = emptyMap<K, A>().toSortedMap().k()
}

fun <K : Comparable<K>> SortedMapK.Companion.align(): SortedMapKAlign<K> =
  object : SortedMapKAlign<K> {}

@extension
interface SortedMapKEqK<K : Comparable<K>> : EqK<SortedMapKPartialOf<K>> {
  fun EQK(): Eq<K>

  override fun <A> Kind<SortedMapKPartialOf<K>, A>.eqK(other: Kind<SortedMapKPartialOf<K>, A>, EQ: Eq<A>): Boolean =
    SortedMapK.eq(EQK(), EQ).run { this@eqK.fix().eqv(other.fix()) }
}

interface SortedMapKUnalign<K : Comparable<K>> : Unalign<SortedMapKPartialOf<K>>, SortedMapKSemialign<K> {
  override fun <A, B> unalign(ior: Kind<SortedMapKPartialOf<K>, Ior<A, B>>): Tuple2<Kind<SortedMapKPartialOf<K>, A>, Kind<SortedMapKPartialOf<K>, B>> =
    ior.fix().let { map ->
      map.entries.foldLeft(emptyMap<K, A>() toT emptyMap<K, B>()) { (ls, rs), (k, v) ->
        v.fold(
          { a -> ls.plus(k to a) toT rs },
          { b -> ls toT rs.plus(k to b) },
          { a, b -> ls.plus(k to a) toT rs.plus(k to b) }
        )
      }.bimap({ it.toSortedMap().k() }, { it.toSortedMap().k() })
    }
}

fun <K : Comparable<K>> SortedMapK.Companion.unalign() = object : SortedMapKUnalign<K> {}

interface SortedMapKZip<K : Comparable<K>> : Zip<SortedMapKPartialOf<K>>, SortedMapKSemialign<K> {
  override fun <A, B> Kind<SortedMapKPartialOf<K>, A>.zip(other: Kind<SortedMapKPartialOf<K>, B>): Kind<SortedMapKPartialOf<K>, Tuple2<A, B>> =
    (this.fix() to other.fix()).let { (ls, rs) ->
      val keys = (ls.keys.intersect(rs.keys))

      val values = keys.map { key -> ls.getOption(key).flatMap { l -> rs.getOption(key).map { key to (l toT it) } } }.flattenOption()

      return values.toMap().toSortedMap().k()
    }
}

fun <K : Comparable<K>> SortedMapK.Companion.zip() = object : SortedMapKZip<K> {}

interface SortedMapKUnzip<K : Comparable<K>> : Unzip<SortedMapKPartialOf<K>>, SortedMapKZip<K> {
  override fun <A, B> Kind<SortedMapKPartialOf<K>, Tuple2<A, B>>.unzip(): Tuple2<Kind<SortedMapKPartialOf<K>, A>, Kind<SortedMapKPartialOf<K>, B>> =
    this.fix().let { map ->
      map.entries.fold(emptyMap<K, A>() toT emptyMap<K, B>()) { (ls, rs), (k, v) ->
        ls.plus(k to v.a) toT rs.plus(k to v.b)
      }
    }.bimap({ it.toSortedMap().k() }, { it.toSortedMap().k() })
}

fun <K : Comparable<K>> SortedMapK.Companion.unzip() = object : SortedMapKUnzip<K> {}
