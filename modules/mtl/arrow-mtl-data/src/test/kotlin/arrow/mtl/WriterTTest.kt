package arrow.mtl

import arrow.Kind
import arrow.core.Const
import arrow.core.ConstPartialOf
import arrow.core.ForListK
import arrow.core.ForOption
import arrow.core.ListK
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.const
import arrow.core.extensions.const.divisible.divisible
import arrow.core.extensions.listk.monad.monad
import arrow.core.extensions.listk.monoidK.monoidK
import arrow.core.extensions.monoid
import arrow.core.extensions.option.alternative.alternative
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.option.functor.functor
import arrow.core.extensions.option.monad.monad
import arrow.core.extensions.option.monadFilter.monadFilter
import arrow.core.fix
import arrow.core.value
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.applicativeError.attempt
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.functor.functor
import arrow.fx.extensions.io.monad.monad
import arrow.fx.mtl.concurrent
import arrow.mtl.extensions.writert.alternative.alternative
import arrow.mtl.extensions.writert.applicative.applicative
import arrow.mtl.extensions.writert.divisible.divisible
import arrow.mtl.extensions.writert.functor.functor
import arrow.mtl.extensions.writert.monad.monad
import arrow.mtl.extensions.writert.monadFilter.monadFilter
import arrow.mtl.extensions.writert.monadWriter.monadWriter
import arrow.mtl.extensions.writert.monoidK.monoidK
import arrow.test.UnitSpec
import arrow.test.generators.intSmall
import arrow.test.generators.tuple2
import arrow.test.laws.AlternativeLaws
import arrow.test.laws.ConcurrentLaws
import arrow.test.laws.DivisibleLaws
import arrow.test.laws.MonadFilterLaws
import arrow.test.laws.MonadWriterLaws
import arrow.test.laws.MonoidKLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class WriterTTest : UnitSpec() {

  private fun <A> IOEQ(): Eq<Kind<WriterTPartialOf<ForIO, Int>, A>> = Eq { a, b ->
    a.value().attempt().unsafeRunSync() == b.value().attempt().unsafeRunSync()
  }

  init {

    testLaws(
      AlternativeLaws.laws(
        WriterT.alternative(Int.monoid(), Option.alternative()),
        { i -> WriterT.just(Option.applicative(), Int.monoid(), i) },
        { i -> WriterT.just(Option.applicative(), Int.monoid(), { j: Int -> i + j }) },
        Eq { a, b -> a.value() == b.value() }
      ),
      DivisibleLaws.laws(
        WriterT.divisible<ConstPartialOf<Int>, Int>(Const.divisible(Int.monoid())),
        { WriterT(it.const()) },
        Eq { a, b -> a.value().value() == b.value().value() }
      ),
      ConcurrentLaws.laws(
        WriterT.concurrent(IO.concurrent(), Int.monoid()),
        WriterT.functor<ForIO, Int>(IO.functor()),
        WriterT.applicative(IO.applicative(), Int.monoid()),
        WriterT.monad(IO.monad(), Int.monoid()),
        IOEQ(),
        IOEQ(), IOEQ()
      ),
      MonoidKLaws.laws(
        WriterT.monoidK<ForListK, Int>(ListK.monoidK()),
        WriterT.applicative(ListK.monad(), Int.monoid()),
        Eq { a, b -> a.value() == b.value() }),

      MonadWriterLaws.laws(
        WriterT.monad(Option.monad(), Int.monoid()),
        WriterT.monadWriter(Option.monad(), Int.monoid()),
        Int.monoid(),
        WriterT.functor<ForOption, Int>(Option.functor()),
        WriterT.applicative(Option.applicative(), Int.monoid()),
        WriterT.monad(Option.monad(), Int.monoid()),
        Gen.intSmall(),
        Gen.tuple2(Gen.intSmall(), Gen.intSmall()),
        Eq { a, b ->
          a.value().fix().let { optionA: Option<Tuple2<Int, Int>> ->
            val optionB = b.value().fix()
            optionA.fold({ optionB.fold({ true }, { false }) }, { value: Tuple2<Int, Int> -> optionB.fold({ false }, { value == it }) })
          }
        },
        Eq { a, b ->
          a.value().fix().let { optionA: Option<Tuple2<Int, Tuple2<Int, Int>>> ->
            val optionB = b.value().fix()
            optionA.fold({ optionB.fold({ true }, { false }) }, { value: Tuple2<Int, Tuple2<Int, Int>> -> optionB.fold({ false }, { value == it }) })
          }
        }
      ),

      MonadFilterLaws.laws(
        WriterT.monadFilter(Option.monadFilter(), Int.monoid()),
        WriterT.functor<ForOption, Int>(Option.functor()),
        WriterT.applicative(Option.applicative(), Int.monoid()),
        WriterT.monad(Option.monad(), Int.monoid()),
        { WriterT(Option(Tuple2(it, it))) },
        object : Eq<Kind<WriterTPartialOf<ForOption, Int>, Int>> {
          override fun Kind<WriterTPartialOf<ForOption, Int>, Int>.eqv(b: Kind<WriterTPartialOf<ForOption, Int>, Int>): Boolean =
            value().fix().let { optionA: Option<Tuple2<Int, Int>> ->
              val optionB = b.value().fix()
              optionA.fold({ optionB.fold({ true }, { false }) }, { value: Tuple2<Int, Int> -> optionB.fold({ false }, { value == it }) })
            }
        })
    )
  }
}
