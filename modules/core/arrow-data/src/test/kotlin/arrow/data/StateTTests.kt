package arrow.data

import arrow.Kind
import arrow.core.ForId
import arrow.core.ForTry
import arrow.core.Try
import arrow.core.monad
import arrow.mtl.instances.StateTMonadStateInstance
import arrow.mtl.monadState
import arrow.test.UnitSpec
import arrow.test.laws.MonadCombineLaws
import arrow.test.laws.MonadStateLaws
import arrow.test.laws.SemigroupKLaws
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class StateTTests : UnitSpec() {

    val M: StateTMonadStateInstance<ForTry, Int> = StateT.monadState(Try.monad())

    val EQ: Eq<StateTOf<ForTry, Int, Int>> = Eq { a, b ->
        a.runM(1, Try.monad()) == b.runM(1, Try.monad())
    }

    val EQ_UNIT: Eq<StateTOf<ForTry, Int, Unit>> = Eq { a, b ->
        a.runM(1, Try.monad()) == b.runM(1, Try.monad())
    }

    val EQ_LIST: Eq<Kind<StateTPartialOf<ForListK, Int>, Int>> = Eq { a, b ->
        a.runM(1, ListK.monad()) == b.runM(1, ListK.monad())
    }

    init {

        "instances can be resolved implicitly" {
            functor<StateTPartialOf<ForId, Int>>() shouldNotBe null
            applicative<StateTPartialOf<ForId, Int>>() shouldNotBe null
            monad<StateTPartialOf<ForId, Int>>() shouldNotBe null
            monadState<StateTPartialOf<ForId, Int>, Int>() shouldNotBe null
            semigroupK<StateTPartialOf<ForNonEmptyList, ForNonEmptyList>>() shouldNotBe null
        }

        testLaws(
            MonadStateLaws.laws(M, EQ, EQ_UNIT),
            SemigroupKLaws.laws(
                StateT.semigroupK<ForListK, Int>(ListK.monad(), ListK.semigroupK()),
                StateT.applicative<ForListK, Int>(ListK.monad()),
                EQ_LIST),
            MonadCombineLaws.laws(StateT.monadCombine<ForListK, Int>(ListK.monadCombine()),
                { StateT.lift(ListK.pure(it), ListK.monad()) },
                { StateT.lift(ListK.pure({ s: Int -> s * 2 }), ListK.monad()) },
                EQ_LIST)
        )

    }
}
