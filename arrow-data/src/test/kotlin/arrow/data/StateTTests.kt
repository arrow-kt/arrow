package arrow.data

import arrow.HK
import arrow.core.IdHK
import arrow.instances.applicative
import arrow.instances.monad
import arrow.instances.semigroupK
import arrow.mtl.instances.StateTMonadStateInstance
import arrow.mtl.instances.monadCombine
import arrow.mtl.instances.monadState
import arrow.mtl.monadState
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith
import arrow.test.UnitSpec
import arrow.test.laws.MonadCombineLaws
import arrow.test.laws.MonadStateLaws
import arrow.test.laws.SemigroupKLaws
import arrow.typeclasses.*

@RunWith(KTestJUnitRunner::class)
class StateTTests : UnitSpec() {

    val M: StateTMonadStateInstance<TryHK, Int> = StateT.monadState<TryHK, Int>(Try.monad())

    val EQ: Eq<StateTKind<TryHK, Int, Int>> = Eq { a, b ->
        a.runM(1, Try.monad()) == b.runM(1, Try.monad())
    }

    val EQ_UNIT: Eq<StateTKind<TryHK, Int, Unit>> = Eq { a, b ->
        a.runM(1, Try.monad()) == b.runM(1, Try.monad())
    }

    val EQ_LIST: Eq<HK<StateTKindPartial<ListKWHK, Int>, Int>> = Eq { a, b ->
        a.runM(1, ListKW.monad()) == b.runM(1, ListKW.monad())
    }

    init {

        "instances can be resolved implicitly" {
            functor<StateTKindPartial<IdHK, Int>>() shouldNotBe null
            applicative<StateTKindPartial<IdHK, Int>>() shouldNotBe null
            monad<StateTKindPartial<IdHK, Int>>() shouldNotBe null
            monadState<StateTKindPartial<IdHK, Int>, Int>() shouldNotBe null
            semigroupK<StateTKindPartial<NonEmptyListHK, NonEmptyListHK>>() shouldNotBe null
        }

        testLaws(
            MonadStateLaws.laws(M, EQ, EQ_UNIT),
            SemigroupKLaws.laws(
                StateT.semigroupK<ListKWHK, Int>(ListKW.monad(), ListKW.semigroupK()),
                StateT.applicative<ListKWHK, Int>(ListKW.monad()),
                EQ_LIST),
            MonadCombineLaws.laws(StateT.monadCombine<ListKWHK, Int>(ListKW.monadCombine()),
                { StateT.lift(ListKW.pure(it), ListKW.monad()) },
                { StateT.lift(ListKW.pure({ s: Int -> s * 2 }), ListKW.monad()) },
                EQ_LIST)
        )

    }
}
