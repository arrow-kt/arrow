package arrow.mtl

import arrow.Kind
import arrow.core.ForId
import arrow.core.Id
import arrow.core.Tuple2
import arrow.core.extensions.eq
import arrow.core.extensions.id.eqK.eqK
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.id.monad.monad
import arrow.core.extensions.monoid
import arrow.core.extensions.tuple2.eq.eq
import arrow.mtl.extensions.accumt.functor.functor
import arrow.mtl.extensions.accumt.monad.monad
import arrow.test.UnitSpec
import arrow.test.generators.GenK
import arrow.test.generators.genK
import arrow.test.generators.intSmall
import arrow.test.generators.tuple2
import arrow.test.laws.FunctorLaws
import arrow.test.laws.MonadLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Monad
import io.kotlintest.properties.Gen

class AccumTTest : UnitSpec() {
  init {
    testLaws(
      FunctorLaws.laws(
        AccumT.functor<Int, ForId>(Id.functor()),
        AccumT.genK(Id.genK(), Gen.intSmall()),
        AccumT.eqK(Id.monad(), Id.eqK(), Int.eq(), 123)
      ),

      MonadLaws.laws(
        AccumT.monad(Int.monoid(), Id.monad()),
        AccumT.genK(Id.genK(), Gen.intSmall()),
        AccumT.eqK(Id.monad(), Id.eqK(), Int.eq(), 4711)
      )
    )
  }
}

private fun <W, M> AccumT.Companion.genK(genkM: GenK<M>, genW: Gen<W>) =
  object : GenK<AccumTPartialOf<W, M>> {
    override fun <A> genK(gen: Gen<A>): Gen<Kind<AccumTPartialOf<W, M>, A>> =
      genkM.genK(genkM.genK(Gen.tuple2(gen, genW)).map {
        { _: W -> it }
      }).map {
        AccumT(it)
      }
  }

private fun <W, M> AccumT.Companion.eqK(MM: Monad<M>, EQK: EqK<M>, EQw: Eq<W>, w: W) =
  object : EqK<AccumTPartialOf<W, M>> {
    override fun <A> Kind<AccumTPartialOf<W, M>, A>.eqK(other: Kind<AccumTPartialOf<W, M>, A>, EQ: Eq<A>): Boolean =
      (this.fix() to other.fix()).let {
        it.first.runAccumT(MM, w) to it.second.runAccumT(MM, w)
      }.let {
        EQK.liftEq(Tuple2.eq(EQ, EQw)).run {
          val result = it.first.eqv(it.second)

          if (!result) {
            println("${it.first} != ${it.second}}")
          }

          result
        }
      }
  }

