package arrow.data

import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.data.extensions.setk.eq.eq
import arrow.data.extensions.setk.foldable.foldable
import arrow.data.extensions.setk.hash.hash
import arrow.data.extensions.setk.monoidK.monoidK
import arrow.data.extensions.setk.semigroupK.semigroupK
import arrow.data.extensions.setk.show.show
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
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
