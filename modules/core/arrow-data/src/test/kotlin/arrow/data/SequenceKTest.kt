package arrow.data

import arrow.Kind
import arrow.instances.IntEqInstance
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import arrow.typeclasses.Show
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class SequenceKTest : UnitSpec() {
  val applicative = SequenceK.applicative()

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
      EqLaws.laws(SequenceK.eq(IntEqInstance)) { sequenceOf(it).k() },
      ShowLaws.laws(show, eq) { sequenceOf(it).k() },
      MonadLaws.laws(SequenceK.monad(), eq),
      MonoidKLaws.laws(SequenceK.monoidK(), applicative, eq),
      TraverseLaws.laws(SequenceK.traverse(), applicative, { n: Int -> SequenceK(sequenceOf(n)) }, eq)
    )
  }
}
