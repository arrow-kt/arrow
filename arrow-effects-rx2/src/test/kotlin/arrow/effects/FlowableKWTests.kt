package arrow.effects

import arrow.test.UnitSpec
import arrow.test.laws.AsyncLaws
import arrow.test.laws.FoldableLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.TestSubscriber
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(KTestJUnitRunner::class)
class FlowableKWTests : UnitSpec() {

    fun <T> EQ(): Eq<FlowableKWKind<T>> = object : Eq<FlowableKWKind<T>> {
        override fun eqv(a: FlowableKWKind<T>, b: FlowableKWKind<T>): Boolean =
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
            functor<FlowableKWHK>() shouldNotBe null
            applicative<FlowableKWHK>() shouldNotBe null
            monad<FlowableKWHK>() shouldNotBe null
            applicativeError<FlowableKWHK, Unit>() shouldNotBe null
            monadError<FlowableKWHK, Unit>() shouldNotBe null
            monadSuspend<FlowableKWHK>() shouldNotBe null
            async<FlowableKWHK>() shouldNotBe null
            effect<FlowableKWHK>() shouldNotBe null
            foldable<FlowableKWHK>() shouldNotBe null
            traverse<FlowableKWHK>() shouldNotBe null
        }

        testLaws(AsyncLaws.laws(FlowableKW.async(), EQ(), EQ()))
        testLaws(AsyncLaws.laws(FlowableKW.async(), EQ(), EQ()))
        testLaws(AsyncLaws.laws(FlowableKW.async(), EQ(), EQ()))

        testLaws(AsyncLaws.laws(FlowableKW.asyncDrop(), EQ(), EQ()))
        testLaws(AsyncLaws.laws(FlowableKW.asyncDrop(), EQ(), EQ()))
        testLaws(AsyncLaws.laws(FlowableKW.asyncDrop(), EQ(), EQ()))

        testLaws(AsyncLaws.laws(FlowableKW.asyncError(), EQ(), EQ()))
        testLaws(AsyncLaws.laws(FlowableKW.asyncError(), EQ(), EQ()))
        testLaws(AsyncLaws.laws(FlowableKW.asyncError(), EQ(), EQ()))

        testLaws(AsyncLaws.laws(FlowableKW.asyncLatest(), EQ(), EQ()))
        testLaws(AsyncLaws.laws(FlowableKW.asyncLatest(), EQ(), EQ()))
        testLaws(AsyncLaws.laws(FlowableKW.asyncLatest(), EQ(), EQ()))

        testLaws(AsyncLaws.laws(FlowableKW.asyncMissing(), EQ(), EQ()))
        testLaws(AsyncLaws.laws(FlowableKW.asyncMissing(), EQ(), EQ()))
        testLaws(AsyncLaws.laws(FlowableKW.asyncMissing(), EQ(), EQ()))

        testLaws(
                FoldableLaws.laws(FlowableKW.foldable(), { FlowableKW.pure(it) }, Eq.any()),
                TraverseLaws.laws(FlowableKW.traverse(), FlowableKW.functor(), { FlowableKW.pure(it) }, EQ())
        )

        "Multi-thread Flowables finish correctly" {
            val value: Flowable<Long> = FlowableKW.monadErrorFlat().bindingCatch {
                val a = Flowable.timer(2, TimeUnit.SECONDS).k().bind()
                a
            }.value()
            val test: TestSubscriber<Long> = value.test()
            test.awaitDone(5, TimeUnit.SECONDS)
            test.assertTerminated().assertComplete().assertNoErrors().assertValue(0)
        }

        "Multi-thread Observables should run on their required threads" {
            val originalThread: Thread = Thread.currentThread()
            var threadRef: Thread? = null
            val value: Flowable<Long> = FlowableKW.monadErrorFlat().bindingCatch {
                val a = Flowable.timer(2, TimeUnit.SECONDS, Schedulers.newThread()).k().bind()
                threadRef = Thread.currentThread()
                val b = Flowable.just(a).observeOn(Schedulers.newThread()).k().bind()
                b
            }.value()
            val test: TestSubscriber<Long> = value.test()
            val lastThread: Thread = test.awaitDone(5, TimeUnit.SECONDS).lastThread()
            val nextThread = (threadRef?.name ?: "")

            nextThread shouldNotBeElseLogged originalThread.name
            lastThread.name shouldNotBeElseLogged originalThread.name
            lastThread.name shouldNotBeElseLogged nextThread
        }

        "Flowable cancellation forces binding to cancel without completing too" {
            val value: Flowable<Long> = FlowableKW.monadErrorFlat().bindingCatch {
                val a = Flowable.timer(3, TimeUnit.SECONDS).k().bind()
                a
            }.value()
            val test: TestSubscriber<Long> = value.doOnSubscribe { subscription ->
                Flowable.timer(1, TimeUnit.SECONDS).subscribe {
                    subscription.cancel()
                }
            }.test()
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
