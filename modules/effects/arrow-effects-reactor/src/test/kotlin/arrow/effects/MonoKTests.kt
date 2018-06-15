package arrow.effects

import arrow.effecs.ForMonoK
import arrow.effecs.MonoK
import arrow.effecs.MonoKOf
import arrow.effecs.applicative
import arrow.effecs.applicativeError
import arrow.effecs.async
import arrow.effecs.effect
import arrow.effecs.functor
import arrow.effecs.monad
import arrow.effecs.monadDefer
import arrow.effecs.monadError
import arrow.effecs.value
import arrow.test.UnitSpec
import arrow.test.laws.ApplicativeErrorLaws
import arrow.test.laws.ApplicativeLaws
import arrow.test.laws.AsyncLaws
import arrow.test.laws.FunctorLaws
import arrow.test.laws.MonadErrorLaws
import arrow.test.laws.MonadLaws
import arrow.test.laws.MonadSuspendLaws
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith
import kotlin.math.E

@RunWith(KTestJUnitRunner::class)
class MonoKTest : UnitSpec() {

  fun <T> EQ(): Eq<MonoKOf<T>> = object : Eq<MonoKOf<T>> {
    override fun MonoKOf<T>.eqv(b: MonoKOf<T>): Boolean =
        try {
          this.value().block() == b.value().block()
        } catch (throwable: Throwable) {
          val errA = try {
            this.value().block()
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
    testLaws(
        FunctorLaws.laws(MonoK.functor(), { MonoK.just(it) }, EQ()),
        ApplicativeLaws.laws(MonoK.applicative(), EQ()),
        MonadLaws.laws(MonoK.monad(), EQ()),
        MonadErrorLaws.laws(MonoK.monadError(), EQ(), EQ(), EQ()),
        ApplicativeErrorLaws.laws(MonoK.applicativeError(), EQ(), EQ(), EQ()),
        MonadSuspendLaws.laws(MonoK.monadDefer(), EQ(), EQ(), EQ()),
        AsyncLaws.laws(MonoK.async(), EQ(), EQ(), EQ()),
        AsyncLaws.laws(MonoK.effect(), EQ(), EQ())
    )
  }

}