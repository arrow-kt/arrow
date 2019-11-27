package arrow.test.generators

import arrow.Kind
import arrow.core.ForId
import arrow.core.ForListK
import arrow.core.ForNonEmptyList
import arrow.core.ForOption
import arrow.core.ForSequenceK
import arrow.core.MapKPartialOf
import arrow.core.SortedMapKPartialOf
import arrow.extension
import io.kotlintest.properties.Gen

interface GenK<F> {
  /**
   * lifts a Gen<A> to the context F. the resulting Gen can be used to create types Kind<F, A>
   */
  fun <A> genK(gen: Gen<A>): Gen<Kind<F, A>>
}

@extension
interface OptionGenK : GenK<ForOption> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<ForOption, A>> =
    Gen.option(gen) as Gen<Kind<ForOption, A>>
}

@extension
interface IdGenK : GenK<ForId> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<ForId, A>> =
    Gen.id(gen) as Gen<Kind<ForId, A>>
}

@extension
interface ListKGenK : GenK<ForListK> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<ForListK, A>> =
    Gen.listK(gen) as Gen<Kind<ForListK, A>>
}

@extension
interface NonEmptyListGenK : GenK<ForNonEmptyList> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<ForNonEmptyList, A>> =
    Gen.nonEmptyList(gen) as Gen<Kind<ForNonEmptyList, A>>
}

@extension
interface SequenceKGenK : GenK<ForSequenceK> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<ForSequenceK, A>> =
    Gen.sequenceK(gen) as Gen<Kind<ForSequenceK, A>>
}

@extension
interface MapKGenK<K> : GenK<MapKPartialOf<K>> {

  fun kGen(): Gen<K>

  override fun <A> genK(gen: Gen<A>): Gen<Kind<MapKPartialOf<K>, A>> =
    Gen.mapK(kGen(), gen) as Gen<Kind<MapKPartialOf<K>, A>>
}

@extension
interface SortedMapKGenK<K : Comparable<K>> : GenK<SortedMapKPartialOf<K>> {

  fun kGen(): Gen<K>

  override fun <A> genK(gen: Gen<A>): Gen<Kind<SortedMapKPartialOf<K>, A>> =
    Gen.sortedMapK(kGen(), gen) as Gen<Kind<SortedMapKPartialOf<K>, A>>
}
