package arrow.weak

import arrow.test.UnitSpec
import arrow.test.laws.EqLaws
import arrow.test.laws.MonadLaws
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class WeakTest : UnitSpec() {

    private val EQ: Eq<WeakOf<Int>> = Eq { a, b ->
        a.fix().option() == b.fix().option()
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
            EqLaws.laws { Weak(it) },
            MonadLaws.laws(Weak.monad(), EQ)
        )
    }
}
