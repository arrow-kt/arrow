package arrow.effects

import arrow.effecs.ForMonoK
import arrow.effecs.MonoK
import arrow.effecs.MonoKOf
import arrow.effecs.value
import arrow.test.UnitSpec
import arrow.test.laws.AsyncLaws
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class MonoKTest : UnitSpec() {

    fun <T> EQ(): Eq<MonoKOf<T>> = object : Eq<MonoKOf<T>> {
        override fun eqv(a: MonoKOf<T>, b: MonoKOf<T>): Boolean =
                try {
                    a.value().block() == b.value().block()
                } catch (throwable: Throwable) {
                    val errA = try {
                        a.value().block()
                        throw IllegalArgumentException()
                    } catch (err: Throwable) {
                        err
                    }

                    val errB = try {
                        b.value().block()
                        throw IllegalStateException()
                    } catch (err: Throwable) {
                        err
                    }

                    errA == errB
                }
    }

    init {

        "instances can be resolved implicitly" {
            functor<ForMonoK>() shouldNotBe null
            applicative<ForMonoK>() shouldNotBe null
            monad<ForMonoK>() shouldNotBe null
            applicativeError<ForMonoK, Unit>() shouldNotBe null
            monadError<ForMonoK, Unit>() shouldNotBe null
            monadSuspend<ForMonoK>() shouldNotBe null
            async<ForMonoK>() shouldNotBe null
            effect<ForMonoK>() shouldNotBe null
        }

        testLaws(
                AsyncLaws.laws(MonoK.async(), EQ(), EQ())
        )
    }

}