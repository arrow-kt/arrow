package arrow.test.generators

import arrow.Kind
import arrow.core.Const
import arrow.core.ConstPartialOf
import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.core.ForId
import arrow.core.ForListK
import arrow.core.ForNonEmptyList
import arrow.core.ForOption
import arrow.core.ForSequenceK
import arrow.core.ForSetK
import arrow.core.ForTry
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
import arrow.core.Success
import arrow.core.Try
import arrow.core.Validated
import arrow.core.ValidatedPartialOf
import arrow.mtl.EitherT
import arrow.mtl.EitherTPartialOf
import arrow.mtl.typeclasses.Nested
import arrow.mtl.typeclasses.nest
import io.kotlintest.properties.Gen

interface GenK<F> {
  /**
   * lifts a Gen<A> to the context F. the resulting Gen can be used to create types Kind<F, A>
   */
  fun <A> genK(gen: Gen<A>): Gen<Kind<F, A>>
}

private val DEFAULT_COLLECTION_MAX_SIZE = 100

fun Option.Companion.genK() = object : GenK<ForOption> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<ForOption, A>> =
    Gen.option(gen) as Gen<Kind<ForOption, A>>
}

fun Id.Companion.genK() = object : GenK<ForId> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<ForId, A>> =
    Gen.id(gen) as Gen<Kind<ForId, A>>
}

fun ListK.Companion.genK(withMaxSize: Int = DEFAULT_COLLECTION_MAX_SIZE) = object : GenK<ForListK> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<ForListK, A>> =
    Gen.listK(gen).filter { it.size <= withMaxSize } as Gen<Kind<ForListK, A>>
}

fun NonEmptyList.Companion.genK(withMaxSize: Int = DEFAULT_COLLECTION_MAX_SIZE) = object : GenK<ForNonEmptyList> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<ForNonEmptyList, A>> =
    Gen.nonEmptyList(gen).filter { it.size <= withMaxSize } as Gen<Kind<ForNonEmptyList, A>>
}

fun SequenceK.Companion.genK() = object : GenK<ForSequenceK> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<ForSequenceK, A>> =
    Gen.sequenceK(gen) as Gen<Kind<ForSequenceK, A>>
}

fun <K> MapK.Companion.genK(kgen: Gen<K>, withMaxSize: Int = DEFAULT_COLLECTION_MAX_SIZE) =
  object : GenK<MapKPartialOf<K>> {
    override fun <A> genK(gen: Gen<A>): Gen<Kind<MapKPartialOf<K>, A>> =
      Gen.mapK(kgen, gen).filter { it.size <= withMaxSize } as Gen<Kind<MapKPartialOf<K>, A>>
  }

fun <K : Comparable<K>> SortedMapK.Companion.genK(kgen: Gen<K>, withMaxSize: Int = DEFAULT_COLLECTION_MAX_SIZE) =
  object : GenK<SortedMapKPartialOf<K>> {
    override fun <A> genK(gen: Gen<A>): Gen<Kind<SortedMapKPartialOf<K>, A>> =
      Gen.sortedMapK(kgen, gen) as Gen<Kind<SortedMapKPartialOf<K>, A>>
  }

fun SetK.Companion.genK(withMaxSize: Int = DEFAULT_COLLECTION_MAX_SIZE) = object : GenK<ForSetK> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<ForSetK, A>> =
    Gen.genSetK(gen).filter { it.size <= withMaxSize } as Gen<Kind<ForSetK, A>>
}

fun <A> Ior.Companion.genK(kgen: Gen<A>) =
  object : GenK<IorPartialOf<A>> {
    override fun <B> genK(gen: Gen<B>): Gen<Kind<IorPartialOf<A>, B>> =
      Gen.ior(kgen, gen) as Gen<Kind<IorPartialOf<A>, B>>
  }

fun <A> Either.Companion.genK(genA: Gen<A>) =
  object : GenK<EitherPartialOf<A>> {
    override fun <B> genK(gen: Gen<B>): Gen<Kind<EitherPartialOf<A>, B>> =
      Gen.either(genA, gen) as Gen<Kind<EitherPartialOf<A>, B>>
  }

fun <E> Validated.Companion.genK(genE: Gen<E>) =
  object : GenK<ValidatedPartialOf<E>> {
    override fun <A> genK(gen: Gen<A>): Gen<Kind<ValidatedPartialOf<E>, A>> =
      Gen.validated(genE, gen) as Gen<Kind<ValidatedPartialOf<E>, A>>
  }

fun <A> Const.Companion.genK(genA: Gen<A>) = object : GenK<ConstPartialOf<A>> {
  override fun <T> genK(gen: Gen<T>): Gen<Kind<ConstPartialOf<A>, T>> =
    genA.map {
      Const<A, T>(it)
    }
}

fun <F, L> EitherT.Companion.genK(genkF: GenK<F>, genL: Gen<L>) =
  object : GenK<EitherTPartialOf<F, L>> {
    override fun <R> genK(gen: Gen<R>): Gen<Kind<EitherTPartialOf<F, L>, R>> =
      genkF.genK(Gen.either(genL, gen)).map {
        EitherT(it)
      }
  }

fun <F, G> GenK<F>.nested(GENKG: GenK<G>): GenK<Nested<F, G>> = object : GenK<Nested<F, G>> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<Nested<F, G>, A>> =
    this@nested.genK(GENKG.genK(gen)).map { it.nest() }
}

fun Try.Companion.genK() = object : GenK<ForTry> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<ForTry, A>> =
    Gen.oneOf(
      gen.map {
        Success(it)
      }, Gen.throwable().map { Try.Failure(it) })
}
