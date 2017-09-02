package kategory

import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class StateTTests : UnitSpec() {

    init {

        val instances = StateT.monadState<TryHK, Int>(Try.monad())

        testLaws(MonadStateLaws.laws(
                instances,
                Eq { a, b ->
                    a.runM(1) == b.runM(1)

                },
                Eq { a, b ->
                    a.runM(1) == b.runM(1)
                }))

        testLaws(SemigroupKLaws.laws(
                StateT.semigroupK<ListKWHK, Int>(ListKW.monad(), ListKW.semigroupK()),
                StateT.applicative<ListKWHK, Int>(ListKW.monad()),
                Eq { a, b ->
                    a.runM(1) == b.runM(1)
                }))
    }
}
