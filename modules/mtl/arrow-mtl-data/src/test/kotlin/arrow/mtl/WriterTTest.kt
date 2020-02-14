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
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.listk.eqK.eqK
import arrow.core.extensions.listk.monoid.monoid
import arrow.core.extensions.listk.monoidK.monoidK
import arrow.core.extensions.monoid
import arrow.core.extensions.option.alternative.alternative
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.option.eqK.eqK
import arrow.core.extensions.option.functor.functor
import arrow.core.extensions.option.monad.monad
import arrow.core.extensions.option.monadFilter.monadFilter
import arrow.core.k
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.functor.functor
import arrow.fx.extensions.io.monad.monad
import arrow.fx.mtl.concurrent
import arrow.fx.mtl.timer
import arrow.mtl.extensions.WriterTEqK
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
import arrow.test.generators.tuple2
import arrow.test.laws.AlternativeLaws
import arrow.test.laws.ConcurrentLaws
import arrow.test.laws.DivisibleLaws
import arrow.test.laws.MonadFilterLaws
import arrow.test.laws.MonadWriterLaws
import arrow.test.laws.MonoidKLaws
import io.kotlintest.properties.Gen

class WriterTTest : UnitSpec() {

  fun ioEQK(): WriterTEqK<ForIO, ListK<Int>> = WriterT.eqK(IO.eqK(), ListK.eq(Int.eq()))

  fun optionEQK(): WriterTEqK<ForOption, ListK<Int>> = WriterT.eqK(Option.eqK(), ListK.eq(Int.eq()))

  fun constEQK(): WriterTEqK<ConstPartialOf<Int>, ListK<Int>> = WriterT.eqK(Const.eqK(Int.eq()), ListK.eq(Int.eq()))

  fun listEQK(): WriterTEqK<ForListK, ListK<Int>> = WriterT.eqK(ListK.eqK(), ListK.eq(Int.eq()))

  init {

    testLaws(
      AlternativeLaws.laws(
        WriterT.alternative(ListK.monoid<Int>(), Option.alternative()),
        WriterT.genK(Option.genK(), Gen.list(Gen.int()).map { it.k() }),
        optionEQK()
      ),
      DivisibleLaws.laws(
        WriterT.divisible<ConstPartialOf<Int>, ListK<Int>>(Const.divisible(Int.monoid())),
        WriterT.genK(Const.genK(Gen.int()), Gen.list(Gen.int()).map { it.k() }),
        constEQK()
      ),
      ConcurrentLaws.laws(
        WriterT.concurrent(IO.concurrent(), ListK.monoid<Int>()),
        WriterT.timer(IO.concurrent(), ListK.monoid<Int>()),
        WriterT.functor<ForIO, ListK<Int>>(IO.functor()),
        WriterT.applicative(IO.applicative(), ListK.monoid<Int>()),
        WriterT.monad(IO.monad(), ListK.monoid<Int>()),
        WriterT.genK(IO.genK(), Gen.list(Gen.int()).map { it.k() }),
        ioEQK()
      ),
      MonoidKLaws.laws(
        WriterT.monoidK<ForListK, ListK<Int>>(ListK.monoidK()),
        WriterT.genK(ListK.genK(), Gen.list(Gen.int()).map { it.k() }),
        listEQK()
      ),

      MonadWriterLaws.laws(
        WriterT.monad(Option.monad(), ListK.monoid<Int>()),
        WriterT.monadWriter(Option.monad(), ListK.monoid<Int>()),
        ListK.monoid<Int>(),
        WriterT.functor<ForOption, ListK<Int>>(Option.functor()),
        WriterT.applicative(Option.applicative(), ListK.monoid<Int>()),
        WriterT.monad(Option.monad(), ListK.monoid<Int>()),
        Gen.list(Gen.int()).map { it.k() },
        WriterT.genK(Option.genK(), Gen.list(Gen.int()).map { it.k() }),
        optionEQK(),
        ListK.eq(Int.eq())
      ),

      MonadFilterLaws.laws(
        WriterT.monadFilter(Option.monadFilter(), ListK.monoid<Int>()),
        WriterT.functor<ForOption, ListK<Int>>(Option.functor()),
        WriterT.applicative(Option.applicative(), ListK.monoid<Int>()),
        WriterT.monad(Option.monad(), ListK.monoid<Int>()),
        WriterT.genK(Option.genK(), Gen.list(Gen.int()).map { it.k() }),
        optionEQK()
      )
    )
  }
}

internal fun <F, W> WriterT.Companion.genK(
  GENKF: GenK<F>,
  GENW: Gen<W>
) = object : GenK<WriterTPartialOf<F, W>> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<WriterTPartialOf<F, W>, A>> =
    GENKF.genK(Gen.tuple2(GENW, gen)).map(::WriterT)
}
