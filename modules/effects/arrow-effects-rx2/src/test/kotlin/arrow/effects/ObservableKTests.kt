package arrow.effects

import arrow.test.UnitSpec
import arrow.test.laws.AsyncLaws
import arrow.test.laws.FoldableLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.bindingCatch
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(KTestJUnitRunner::class)
class ObservableKTest : UnitSpec() {

    fun <T> EQ(): Eq<ObservableKOf<T>> = object : Eq<ObservableKOf<T>> {
        override fun ObservableKOf<T>.eqv(b: ObservableKOf<T>): Boolean =
                try {
                    this.value().blockingFirst() == b.value().blockingFirst()
                } catch (throwable: Throwable) {
                    val errA = try {
                        this.value().blockingFirst()
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

    init {

        testLaws(AsyncLaws.laws(ObservableK.async(), EQ(), EQ()))
        // FIXME(paco) #691
        //testLaws(AsyncLaws.laws(ObservableK.async(), EQ(), EQ()))
        //testLaws(AsyncLaws.laws(ObservableK.async(), EQ(), EQ()))

        testLaws(
                FoldableLaws.laws(ObservableK.foldable(), { ObservableK.pure(it) }, Eq.any()),
                TraverseLaws.laws(ObservableK.traverse(), ObservableK.functor(), { ObservableK.pure(it) }, EQ())
        )

        "Multi-thread Observables finish correctly" {
            val value: Observable<Long> = ObservableK.monadErrorFlat().bindingCatch {
                val a = Observable.timer(2, TimeUnit.SECONDS).k().bind()
                a
            }.value()

            val test: TestObserver<Long> = value.test()
            test.awaitDone(5, TimeUnit.SECONDS)
            test.assertTerminated().assertComplete().assertNoErrors().assertValue(0)
        }

        "Multi-thread Observables should run on their required threads" {
            val originalThread: Thread = Thread.currentThread()
            var threadRef: Thread? = null
            val value: Observable<Long> = ObservableK.monadErrorFlat().bindingCatch {
                val a = Observable.timer(2, TimeUnit.SECONDS, Schedulers.newThread()).k().bind()
                threadRef = Thread.currentThread()
                val b = Observable.just(a).observeOn(Schedulers.io()).k().bind()
                b
            }.value()
            val test: TestObserver<Long> = value.test()
            val lastThread: Thread = test.awaitDone(5, TimeUnit.SECONDS).lastThread()
            val nextThread = (threadRef?.name ?: "")

            nextThread shouldNotBeElseLogged originalThread.name
            lastThread.name shouldNotBeElseLogged originalThread.name
            lastThread.name shouldNotBeElseLogged nextThread
        }

        "Observable cancellation forces binding to cancel without completing too" {
            val value: Observable<Long> = ObservableK.monadErrorFlat().bindingCatch {
                val a = Observable.timer(3, TimeUnit.SECONDS).k().bind()
                a
            }.value()
            val test: TestObserver<Long> = value.doOnSubscribe { subscription -> Observable.timer(1, TimeUnit.SECONDS).subscribe { subscription.dispose() } }.test()
            test.awaitTerminalEvent(5, TimeUnit.SECONDS)

            test.assertNotTerminated().assertNotComplete().assertNoErrors().assertNoValues()
        }
    }

    // FIXME(paco): remove if this hasn't triggered in a while - 26 Jan 18
    private infix fun String.shouldNotBeElseLogged(b: String) {
        try {
            this shouldNotBe b
        } catch (t: Throwable) {
            println("$this  <---->  $b")
            throw t
        }
    }
}
