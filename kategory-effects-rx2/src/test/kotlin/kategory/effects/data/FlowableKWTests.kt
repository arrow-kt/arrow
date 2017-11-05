package kategory.effects

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.Gen
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.TestSubscriber
import kategory.*
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
            monadError<FlowableKWHK, Unit>() shouldNotBe null
            asyncContext<FlowableKWHK>() shouldNotBe null
            foldable<FlowableKWHK>() shouldNotBe null
            traverse<FlowableKWHK>() shouldNotBe null
        }

        testLaws(AsyncLaws.laws(FlowableKW.asyncContext(), FlowableKW.monadErrorFlat(), EQ(), EQ()))
        testLaws(AsyncLaws.laws(FlowableKW.asyncContext(), FlowableKW.monadErrorConcat(), EQ(), EQ()))
        testLaws(AsyncLaws.laws(FlowableKW.asyncContext(), FlowableKW.monadErrorSwitch(), EQ(), EQ()))

        testLaws(AsyncLaws.laws(FlowableKW.asyncContextDrop(), FlowableKW.monadError(), EQ(), EQ()))
        testLaws(AsyncLaws.laws(FlowableKW.asyncContextDrop(), FlowableKW.monadErrorConcat(), EQ(), EQ()))
        testLaws(AsyncLaws.laws(FlowableKW.asyncContextDrop(), FlowableKW.monadErrorSwitch(), EQ(), EQ()))

        testLaws(AsyncLaws.laws(FlowableKW.asyncContextError(), FlowableKW.monadError(), EQ(), EQ()))
        testLaws(AsyncLaws.laws(FlowableKW.asyncContextError(), FlowableKW.monadErrorConcat(), EQ(), EQ()))
        testLaws(AsyncLaws.laws(FlowableKW.asyncContextError(), FlowableKW.monadErrorSwitch(), EQ(), EQ()))

        testLaws(AsyncLaws.laws(FlowableKW.asyncContextLatest(), FlowableKW.monadError(), EQ(), EQ()))
        testLaws(AsyncLaws.laws(FlowableKW.asyncContextLatest(), FlowableKW.monadErrorConcat(), EQ(), EQ()))
        testLaws(AsyncLaws.laws(FlowableKW.asyncContextLatest(), FlowableKW.monadErrorSwitch(), EQ(), EQ()))

        testLaws(AsyncLaws.laws(FlowableKW.asyncContextMissing(), FlowableKW.monadError(), EQ(), EQ()))
        testLaws(AsyncLaws.laws(FlowableKW.asyncContextMissing(), FlowableKW.monadErrorConcat(), EQ(), EQ()))
        testLaws(AsyncLaws.laws(FlowableKW.asyncContextMissing(), FlowableKW.monadErrorSwitch(), EQ(), EQ()))

        testLaws(FoldableLaws.laws(FlowableKW.foldable(), { FlowableKW.pure(it) }, Eq.any()))
        testLaws(TraverseLaws.laws(FlowableKW.traverse(), FlowableKW.functor(), { FlowableKW.pure(it)  }, EQ()))

        "Multi-thread Flowables finish correctly" {
            val value: Flowable<Long> = FlowableKW.monadErrorFlat().bindingE {
                val a = Flowable.timer(2, TimeUnit.SECONDS).k().bind()
                yields(a)
            }.value()
            val test: TestSubscriber<Long> = value.test()
            test.awaitDone(5, TimeUnit.SECONDS)
            test.assertTerminated().assertComplete().assertNoErrors().assertValue(0)
        }

        "Multi-thread Observables should run on their required threads" {
            val originalThread: Thread = Thread.currentThread()
            var nextThread: Thread? = null
            val value: Flowable<Long> = FlowableKW.monadErrorFlat().bindingE {
                val a = Flowable.timer(2, TimeUnit.SECONDS).k().bind()
                nextThread = Thread.currentThread()
                val b = Flowable.just(a).observeOn(Schedulers.io()).k().bind()
                yields(b)
            }.value()
            val test: TestSubscriber<Long> = value.test()
            val lastThread: Thread = test.awaitDone(5, TimeUnit.SECONDS).lastThread()
            nextThread shouldNotBe originalThread
            lastThread shouldNotBe originalThread
            lastThread shouldNotBe nextThread
        }

        "Flowable cancellation forces binding to cancel without completing too" {
            val value: Flowable<Long> = FlowableKW.monadErrorFlat().bindingE {
                val a = Flowable.timer(3, TimeUnit.SECONDS).k().bind()
                yields(a)
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
}
