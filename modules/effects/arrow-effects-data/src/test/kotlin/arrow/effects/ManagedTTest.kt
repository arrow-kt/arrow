package arrow.effects

import arrow.Kind
import arrow.core.extensions.monoid
import arrow.effects.extensions.io.bracket.bracket
import arrow.effects.extensions.managedt.monad.monad
import arrow.effects.extensions.managedt.monoid.monoid
import arrow.effects.typeclasses.seconds
import arrow.test.UnitSpec
import arrow.test.laws.MonadLaws
import arrow.test.laws.MonoidLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class ManagedTTest : UnitSpec() {
  init {

    val EQ = Eq<Kind<ManagedTPartialOf<ForIO, Throwable>, Int>> { a, b ->
      a.fix().invoke { IO.just(1) }.fix().unsafeRunTimed(60.seconds) ==
        b.fix().invoke { IO.just(1) }.fix().unsafeRunTimed(60.seconds)
    }

    testLaws(
      MonadLaws.laws(ManagedT.monad(IO.bracket()), EQ),
      MonoidLaws.laws(ManagedT.monoid(Int.monoid(), IO.bracket()), Gen.int().map { ManagedT.just(it, IO.bracket()) }, EQ)
    )

  }
}