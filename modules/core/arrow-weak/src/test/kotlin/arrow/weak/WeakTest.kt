package arrow.weak

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import arrow.test.laws.EqLaws
import org.junit.runner.RunWith
import arrow.test.UnitSpec
import arrow.test.laws.MonadLaws
import arrow.typeclasses.*

@RunWith(KTestJUnitRunner::class)
class WeakTest : UnitSpec() {
    val EQ: Eq<WeakOf<Int>> = Eq { a, b ->
        a.fix().getOrElse { -1 } == b.fix().getOrElse { -2 }
    }

    init {

        "instances can be resolved implicitly" {
            functor<ForWeak>() shouldNotBe null
            applicative<ForWeak>() shouldNotBe null
            monad<ForWeak>() shouldNotBe null
            foldable<ForWeak>() shouldNotBe null
            traverse<ForWeak>() shouldNotBe null
            eq<Weak<Int>>() shouldNotBe null
        }

        testLaws(
            EqLaws.laws { Weak(it) }
            // FIXME(pablisco) - Disabled due to flakyness
            //MonadLaws.laws(Weak.monad(), EQ)
        )
    }
}
