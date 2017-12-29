package arrow.data

import arrow.HK
import arrow.instances.comonad
import arrow.instances.monad
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith
import arrow.test.UnitSpec
import arrow.test.laws.ComonadLaws
import arrow.test.laws.MonadLaws
import arrow.typeclasses.Eq

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
