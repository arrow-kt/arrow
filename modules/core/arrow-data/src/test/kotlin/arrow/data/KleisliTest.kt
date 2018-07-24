package arrow.data

import arrow.core.*
import arrow.instances.ForKleisli
import arrow.test.UnitSpec
import arrow.test.laws.MonadErrorLaws
import arrow.typeclasses.Eq
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class KleisliTest : UnitSpec() {
  private fun <A> EQ(): Eq<KleisliOf<ForTry, Int, A>> = Eq { a, b ->
    a.fix().run(1) == b.fix().run(1)
  }

  init {

    ForKleisli<ForTry, Int, Throwable>(Try.monadError()) extensions {
      testLaws(MonadErrorLaws.laws(this, EQ(), EQ()))
    }

    "andThen should continue sequence" {
      val kleisli: Kleisli<ForId, Int, Int> = Kleisli({ a: Int -> Id(a) })

      kleisli.andThen(Id.monad(), Id(3)).run(0).fix().value shouldBe 3

      kleisli.andThen(Id.monad(), { b -> Id(b + 1) }).run(0).fix().value shouldBe 1
    }
  }
}
