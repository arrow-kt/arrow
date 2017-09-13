package kategory.effects.data

import io.kotlintest.KTestJUnitRunner
import kategory.*
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class ObservableWTest : UnitSpec() {

    init {
        fun <T> EQ(): Eq<ObservableWKind<T>> = object : Eq<ObservableWKind<T>> {
            override fun eqv(a: ObservableWKind<T>, b: ObservableWKind<T>): Boolean =
                    try {
                        a.value().blockingFirst() == b.value().blockingFirst()
                    } catch (throwable: Throwable) {
                        val errA = try {
                            a.value().blockingFirst()
                            throw IllegalArgumentException()
                        } catch (err: Throwable) {
                            err
                        }

                        val errB = try {
                            b.value().blockingFirst()
                            throw IllegalStateException()
                        } catch (err: Throwable) {
                            err
                        }

                        errA == errB
                    }
        }
        testLaws(AsyncLaws.laws(ObservableW.asyncContext(), ObservableW.monadErrorFlat(), EQ(), EQ()))
        testLaws(AsyncLaws.laws(ObservableW.asyncContext(), ObservableW.monadErrorConcat(), EQ(), EQ()))
        testLaws(AsyncLaws.laws(ObservableW.asyncContext(), ObservableW.monadErrorSwitch(), EQ(), EQ()))
    }
}