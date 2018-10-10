package arrow.effects

import arrow.Kind
import arrow.core.Option
import arrow.core.eq
import arrow.data.Kleisli
import arrow.data.KleisliPartialOf
import arrow.data.fix
import arrow.data.monadDefer
import arrow.effects.typeclasses.seconds
import arrow.test.UnitSpec
import arrow.test.laws.MonadDeferLaws
import io.kotlintest.KTestJUnitRunner
import arrow.typeclasses.Eq
import org.junit.runner.RunWith

/**
 *
 */

@RunWith(KTestJUnitRunner::class)
class KleisliTest: UnitSpec() {

    val IO_DEFER = IO.monadDefer()

    val EQ_OPTION = Option.eq(Eq.any())

    fun <A> EQ(): Eq<Kind<KleisliPartialOf<ForIO, Unit>, A>> {
        return Eq {a, b ->
            EQ_OPTION.run {
                a.fix().run(Unit).fix().attempt().unsafeRunTimed(60.seconds).eqv(b.fix().run(Unit).fix().attempt().unsafeRunTimed(60.seconds))
            }
        }
    }

    init {

        testLaws(MonadDeferLaws.laws(Kleisli.monadDefer<ForIO, Unit>(IO_DEFER), EQ(), EQ()))

    }

}