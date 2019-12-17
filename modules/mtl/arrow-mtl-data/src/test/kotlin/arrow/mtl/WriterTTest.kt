package arrow.mtl

import arrow.Kind
import arrow.core.Const
import arrow.core.ConstPartialOf
import arrow.core.Either
import arrow.core.ForListK
import arrow.core.ForOption
import arrow.core.ListK
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.const.divisible.divisible
import arrow.core.extensions.either.eq.eq
import arrow.core.extensions.eq
import arrow.core.extensions.listk.monad.monad
import arrow.core.extensions.listk.monoidK.monoidK
import arrow.core.extensions.monoid
import arrow.core.extensions.option.alternative.alternative
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.option.monad.monad
import arrow.core.extensions.option.monadFilter.monadFilter
import arrow.core.extensions.tuple2.eq.eq
import arrow.core.fix
import arrow.core.value
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.applicativeError.attempt
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
import arrow.test.laws.AsyncLaws
import arrow.test.laws.DivisibleLaws
import arrow.test.laws.MonadFilterLaws
import arrow.test.laws.MonadWriterLaws
import arrow.test.laws.MonoidKLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen

class WriterTTest : UnitSpec() {

  fun ioEQK() = object : EqK<WriterTPartialOf<ForIO, Int>> {
    override fun <A> Kind<WriterTPartialOf<ForIO, Int>, A>.eqK(other: Kind<WriterTPartialOf<ForIO, Int>, A>, EQ: Eq<A>): Boolean =
      (this.fix().value().attempt().unsafeRunSync() to other.fix().value().attempt().unsafeRunSync()).let {
        Either.eq(Eq.any(), Tuple2.eq(Int.eq(), EQ)).run {
          it.first.eqv(it.second)
        }
      }
  }

  fun optionEQK() = object : EqK<WriterTPartialOf<ForOption, Int>> {
    override fun <A> Kind<WriterTPartialOf<ForOption, Int>, A>.eqK(other: Kind<WriterTPartialOf<ForOption, Int>, A>, EQ: Eq<A>): Boolean =
      (this.fix().value().fix() to other.fix().value().fix()).let { (optionA, optionB) ->
        Option.eq(Tuple2.eq(Int.eq(), EQ)).run {
          optionA.eqv(optionB)
        }
      }
  }

  fun constEQK() = object : EqK<WriterTPartialOf<ConstPartialOf<Int>, Int>> {
    override fun <A> Kind<WriterTPartialOf<ConstPartialOf<Int>, Int>, A>.eqK(other: Kind<WriterTPartialOf<ConstPartialOf<Int>, Int>, A>, EQ: Eq<A>): Boolean =
      this.value().value() == other.value().value()
  }

  fun listEQK() = object : EqK<WriterTPartialOf<ForListK, Int>> {
    override fun <A> Kind<WriterTPartialOf<ForListK, Int>, A>.eqK(other: Kind<WriterTPartialOf<ForListK, Int>, A>, EQ: Eq<A>): Boolean =
      this.value() == other.value()
  }

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
      AsyncLaws.laws(WriterT.async(IO.async(), Int.monoid()), ioEQK()),
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
  genkF: GenK<F>,
  genW: Gen<W>
) = object : GenK<WriterTPartialOf<F, W>> {
  override fun <A> genK(gen: Gen<A>): Gen<Kind<WriterTPartialOf<F, W>, A>> =
    genkF.genK(Gen.tuple2(genW, gen)).map {
      WriterT(it)
    }
}
