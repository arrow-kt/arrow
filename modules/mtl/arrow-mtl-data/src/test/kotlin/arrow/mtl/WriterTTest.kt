package arrow.mtl

import arrow.Kind
import arrow.core.Const
import arrow.core.ConstPartialOf
import arrow.core.ForListK
import arrow.core.ListK
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.const.divisible.divisible
import arrow.core.extensions.const.eqK.eqK
import arrow.core.extensions.eq
import arrow.core.extensions.listk.eqK.eqK
import arrow.core.extensions.listk.monad.monad
import arrow.core.extensions.listk.monoidK.monoidK
import arrow.core.extensions.monoid
import arrow.core.extensions.option.alternative.alternative
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.option.eqK.eqK
import arrow.core.extensions.option.monad.monad
import arrow.core.extensions.option.monadFilter.monadFilter
import arrow.core.extensions.tuple2.eq.eq
import arrow.fx.IO
import arrow.fx.extensions.io.applicativeError.attempt
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.mtl.concurrent
import arrow.fx.extensions.io.async.async
import arrow.fx.mtl.writert.async.async

import arrow.mtl.extensions.writert.alternative.alternative
import arrow.mtl.extensions.writert.applicative.applicative
import arrow.mtl.extensions.writert.divisible.divisible
import arrow.mtl.extensions.writert.monad.monad
import arrow.mtl.extensions.writert.monadFilter.monadFilter
import arrow.mtl.extensions.writert.monadWriter.monadWriter
import arrow.mtl.extensions.writert.monoidK.monoidK
import arrow.test.UnitSpec
import arrow.test.generators.GenK
import arrow.test.generators.genK
import arrow.test.generators.intSmall
import arrow.test.generators.tuple2
import arrow.test.laws.AlternativeLaws
import arrow.test.laws.ConcurrentLaws
import arrow.test.laws.DivisibleLaws
import arrow.test.laws.MonadFilterLaws
import arrow.test.laws.MonadWriterLaws
import arrow.test.laws.MonoidKLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen

class WriterTTest : UnitSpec() {

  fun ioEQK() = WriterT.eqK(IO.eqK(), Int.eq())

  fun optionEQK() = WriterT.eqK(Option.eqK(), Int.eq())

  fun constEQK() = WriterT.eqK(Const.eqK(Int.eq()), Int.eq())

  fun listEQK() = WriterT.eqK(ListK.eqK(), Int.eq())

  init {

    testLaws(
      AlternativeLaws.laws(
        WriterT.alternative(Int.monoid(), Option.alternative()),
        { i -> WriterT.just(Option.applicative(), Int.monoid(), i) },
        { i -> WriterT.just(Option.applicative(), Int.monoid()) { j: Int -> i + j } },
        optionEQK()
      ),
      DivisibleLaws.laws(
        WriterT.divisible<ConstPartialOf<Int>, Int>(Const.divisible(Int.monoid())),
        WriterT.genK(Const.genK(Gen.int()), Gen.int()),
        constEQK()
      ),
      ConcurrentLaws.laws(WriterT.concurrent(IO.concurrent(), Int.monoid()), IOEQ(), IOEQ(), IOEQ()),
      MonoidKLaws.laws(
        WriterT.monoidK<ForListK, Int>(ListK.monoidK()),
        WriterT.applicative(ListK.monad(), Int.monoid()),
        listEQK()
      ),

      MonadWriterLaws.laws(WriterT.monad(Option.monad(), Int.monoid()),
        WriterT.monadWriter(Option.monad(), Int.monoid()),
        Int.monoid(),
        Gen.intSmall(),
        Gen.tuple2(Gen.intSmall(), Gen.intSmall()),
        optionEQK(),
        Int.eq()
      ),

      MonadFilterLaws.laws(
        WriterT.monadFilter(Option.monadFilter(), Int.monoid()),
        { WriterT(Option(Tuple2(it, it))) },
        optionEQK()
      )
    )
  }
}

private fun <F, W> WriterT.Companion.genK(
  GENKF: GenK<F>,
  GENW: Gen<W>
) = object : GenK<WriterTPartialOf<F, W>> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<WriterTPartialOf<F, W>, A>> =
    GENKF.genK(Gen.tuple2(GENW, gen)).map(::WriterT)
}

fun <F, W> WriterT.Companion.eqK(EQKF: EqK<F>, EQW: Eq<W>) = object : EqK<WriterTPartialOf<F, W>> {
  override fun <A> Kind<WriterTPartialOf<F, W>, A>.eqK(other: Kind<WriterTPartialOf<F, W>, A>, EQ: Eq<A>): Boolean =
    (this.fix() to other.fix()).let {
      EQKF.liftEq(Tuple2.eq(EQW, EQ)).run {
        it.first.value().eqv(it.second.value())
      }
    }
}
