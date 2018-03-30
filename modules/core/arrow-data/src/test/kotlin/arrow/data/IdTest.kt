package arrow.data

import arrow.core.*
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class IdTest : UnitSpec() {
    init {

        testLaws(
                EqLaws.laws(Id.eq(Eq.any())) { Id(it) },
                ShowLaws.laws(Id.show(), Eq.any()) { Id(it) },
                MonadLaws.laws(Id.monad(), Eq.any()),
                TraverseLaws.laws(Id.traverse(), Id.functor(), ::Id, Eq.any()),
                ComonadLaws.laws(Id.comonad(), ::Id, Eq.any())
        )
    }
}
