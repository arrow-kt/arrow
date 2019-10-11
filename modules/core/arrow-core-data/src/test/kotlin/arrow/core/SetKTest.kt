package arrow.core

import arrow.Kind
import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.tuple2.eq.eq
import arrow.core.extensions.setk.eq.eq
import arrow.core.extensions.setk.foldable.foldable
import arrow.core.extensions.setk.hash.hash
import arrow.core.extensions.setk.monoidK.monoidK
import arrow.core.extensions.setk.monoidal.monoidal
import arrow.core.extensions.setk.semigroupK.semigroupK
import arrow.core.extensions.setk.show.show
import arrow.test.UnitSpec
import arrow.test.laws.FoldableLaws
import arrow.test.laws.HashLaws
import arrow.test.laws.MonoidKLaws
import arrow.test.laws.MonoidalLaws
import arrow.test.laws.SemigroupKLaws
import arrow.test.laws.ShowLaws
import arrow.typeclasses.Eq

class SetKTest : UnitSpec() {

  val associativeSemigroupalEq: Eq<SetKOf<Tuple2<Int, Tuple2<Int, Int>>>> = object : Eq<SetKOf<Tuple2<Int, Tuple2<Int, Int>>>> {
    override fun SetKOf<Tuple2<Int, Tuple2<Int, Int>>>.eqv(b: SetKOf<Tuple2<Int, Tuple2<Int, Int>>>): Boolean {
      return SetK.eq(Tuple2.eq(Int.eq(), Tuple2.eq(Int.eq(), Int.eq()))).run {
        this@eqv.fix().eqv(b.fix())
      }
    }
  }

  init {

    val EQ = SetK.eq(Int.eq())

    testLaws(
      ShowLaws.laws(SetK.show(), EQ) { SetK.just(it) },
      SemigroupKLaws.laws(SetK.semigroupK(), { SetK.just(it) }, Eq.any()),
      MonoidalLaws.laws(SetK.monoidal(), { SetK.just(it) }, Eq.any(), this::bijection, associativeSemigroupalEq),
      MonoidKLaws.laws(SetK.monoidK(), { SetK.just(it) }, Eq.any()),
      FoldableLaws.laws(SetK.foldable(), { SetK.just(it) }, Eq.any()),
      HashLaws.laws(SetK.hash(Int.hash()), SetK.eq(Int.eq())) { SetK.just(it) }
    )
  }

  private fun bijection(from: Kind<ForSetK, Tuple2<Tuple2<Int, Int>, Int>>): SetK<Tuple2<Int, Tuple2<Int, Int>>> =
    from.fix().map { Tuple2(it.a.a, Tuple2(it.a.b, it.b)) }.toSet().k()
}
