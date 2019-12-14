package arrow.test.generators

import arrow.Kind
import arrow.core.ForId
import arrow.core.ForListK
import arrow.core.ForNonEmptyList
import arrow.core.ForOption
import arrow.core.ForSequenceK
import arrow.core.ForSetK
import arrow.core.Id
import arrow.core.Ior
import arrow.core.IorPartialOf
import arrow.core.ListK
import arrow.core.MapK
import arrow.core.MapKPartialOf
import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.SequenceK
import arrow.core.SetK
import arrow.core.SortedMapK
import arrow.core.SortedMapKPartialOf
import io.kotlintest.properties.Gen

interface GenK<F> {
  /**
   * lifts a Gen<A> to the context F. the resulting Gen can be used to create types Kind<F, A>
   */
  fun <A> genK(gen: Gen<A>): Gen<Kind<F, A>>
}

fun Option.Companion.genK() = object : GenK<ForOption> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<ForOption, A>> =
    Gen.option(gen) as Gen<Kind<ForOption, A>>
}

fun Id.Companion.genK() = object : GenK<ForId> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<ForId, A>> =
    Gen.id(gen) as Gen<Kind<ForId, A>>
}

fun ListK.Companion.genK() = object : GenK<ForListK> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<ForListK, A>> =
    Gen.listK(gen) as Gen<Kind<ForListK, A>>
}

fun NonEmptyList.Companion.genK() = object : GenK<ForNonEmptyList> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<ForNonEmptyList, A>> =
    Gen.nonEmptyList(gen) as Gen<Kind<ForNonEmptyList, A>>
}

fun SequenceK.Companion.genK() = object : GenK<ForSequenceK> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<ForSequenceK, A>> =
    Gen.sequenceK(gen) as Gen<Kind<ForSequenceK, A>>
}

fun <K> MapK.Companion.genK(kgen: Gen<K>) =
  object : GenK<MapKPartialOf<K>> {
    override fun <A> genK(gen: Gen<A>): Gen<Kind<MapKPartialOf<K>, A>> =
      Gen.mapK(kgen, gen) as Gen<Kind<MapKPartialOf<K>, A>>
  }

fun <K : Comparable<K>> SortedMapK.Companion.genK(kgen: Gen<K>) =
  object : GenK<SortedMapKPartialOf<K>> {
    override fun <A> genK(gen: Gen<A>): Gen<Kind<SortedMapKPartialOf<K>, A>> =
      Gen.sortedMapK(kgen, gen) as Gen<Kind<SortedMapKPartialOf<K>, A>>
  }

fun SetK.Companion.genK() = object : GenK<ForSetK> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<ForSetK, A>> =
    Gen.genSetK(gen) as Gen<Kind<ForSetK, A>>
}

fun <A> Ior.Companion.genK(kgen: Gen<A>) =
  object : GenK<IorPartialOf<A>> {
    override fun <B> genK(gen: Gen<B>): Gen<Kind<IorPartialOf<A>, B>> =
      Gen.ior(kgen, gen) as Gen<Kind<IorPartialOf<A>, B>>
  }
