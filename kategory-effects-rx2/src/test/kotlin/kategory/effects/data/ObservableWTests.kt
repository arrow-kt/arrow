package kategory.effects.data

import io.kotlintest.KTestJUnitRunner
import kategory.*
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class ObservableWTest : UnitSpec() {

    init {
        val EQ: Eq<ObservableWKind<Int>> = object : Eq<ObservableWKind<Int>> {
            override fun eqv(a: ObservableWKind<Int>, b: ObservableWKind<Int>): Boolean =
                    try {
                        a.ev().runObservable { }.blockingFirst() == b.ev().runObservable { }.blockingFirst()
                    } catch (throwable: Throwable) {
                        val errA = try {
                            a.ev().runObservable { }.blockingFirst()
                            throw IllegalArgumentException()
                        } catch (err: Throwable) {
                            err
                        }

                        val errB = try {
                            b.ev().runObservable { }.blockingFirst()
                            throw IllegalStateException()
                        } catch (err: Throwable) {
                            err
                        }

                        errA == errB
                    }
        }
        testLaws(AsyncLaws.laws(ObservableW.asyncContext(), ObservableW.monadErrorFlat(), EQ, EQ))
        testLaws(AsyncLaws.laws(ObservableW.asyncContext(), ObservableW.monadErrorConcat(), EQ, EQ))
        testLaws(AsyncLaws.laws(ObservableW.asyncContext(), ObservableW.monadErrorSwitch(), EQ, EQ))
    }
}