package arrow.core

import arrow.Kind
import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.setk.eq.eq
import arrow.core.extensions.setk.eqK.eqK
import arrow.core.extensions.setk.foldable.foldable
import arrow.core.extensions.setk.hash.hash
import arrow.core.extensions.setk.monoid.monoid
import arrow.core.extensions.setk.monoidK.monoidK
import arrow.core.extensions.setk.monoidal.monoidal
import arrow.core.extensions.setk.semigroupK.semigroupK
import arrow.core.extensions.setk.show.show
import arrow.core.extensions.tuple2.eq.eq
import arrow.test.UnitSpec
import arrow.test.generators.genK
import arrow.test.generators.genSetK
import arrow.test.laws.EqKLaws
import arrow.test.laws.FoldableLaws
import arrow.test.laws.HashLaws
import arrow.test.laws.MonoidKLaws
import arrow.test.laws.MonoidLaws
import arrow.test.laws.MonoidalLaws
import arrow.test.laws.SemigroupKLaws
import arrow.test.laws.ShowLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

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
      ShowLaws.laws(SetK.show(), EQ, Gen.genSetK(Gen.int())),
      MonoidLaws.laws(SetK.monoid(), Gen.genSetK(Gen.int()), EQ),
      SemigroupKLaws.laws(SetK.semigroupK(), SetK.genK(), SetK.eqK()),
      MonoidalLaws.laws(SetK.monoidal(),
        SetK.genK(10),
        SetK.eqK(),
        this::bijection
      ),
      MonoidKLaws.laws(SetK.monoidK(), SetK.genK(), SetK.eqK()),
      FoldableLaws.laws(SetK.foldable(), SetK.genK()),
      HashLaws.laws(SetK.hash(Int.hash()), SetK.eq(Int.eq()), Gen.genSetK(Gen.int())),
      EqKLaws.laws(
        SetK.eqK(),
        SetK.genK()
      )
    )
  }

  private fun bijection(from: Kind<ForSetK, Tuple2<Tuple2<Int, Int>, Int>>): SetK<Tuple2<Int, Tuple2<Int, Int>>> =
    from.fix().map { Tuple2(it.a.a, Tuple2(it.a.b, it.b)) }.toSet().k()
}
