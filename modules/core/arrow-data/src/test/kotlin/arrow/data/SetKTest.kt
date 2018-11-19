package arrow.data

import arrow.instances.eq
import arrow.instances.hash
import arrow.instances.setk.eq.eq
import arrow.instances.setk.foldable.foldable
import arrow.instances.setk.hash.hash
import arrow.instances.setk.monoidK.monoidK
import arrow.instances.setk.semigroupK.semigroupK
import arrow.instances.setk.show.show
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class SetKTest : UnitSpec() {

  init {

    val EQ = SetK.eq(Int.eq())

    testLaws(
      ShowLaws.laws(SetK.show(), EQ) { SetK.just(it) },
      SemigroupKLaws.laws(SetK.semigroupK(), { SetK.just(it) }, Eq.any()),
      MonoidKLaws.laws(SetK.monoidK(), { SetK.just(it) }, Eq.any()),
      FoldableLaws.laws(SetK.foldable(), { SetK.just(it) }, Eq.any()),
      HashLaws.laws(SetK.hash(Int.hash()), SetK.eq(Int.eq())) { SetK.just(it) }
    )
  }
}
