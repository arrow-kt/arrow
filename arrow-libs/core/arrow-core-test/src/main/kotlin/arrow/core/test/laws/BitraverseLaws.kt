package arrow.core.test.laws

import arrow.Kind
import arrow.Kind2
import arrow.core.extensions.eq
import arrow.core.test.generators.GenK2
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.intSmall
import arrow.core.test.laws.internal.Id
import arrow.core.test.laws.internal.fix
import arrow.core.test.laws.internal.idApplicative
import arrow.typeclasses.Bitraverse
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK2
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object BitraverseLaws {

  fun <F> laws(BT: Bitraverse<F>, GENK: GenK2<F>, EQK: EqK2<F>): List<Law> {

    val G = GENK.genK(Gen.int(), Gen.int())
    val EQ = EQK.liftEq(Int.eq(), Int.eq())

    return BifoldableLaws.laws(BT, GENK) + listOf(Law("Bitraverse Laws: Identity") { BT.identityBitraverse(BT, G, EQ) })
  }

  fun <F> Bitraverse<F>.identityBitraverse(BT: Bitraverse<F>, GEN: Gen<Kind2<F, Int, Int>>, EQ: Eq<Kind2<F, Int, Int>>) =
    idApplicative.run {
      val idApp = this
      forAll(Gen.functionAToB<Int, Kind<Id.Companion, Int>>(Gen.intSmall().map(::Id)),
        Gen.functionAToB<Int, Kind<Id.Companion, Int>>(Gen.intSmall().map(::Id)), GEN) { f, g, fa ->
        fa.bitraverse(idApplicative, f, g).fix().value.equalUnderTheLaw(BT.run { fa.bimap({ f(it).fix().value }, { g(it).fix().value }) }, EQ)
      }
    }
}
