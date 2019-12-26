package arrow.test.laws

import arrow.Kind
import arrow.Kind2
import arrow.core.ForId
import arrow.core.Id
import arrow.core.extensions.id.applicative.applicative
import arrow.core.extensions.id.comonad.extract
import arrow.test.generators.functionAToB
import arrow.test.generators.intSmall
import arrow.typeclasses.Bitraverse
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object BitraverseLaws {
  fun <F> laws(BT: Bitraverse<F>, cf: (Int) -> Kind2<F, Int, Int>, EQ: Eq<Kind2<F, Int, Int>>): List<Law> =
    BifoldableLaws.laws(BT, Gen.intSmall().map(cf), Eq.any()) + listOf(Law("Bitraverse Laws: Identity") { BT.identityBitraverse(BT, cf, EQ) })

  fun <F> Bitraverse<F>.identityBitraverse(BT: Bitraverse<F>, cf: (Int) -> Kind2<F, Int, Int>, EQ: Eq<Kind2<F, Int, Int>>) =
    Id.applicative().run {
      val idApp = this
      forAll(Gen.functionAToB<Int, Kind<ForId, Int>>(Gen.intSmall().map(::Id)),
        Gen.functionAToB<Int, Kind<ForId, Int>>(Gen.intSmall().map(::Id)), Gen.intSmall().map(cf)) { f, g, fa ->
        fa.bitraverse(idApp, f, g).extract().equalUnderTheLaw(BT.run { fa.bimap({ f(it).extract() }, { g(it).extract() }) }, EQ)
      }
    }
}
