package arrow.fx

import arrow.Kind
import arrow.core.extensions.monoid
import arrow.fx.extensions.applicative
import arrow.fx.extensions.functor
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.monoid
import arrow.fx.typeclasses.Fiber
import arrow.fx.typeclasses.FiberOf
import arrow.fx.typeclasses.FiberPartialOf
import arrow.fx.typeclasses.fix
import arrow.test.UnitSpec
import arrow.test.generators.GenK
import arrow.test.laws.ApplicativeLaws
import arrow.test.laws.MonoidLaws
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen

class FiberTest : UnitSpec() {

  init {
    fun EQ(): Eq<FiberOf<IOPartialOf<Nothing>, Int>> = object : Eq<FiberOf<IOPartialOf<Nothing>, Int>> {
      override fun FiberOf<IOPartialOf<Nothing>, Int>.eqv(b: FiberOf<IOPartialOf<Nothing>, Int>): Boolean = EQ<Nothing, Int>().run {
        fix().join().eqv(b.fix().join())
      }
    }

    fun EQK() = object : EqK<FiberPartialOf<IOPartialOf<Nothing>>> {
      override fun <A> Kind<FiberPartialOf<IOPartialOf<Nothing>>, A>.eqK(other: Kind<FiberPartialOf<IOPartialOf<Nothing>>, A>, EQ: Eq<A>): Boolean =
        IO_EQ(EQ).run {
          this@eqK.fix().join().eqv(other.fix().join())
        }
    }

    fun <F> GENK(A: Applicative<F>) = object : GenK<FiberPartialOf<F>> {
      override fun <A> genK(gen: Gen<A>): Gen<Kind<FiberPartialOf<F>, A>> = gen.map {
        Fiber(A.just(it), A.just(Unit))
      }
    }

    testLaws(
      ApplicativeLaws.laws<FiberPartialOf<IOPartialOf<Nothing>>>(Fiber.applicative(IO.concurrent()), Fiber.functor(IO.concurrent()), GENK(IO.applicative()), EQK()),
      MonoidLaws.laws(Fiber.monoid(IO.concurrent<Nothing>(), Int.monoid()), Gen.int().map { i ->
        Fiber(IO.just(i), IO.unit)
      }, EQ())
    )
  }
}
