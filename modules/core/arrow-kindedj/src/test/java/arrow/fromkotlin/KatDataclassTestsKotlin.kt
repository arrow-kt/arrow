package arrow.fromkotlin

import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.specs.StringSpec
import arrow.kindedj.KatDataclass1
import arrow.kindedj.KatDataclassArrowShow
import arrow.kindedj.fromarrow.KatDataclassKindedJShow
import arrow.kindedj.toKindedJ
import io.kotlintest.shouldBe
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class KatDataclassTestsKotlin : StringSpec() {
  private val kinded = KatDataclass1(0)

  init {
    "Values should be convertible" {
      KatDataclassArrowShow.show(kinded) shouldBe KatDataclassKindedJShow.INSTANCE.show(kinded.toKindedJ())
    }
  }
}
