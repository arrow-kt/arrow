package arrow

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class Function1Test : UnitSpec() {
    val EQ: Eq<Function1Kind<Int, Int>> = Eq { a, b ->
        a(1) == b(1)
    }

    init {

        "instances can be resolved implicitly" {
            functor<Function1KindPartial<Int>>() shouldNotBe null
            applicative<Function1KindPartial<Int>>()  shouldNotBe null
            monad<Function1KindPartial<Int>>()  shouldNotBe null
            monadReader<Function1KindPartial<Int>, Int>()  shouldNotBe null
        }

        testLaws(MonadLaws.laws(Function1.monad<Int>(), EQ))
    }
}