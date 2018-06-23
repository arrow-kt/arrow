package arrow.data

import arrow.instances.IntEqInstance
import arrow.instances.eq
import arrow.instances.extensions
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class SetKTest : UnitSpec() {

  init {

    val EQ = SetK.eq(Int.eq())

    ForSetK extensions {
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
