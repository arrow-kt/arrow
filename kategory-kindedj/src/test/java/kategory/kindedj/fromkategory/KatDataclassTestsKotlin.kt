package kategory.fromkotlin

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import kategory.kindedj.*
import kategory.kindedj.fromkategory.KatDataclassKindedJShow
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class KatDataclassTestsKotlin : StringSpec() {
    private val kinded = KatDataclass1(0)

    private val kinded2 = KatDataclass2<Int, String>(0)

    private val kinded3 = KatDataclass3<Int, String, Boolean>(0)

    private val kinded4 = KatDataclass4<Int, String, Boolean, Long>(0)

    private val kinded5 = KatDataclass5<Int, String, Boolean, Long, Float>(0)

    init {
        "Values should be convertible" {
            KatDataclassKategoryShow.show(kinded) shouldBe KatDataclassKindedJShow.INSTANCE.show(kinded.toKindedJ())
        }
    }
}