package kategory

import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class Function1Test : UnitSpec() {
    init {
        testLaws(MonadLaws.laws(Function1.monad<Int>(), Eq { a, b ->
            a(1) == b(1)
        }))
    }
}