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
import arrow.core.extensions.show
import arrow.core.extensions.tuple2.eq.eq
import arrow.core.test.UnitSpec
import arrow.core.test.generators.genK
import arrow.core.test.generators.genSetK
import arrow.core.test.laws.EqKLaws
import arrow.core.test.laws.FoldableLaws
import arrow.core.test.laws.HashLaws
import arrow.core.test.laws.MonoidKLaws
import arrow.core.test.laws.MonoidLaws
import arrow.core.test.laws.MonoidalLaws
import arrow.core.test.laws.SemigroupKLaws
import arrow.core.test.laws.ShowLaws
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
      ShowLaws.laws(SetK.show(Int.show()), EQ, Gen.genSetK(Gen.int())),
      MonoidLaws.laws(SetK.monoid(), Gen.genSetK(Gen.int()), EQ),
      SemigroupKLaws.laws(SetK.semigroupK(), SetK.genK(), SetK.eqK()),
      MonoidalLaws.laws(
        SetK.monoidal(),
        SetK.genK(10),
        SetK.eqK(),
        this::bijection
      ),
      MonoidKLaws.laws(SetK.monoidK(), SetK.genK(), SetK.eqK()),
      FoldableLaws.laws(SetK.foldable(), SetK.genK()),
      HashLaws.laws(SetK.hash(Int.hash()), Gen.genSetK(Gen.int()), SetK.eq(Int.eq())),
      EqKLaws.laws(
        SetK.eqK(),
        SetK.genK()
      )
    )
  }

  private fun bijection(from: Kind<ForSetK, Tuple2<Tuple2<Int, Int>, Int>>): SetK<Tuple2<Int, Tuple2<Int, Int>>> =
    from.fix().map { Tuple2(it.a.a, Tuple2(it.a.b, it.b)) }.toSet().k()
}
