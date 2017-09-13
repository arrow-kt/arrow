package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class Function1Test : UnitSpec() {
    init {

        "instances can be resolved implicitly" {
            functor<Function1KindPartial<Int>>() shouldNotBe null
            applicative<Function1KindPartial<Int>>()  shouldNotBe null
            monad<Function1KindPartial<Int>>()  shouldNotBe null
            monadReader<Function1KindPartial<Int>, Int>()  shouldNotBe null
        }

        testLaws(MonadLaws.laws(Function1.monad<Int>(), object : Eq<Function1Kind<Int, Int>> {
            override fun eqv(a: Function1Kind<Int, Int>, b: Function1Kind<Int, Int>): Boolean =
                a(1) == b(1)
        }))
    }
}