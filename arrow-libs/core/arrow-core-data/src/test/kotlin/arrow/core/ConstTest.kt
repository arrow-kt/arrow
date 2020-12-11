package arrow.core

import arrow.Kind
import arrow.core.extensions.const.applicative.applicative
import arrow.core.extensions.const.eq.eq
import arrow.core.extensions.const.functor.functor
import arrow.core.extensions.const.hash.hash
import arrow.core.extensions.const.order.order
import arrow.core.extensions.const.show.show
import arrow.core.extensions.const.traverseFilter.traverseFilter
import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.monoid
import arrow.core.extensions.order
import arrow.core.extensions.show
import arrow.core.test.UnitSpec
import arrow.core.test.generators.genConst
import arrow.core.test.generators.genK
import arrow.core.test.laws.ApplicativeLaws
import arrow.core.test.laws.EqLaws
import arrow.core.test.laws.HashLaws
import arrow.core.test.laws.OrderLaws
import arrow.core.test.laws.ShowLaws
import arrow.core.test.laws.TraverseFilterLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen

class ConstTest : UnitSpec() {

  fun <A> EQK(EQA: Eq<A>): EqK<ConstPartialOf<A>> = object : EqK<ConstPartialOf<A>> {
    override fun <B> Kind<ConstPartialOf<A>, B>.eqK(other: Kind<ConstPartialOf<A>, B>, EQ: Eq<B>): Boolean =
      Const.eq<A, B>(EQA).run {
        this@eqK.fix().eqv(other.fix())
      }
  }

  init {
    val M = Int.monoid()
    val EQK = EQK(Int.eq())
    val GENK = Const.genK(Gen.int())
    val GEN = Gen.genConst<Int, Int>(Gen.int())

    testLaws(
      TraverseFilterLaws.laws(Const.traverseFilter(), Const.applicative(M), GENK, EQK),
      ApplicativeLaws.laws(Const.applicative(M), Const.functor(), GENK, EQK),
      EqLaws.laws(Const.eq<Int, Int>(Eq.any()), GEN),
      ShowLaws.laws(Const.show(Int.show()), Const.eq<Int, Int>(Eq.any()), GEN),
      HashLaws.laws(Const.hash(Int.hash()), GEN, Const.eq(Int.eq())),
      OrderLaws.laws(Const.order<Int, Int>(Int.order()), GEN)
    )
  }
}
