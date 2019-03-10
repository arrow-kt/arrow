package arrow.data

import arrow.Kind
import arrow.core.Tuple2
import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.tuple2.eq.eq
import arrow.data.extensions.setk.eq.eq
import arrow.data.extensions.setk.foldable.foldable
import arrow.data.extensions.setk.hash.hash
import arrow.data.extensions.setk.monoidK.monoidK
import arrow.data.extensions.setk.semigroupK.semigroupK
import arrow.data.extensions.setk.semigroupal.semigroupal
import arrow.data.extensions.setk.show.show
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
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
      SemigroupalLaws.laws(SetK.semigroupal(), { SetK.just(it) }, this::bijection, associativeSemigroupalEq),
      MonoidKLaws.laws(SetK.monoidK(), { SetK.just(it) }, Eq.any()),
      FoldableLaws.laws(SetK.foldable(), { SetK.just(it) }, Eq.any()),
      HashLaws.laws(SetK.hash(Int.hash()), SetK.eq(Int.eq())) { SetK.just(it) }
    )
  }

  private fun bijection(from: Kind<ForSetK, Tuple2<Tuple2<Int, Int>, Int>>): SetK<Tuple2<Int, Tuple2<Int, Int>>> =
          from.fix().map { Tuple2(it.a.a, Tuple2(it.a.b, it.b)) }.toSet().k()
}
