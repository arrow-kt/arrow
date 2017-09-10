package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class Function1Test : UnitSpec() {
    init {
        testLaws(MonadLaws.laws(Function1.monad<Int>(), object : Eq<Function1Kind<Int, Int>> {
            override fun eqv(a: Function1Kind<Int, Int>, b: Function1Kind<Int, Int>): Boolean =
                a(1) == b(1)
        }))
    }
}