package kategory.effects

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import kategory.*
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(KTestJUnitRunner::class)
class ObservableKWTest : UnitSpec() {

    fun <T> EQ(): Eq<ObservableKWKind<T>> = object : Eq<ObservableKWKind<T>> {
        override fun eqv(a: ObservableKWKind<T>, b: ObservableKWKind<T>): Boolean =
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

    init {

        "instances can be resolved implicitly" {
            functor<ObservableKWHK>() shouldNotBe null
            applicative<ObservableKWHK>() shouldNotBe null
            monad<ObservableKWHK>() shouldNotBe null
            monadError<ObservableKWHK, Unit>() shouldNotBe null
            asyncContext<ObservableKWHK>() shouldNotBe null
            foldable<ObservableKWHK>() shouldNotBe null
            traverse<ObservableKWHK>() shouldNotBe null
        }

        testLaws(
            AsyncLaws.laws(ObservableKW.asyncContext(), ObservableKW.monadErrorFlat(), EQ(), EQ()),
            AsyncLaws.laws(ObservableKW.asyncContext(), ObservableKW.monadErrorConcat(), EQ(), EQ()),
            AsyncLaws.laws(ObservableKW.asyncContext(), ObservableKW.monadErrorSwitch(), EQ(), EQ()),

            FoldableLaws.laws(ObservableKW.foldable(), { ObservableKW.pure(it) }, Eq.any()),
            TraverseLaws.laws(ObservableKW.traverse(), ObservableKW.functor(), { ObservableKW.pure(it)  }, EQ())
        )

        "Multi-thread Observables finish correctly" {
            val value: Observable<Long> = ObservableKW.monadErrorFlat().bindingE {
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
            val value: Observable<Long> = ObservableKW.monadErrorFlat().bindingE {
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
            val value: Observable<Long> = ObservableKW.monadErrorFlat().bindingE {
                val a = Observable.timer(3, TimeUnit.SECONDS).k().bind()
                yields(a)
            }.value()
            val test: TestObserver<Long> = value.doOnSubscribe { subscription -> Observable.timer(1, TimeUnit.SECONDS).subscribe { subscription.dispose() } }.test()
            test.awaitTerminalEvent(5, TimeUnit.SECONDS)

            test.assertNotTerminated().assertNotComplete().assertNoErrors().assertNoValues()
        }
    }
}
