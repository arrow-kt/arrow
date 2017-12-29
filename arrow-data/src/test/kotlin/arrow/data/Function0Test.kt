package arrow

import arrow.data.Function0
import arrow.data.k
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class Function0Test : UnitSpec() {
    val EQ: Eq<HK<Function0HK, Int>> = Eq { a, b ->
        a() == b()
    }

    init {
        testLaws(
            MonadLaws.laws(Function0.monad(), EQ),
            ComonadLaws.laws(Function0.comonad(), { { it }.k() }, EQ)
        )
    }
}
