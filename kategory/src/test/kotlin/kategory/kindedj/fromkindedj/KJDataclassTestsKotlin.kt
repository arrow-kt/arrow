package kategory.fromkotlin

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import kategory.fromKindedJ
import kategory.kindedj.fromkindedj.KJDataclassHK.*
import kategory.kindedj.fromkindedj.KJDataclassKindJShow
import org.junit.runner.RunWith


@RunWith(KTestJUnitRunner::class)
class KJDataclassTestsKotlin : StringSpec() {

    private val kinded = KJDataclass1(0)

    private val kinded2 = KJDataclass2<Int, String>(0)

    private val kinded3 = KJDataclass3<Int, String, Boolean>(0)

    private val kinded4 = KJDataclass4<Int, String, Boolean, Long>(0)

    private val kinded5 = KJDataclass5<Int, String, Boolean, Long, Float>(0)

    init {
        "Values should be convertible" {
            KJDataclassKindJShow.INSTANCE.show(kinded) shouldBe KJDataclassKategoryShow.show(kinded.fromKindedJ())
        }
    }
}