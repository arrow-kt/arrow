package arrow.free

import arrow.Kind
import arrow.core.ForId
import arrow.core.Id
import arrow.core.extensions.id.functor.functor
import arrow.core.fix
import arrow.free.extensions.yoneda.functor.functor
import arrow.test.UnitSpec
import arrow.test.laws.FunctorLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class YonedaTest : UnitSpec() {

  val EQ = Eq<YonedaOf<ForId, Int>> { a, b ->
    a.fix().lower() == b.fix().lower()
  }

  init {

    val f: (Int) -> Yoneda<ForId, Int> = { Yoneda(Id(it), Id.functor()) }
    val g = Gen.int().map(f) as Gen<Kind<Kind<ForYoneda, ForId>, Int>>

    testLaws(FunctorLaws.laws(Yoneda.functor(), g, EQ))

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
