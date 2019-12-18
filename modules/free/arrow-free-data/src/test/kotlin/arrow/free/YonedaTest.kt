package arrow.free

import arrow.Kind
import arrow.core.ForId
import arrow.core.Id
import arrow.core.extensions.id.functor.functor
import arrow.core.fix
import arrow.free.extensions.yoneda.functor.functor
import arrow.test.UnitSpec
import arrow.test.generators.GenK
import arrow.test.laws.FunctorLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class YonedaTest : UnitSpec() {

  val EQK = object : EqK<YonedaPartialOf<ForId>> {
    override fun <A> Kind<YonedaPartialOf<ForId>, A>.eqK(other: Kind<YonedaPartialOf<ForId>, A>, EQ: Eq<A>): Boolean {
      return this.fix().lower() == other.fix().lower()
    }
  }

  fun genk() = object : GenK<Kind<ForYoneda, ForId>> {
    override fun <A> genK(gen: Gen<A>): Gen<Kind<Kind<ForYoneda, ForId>, A>> =
      gen.map {
        Yoneda(Id(it), Id.functor())
      }
  }

  init {
    testLaws(FunctorLaws.laws(Yoneda.functor(), genk(), EQK))

    "toCoyoneda should convert to an equivalent Coyoneda" {
      forAll { x: Int ->
        val op = Yoneda(Id(x.toString()), Id.functor())
        val toYoneda = op.toCoyoneda().lower(Id.functor()).fix()
        val expected = Coyoneda(Id(x), Int::toString).lower(Id.functor()).fix()

        expected == toYoneda
      }
    }
  }
}
