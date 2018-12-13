package arrow.data

import arrow.Kind
import arrow.instances.eq
import arrow.instances.hash
import arrow.instances.sequencek.applicative.applicative
import arrow.instances.sequencek.eq.eq
import arrow.instances.sequencek.hash.hash
import arrow.instances.sequencek.monad.monad
import arrow.instances.sequencek.monoid.monoid
import arrow.instances.sequencek.monoidK.monoidK
import arrow.instances.sequencek.traverse.traverse
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import arrow.typeclasses.Show
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.map
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class SequenceKTest : UnitSpec() {

  init {

    val eq: Eq<Kind<ForSequenceK, Int>> = object : Eq<Kind<ForSequenceK, Int>> {
      override fun Kind<ForSequenceK, Int>.eqv(b: Kind<ForSequenceK, Int>): Boolean =
        toList() == b.toList()
    }

    val show: Show<Kind<ForSequenceK, Int>> = object : Show<Kind<ForSequenceK, Int>> {
      override fun Kind<ForSequenceK, Int>.show(): String =
        toList().toString()
    }

    testLaws(
      ShowLaws.laws(show, eq) { sequenceOf(it).k() },
      MonadLaws.laws(SequenceK.monad(), eq),
      MonoidKLaws.laws(SequenceK.monoidK(), SequenceK.applicative(), eq),
      MonoidLaws.laws(SequenceK.monoid(), Gen.list(Gen.int()).map { it.asSequence() }.generate().k(), eq),
      TraverseLaws.laws(SequenceK.traverse(), SequenceK.applicative(), { n: Int -> SequenceK(sequenceOf(n)) }, eq),
      HashLaws.laws(SequenceK.hash(Int.hash()), SequenceK.eq(Int.eq())) { sequenceOf(it).k() }
    )
  }
}
