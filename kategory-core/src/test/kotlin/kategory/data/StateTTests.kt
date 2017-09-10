package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class StateTTests : UnitSpec() {

    init {

        "instances can be resolved implicitly" {
            functor<StateTKindPartial<IdHK, Int>>() shouldNotBe null
            applicative<StateTKindPartial<IdHK, Int>>() shouldNotBe null
            monad<StateTKindPartial<IdHK, Int>>() shouldNotBe null
            monadState<StateTKindPartial<IdHK, Int>, Int>() shouldNotBe null
            semigroupK<StateTKindPartial<NonEmptyListHK, NonEmptyListHK>>() shouldNotBe null
        }

        val m: StateTMonadStateInstance<TryHK, Int> = StateT.monadState<TryHK, Int>(Try.monad())

        testLaws(MonadStateLaws.laws(
                m,
                object : Eq<StateTKind<TryHK, Int, Int>> {
                    override fun eqv(a: StateTKind<TryHK, Int, Int>, b: StateTKind<TryHK, Int, Int>): Boolean =
                            a.runM(1, Try.monad()) == b.runM(1, Try.monad())

                },
                object : Eq<StateTKind<TryHK, Int, Unit>> {
                    override fun eqv(a: StateTKind<TryHK, Int, Unit>, b: StateTKind<TryHK, Int, Unit>): Boolean =
                            a.runM(1, Try.monad()) == b.runM(1, Try.monad())
                }))

        testLaws(SemigroupKLaws.laws(
                StateT.semigroupK<ListKWHK, Int>(ListKW.monad(), ListKW.semigroupK()),
                StateT.applicative<ListKWHK, Int>(ListKW.monad()),
                object : Eq<HK<StateTKindPartial<ListKWHK, Int>, Int>> {
                    override fun eqv(a: HK<StateTKindPartial<ListKWHK, Int>, Int>, b: HK<StateTKindPartial<ListKWHK, Int>, Int>): Boolean =
                            a.runM(1, ListKW.monad()) == b.runM(1, ListKW.monad())
                }))

        testLaws(MonadCombineLaws.laws(StateT.monadCombine<ListKWHK, Int>(ListKW.monadCombine()),
                { StateT.lift(ListKW.pure(it), ListKW.monad()) },
                { StateT.lift(ListKW.pure({ s: Int -> s * 2 }), ListKW.monad()) },
                object : Eq<HK<StateTKindPartial<ListKWHK, Int>, Int>> {
                    override fun eqv(a: HK<StateTKindPartial<ListKWHK, Int>, Int>, b: HK<StateTKindPartial<ListKWHK, Int>, Int>): Boolean =
                            a.runM(1) == b.runM(1)
                }))

    }
}
