package arrow.data

import arrow.core.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.*

@RunWith(KTestJUnitRunner::class)
class IdTest : UnitSpec() {
    init {

        "instances can be resolved implicitly" {
            functor<IdKind<Int>>() shouldNotBe null
            applicative<IdKind<Int>>() shouldNotBe null
            monad<IdKind<Int>>() shouldNotBe null
            foldable<IdKind<Int>>() shouldNotBe null
            traverse<IdKind<Int>>() shouldNotBe null
            eq<Id<Int>>() shouldNotBe null
            show<Id<Int>>() shouldNotBe null
        }

        testLaws(
            EqLaws.laws { Id(it) },
            ShowLaws.laws { Id(it) },
            MonadLaws.laws(Id.monad(), Eq.any()),
            TraverseLaws.laws(Id.traverse(), Id.functor(), ::Id),
            ComonadLaws.laws(Id.comonad(), ::Id, Eq.any())
        )
    }
}
