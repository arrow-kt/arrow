package arrow.data

import arrow.instances.IntEqInstance
import arrow.instances.eq
import arrow.instances.syntax
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class SetKTest : UnitSpec() {

  init {

    val EQ = SetK.eq(Int.eq())

    SetK syntax {
      testLaws(
        EqLaws.laws(EQ) { SetK.just(it) },
        ShowLaws.laws(SetK.show(), EQ) { SetK.just(it) },
        SemigroupKLaws.laws(this, { SetK.just(it) }, Eq.any()),
        MonoidKLaws.laws(this, { SetK.just(it) }, Eq.any()),
        FoldableLaws.laws(this, { SetK.just(it) }, Eq.any())
      )
    }
  }
}
