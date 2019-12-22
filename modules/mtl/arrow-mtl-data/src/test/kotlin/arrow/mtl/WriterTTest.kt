package arrow.mtl

import arrow.Kind
import arrow.core.Const
import arrow.core.ConstPartialOf
import arrow.core.ForListK
import arrow.core.ForOption
import arrow.core.ListK
import arrow.core.Option
import arrow.core.extensions.const.divisible.divisible
import arrow.core.extensions.const.eqK.eqK
import arrow.core.extensions.eq
import arrow.core.extensions.listk.eqK.eqK
import arrow.core.extensions.listk.monoidK.monoidK
import arrow.core.extensions.monoid
import arrow.core.extensions.option.alternative.alternative
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.option.eqK.eqK
import arrow.core.extensions.option.functor.functor
import arrow.core.extensions.option.monad.monad
import arrow.core.extensions.option.monadFilter.monadFilter
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.functor.functor
import arrow.fx.extensions.io.monad.monad
import arrow.fx.mtl.concurrent
import arrow.mtl.extensions.writert.alternative.alternative
import arrow.mtl.extensions.writert.applicative.applicative
import arrow.mtl.extensions.writert.divisible.divisible
import arrow.mtl.extensions.writert.eqK.eqK
import arrow.mtl.extensions.writert.functor.functor
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
        WriterT.genK(Option.genK(), Gen.int()),
        optionEQK()
      ),
      DivisibleLaws.laws(
        WriterT.divisible<ConstPartialOf<Int>, Int>(Const.divisible(Int.monoid())),
        WriterT.genK(Const.genK(Gen.int()), Gen.int()),
        constEQK()
      ),
      ConcurrentLaws.laws(
        WriterT.concurrent(IO.concurrent(), Int.monoid()),
        WriterT.functor<ForIO, Int>(IO.functor()),
        WriterT.applicative(IO.applicative(), Int.monoid()),
        WriterT.monad(IO.monad(), Int.monoid()),
        WriterT.genK(IO.genK(), Gen.int()),
        ioEQK()
      ),
      MonoidKLaws.laws(
        WriterT.monoidK<ForListK, Int>(ListK.monoidK()),
        WriterT.genK(ListK.genK(), Gen.int()),
        listEQK()
      ),

      MonadWriterLaws.laws(
        WriterT.monad(Option.monad(), Int.monoid()),
        WriterT.monadWriter(Option.monad(), Int.monoid()),
        Int.monoid(),
        WriterT.functor<ForOption, Int>(Option.functor()),
        WriterT.applicative(Option.applicative(), Int.monoid()),
        WriterT.monad(Option.monad(), Int.monoid()),
        Gen.intSmall(),
        WriterT.genK(Option.genK(), Gen.int()),
        optionEQK(),
        Int.eq()
      ),

      MonadFilterLaws.laws(
        WriterT.monadFilter(Option.monadFilter(), Int.monoid()),
        WriterT.functor<ForOption, Int>(Option.functor()),
        WriterT.applicative(Option.applicative(), Int.monoid()),
        WriterT.monad(Option.monad(), Int.monoid()),
        WriterT.genK(Option.genK(), Gen.int()),
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
