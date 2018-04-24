package arrow.fromkotlin

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import arrow.kindedj.KatDataclass1
import arrow.kindedj.KatDataclassArrowShow
import arrow.kindedj.fromarrow.KatDataclassKindedJShow
import arrow.kindedj.toKindedJ
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class KatDataclassTestsKotlin : StringSpec() {
  private val kinded = KatDataclass1(0)

  init {
    "Values should be convertible" {
      KatDataclassArrowShow.show(kinded) shouldBe KatDataclassKindedJShow.INSTANCE.show(kinded.toKindedJ())
    }
  }
}
