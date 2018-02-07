package arrow.data

import arrow.Kind
import arrow.core.ForId
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

    val M: StateTMonadStateInstance<ForTry, Int> = StateT.monadState<ForTry, Int>(Try.monad())

    val EQ: Eq<StateTKind<ForTry, Int, Int>> = Eq { a, b ->
        a.runM(1, Try.monad()) == b.runM(1, Try.monad())
    }

    val EQ_UNIT: Eq<StateTKind<ForTry, Int, Unit>> = Eq { a, b ->
        a.runM(1, Try.monad()) == b.runM(1, Try.monad())
    }

    val EQ_LIST: Eq<Kind<StateTKindPartial<ForListKW, Int>, Int>> = Eq { a, b ->
        a.runM(1, ListKW.monad()) == b.runM(1, ListKW.monad())
    }

    init {

        "instances can be resolved implicitly" {
            functor<StateTKindPartial<ForId, Int>>() shouldNotBe null
            applicative<StateTKindPartial<ForId, Int>>() shouldNotBe null
            monad<StateTKindPartial<ForId, Int>>() shouldNotBe null
            monadState<StateTKindPartial<ForId, Int>, Int>() shouldNotBe null
            semigroupK<StateTKindPartial<ForNonEmptyList, ForNonEmptyList>>() shouldNotBe null
        }

        testLaws(
            MonadStateLaws.laws(M, EQ, EQ_UNIT),
            SemigroupKLaws.laws(
                StateT.semigroupK<ForListKW, Int>(ListKW.monad(), ListKW.semigroupK()),
                StateT.applicative<ForListKW, Int>(ListKW.monad()),
                EQ_LIST),
            MonadCombineLaws.laws(StateT.monadCombine<ForListKW, Int>(ListKW.monadCombine()),
                { StateT.lift(ListKW.pure(it), ListKW.monad()) },
                { StateT.lift(ListKW.pure({ s: Int -> s * 2 }), ListKW.monad()) },
                EQ_LIST)
        )

    }
}
