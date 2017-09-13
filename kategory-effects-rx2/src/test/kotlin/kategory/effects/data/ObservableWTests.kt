package kategory.effects.data

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import kategory.*
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(KTestJUnitRunner::class)
class ObservableWTest : UnitSpec() {

    data class Wrap(val value: String = "")

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

        "Multi-thread Observables finish correctly" {
            val value: Observable<Long> = ObservableW.monadErrorFlat().bindingE {
                val a = Observable.timer(2, TimeUnit.SECONDS).k().bind()
                yields(a)
            }.value()

            val test: TestObserver<Long> = value.test()
            test.awaitDone(5, TimeUnit.SECONDS)
            test.assertTerminated().assertComplete().assertNoErrors().assertValue(0)
        }

        "Multi-thread Observables should run on their required threads" {
            val originalThread: Thread = Thread.currentThread()
            var nextThread: Thread? = null
            val value: Observable<Long> = ObservableW.monadErrorFlat().bindingE {
                val a = Observable.timer(2, TimeUnit.SECONDS).k().bind()
                nextThread = Thread.currentThread()
                val b = Observable.just(a).observeOn(Schedulers.io()).k().bind()
                yields(b)
            }.value()
            val test: TestObserver<Long> = value.test()
            val lastThread: Thread = test.awaitDone(5, TimeUnit.SECONDS).lastThread()
            nextThread shouldNotBe originalThread
            lastThread shouldNotBe originalThread
            lastThread shouldNotBe nextThread
        }

        "Observable cancellation forces binding to cancel without completing too" {
            val value: Observable<Long> = ObservableW.monadErrorFlat().bindingE {
                val a = Observable.timer(3, TimeUnit.SECONDS).k().bind()
                yields(a)
            }.value()
            val test: TestObserver<Long> = value.doOnSubscribe { subscription -> Observable.timer(1, TimeUnit.SECONDS).subscribe { subscription.dispose() } }.test()
            test.awaitTerminalEvent(10, TimeUnit.SECONDS)

            test.assertNotTerminated().assertNotComplete().assertNoErrors().assertNoValues()
        }
    }
}