package kategory.fromkotlin

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import kategory.kindedj.KatDataclass1
import kategory.kindedj.KatDataclassKategoryShow
import kategory.kindedj.fromkategory.KatDataclassKindedJShow
import kategory.kindedj.toKindedJ
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class KatDataclassTestsKotlin : StringSpec() {
    private val kinded = KatDataclass1(0)

    init {
        "Values should be convertible" {
            KatDataclassKategoryShow.show(kinded) shouldBe KatDataclassKindedJShow.INSTANCE.show(kinded.toKindedJ())
        }
    }
}
