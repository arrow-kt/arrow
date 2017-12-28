package arrow.fromkotlin

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import arrow.kindedj.KJDataclassArrowShow
import arrow.kindedj.fromKindedJ
import arrow.kindedj.fromkindedj.KJDataclassHK.KJDataclass1
import arrow.kindedj.fromkindedj.KJDataclassKindedJShow
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class KJDataclassTestsKotlin : StringSpec() {

    private val kinded = KJDataclass1(0)

    init {
        "Values should be convertible" {
            KJDataclassKindedJShow.INSTANCE.show(kinded) shouldBe KJDataclassArrowShow.show(kinded.fromKindedJ())
        }
    }
}