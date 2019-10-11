package arrow.core

import arrow.Kind
import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.sequencek.applicative.applicative
import arrow.core.extensions.sequencek.eq.eq
import arrow.core.extensions.sequencek.functorFilter.functorFilter
import arrow.core.extensions.sequencek.hash.hash
import arrow.core.extensions.sequencek.monad.monad
import arrow.core.extensions.sequencek.monoid.monoid
import arrow.core.extensions.sequencek.monoidK.monoidK
import arrow.core.extensions.sequencek.monoidal.monoidal
import arrow.core.extensions.sequencek.traverse.traverse
import arrow.test.UnitSpec
import arrow.test.generators.sequenceK
import arrow.test.laws.FunctorFilterLaws
import arrow.test.laws.HashLaws
import arrow.test.laws.MonadLaws
import arrow.test.laws.MonoidKLaws
import arrow.test.laws.MonoidLaws
import arrow.test.laws.MonoidalLaws
import arrow.test.laws.ShowLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Show
import io.kotlintest.properties.Gen

class SequenceKTest : UnitSpec() {

  init {

    val eq: Eq<Kind<ForSequenceK, Int>> = object : Eq<Kind<ForSequenceK, Int>> {
      override fun Kind<ForSequenceK, Int>.eqv(b: Kind<ForSequenceK, Int>): Boolean =
        fix().toList() == b.fix().toList()
    }

    val associativeSemigroupalEq: Eq<Kind<ForSequenceK, Tuple2<Int, Tuple2<Int, Int>>>> = object : Eq<Kind<ForSequenceK, Tuple2<Int, Tuple2<Int, Int>>>> {
      override fun Kind<ForSequenceK, Tuple2<Int, Tuple2<Int, Int>>>.eqv(b: Kind<ForSequenceK, Tuple2<Int, Tuple2<Int, Int>>>): Boolean =
        fix().toList() == b.fix().toList()
    }

    val tuple2Eq: Eq<Kind<ForSequenceK, Tuple2<Int, Int>>> = object : Eq<Kind<ForSequenceK, Tuple2<Int, Int>>> {
      override fun Kind<ForSequenceK, Tuple2<Int, Int>>.eqv(b: Kind<ForSequenceK, Tuple2<Int, Int>>): Boolean =
        fix().toList() == b.fix().toList()
    }

    val show: Show<Kind<ForSequenceK, Int>> = object : Show<Kind<ForSequenceK, Int>> {
      override fun Kind<ForSequenceK, Int>.show(): String =
        fix().toList().toString()
    }

    testLaws(
      ShowLaws.laws(show, eq) { sequenceOf(it).k() },
      MonadLaws.laws(SequenceK.monad(), eq),
      MonoidKLaws.laws(SequenceK.monoidK(), SequenceK.applicative(), eq),
      MonoidLaws.laws(SequenceK.monoid(), Gen.sequenceK(Gen.int()), eq),
      MonoidalLaws.laws(SequenceK.monoidal(), { SequenceK.just(it) }, tuple2Eq, this::bijection, associativeSemigroupalEq),
      TraverseLaws.laws(SequenceK.traverse(), SequenceK.applicative(), { n: Int -> SequenceK(sequenceOf(n)) }, eq),
      FunctorFilterLaws.laws(SequenceK.functorFilter(), { SequenceK.just(it) }, eq),
      HashLaws.laws(SequenceK.hash(Int.hash()), SequenceK.eq(Int.eq())) { sequenceOf(it).k() }
    )
  }

  private fun bijection(from: Kind<ForSequenceK, Tuple2<Tuple2<Int, Int>, Int>>): SequenceK<Tuple2<Int, Tuple2<Int, Int>>> =
    from.fix().toList().map { Tuple2(it.a.a, Tuple2(it.a.b, it.b)) }.asSequence().k()
}
