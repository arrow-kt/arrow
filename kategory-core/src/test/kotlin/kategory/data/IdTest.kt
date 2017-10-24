package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import kategory.laws.EqLaws
import org.junit.runner.RunWith

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
        }

        testLaws(EqLaws.laws { Id(it) })
        testLaws(MonadLaws.laws(Id.monad(), Eq.any()))
        testLaws(TraverseLaws.laws(Id.traverse(), Id.functor(), ::Id))
        testLaws(ComonadLaws.laws(Id.comonad(), ::Id, Eq.any()))
    }
}
