package arrow.mtl

import arrow.Kind
import arrow.core.Either
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.ForListK
import arrow.core.ListK
import arrow.core.extensions.const.divisible.divisible
import arrow.core.extensions.monoid
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.applicativeError.attempt
import arrow.fx.extensions.io.async.async
import arrow.fx.mtl.writert.async.async
import arrow.core.extensions.listk.monad.monad
import arrow.core.extensions.listk.monoidK.monoidK
import arrow.core.extensions.option.monad.monad
import arrow.core.fix
import arrow.mtl.extensions.writert.applicative.applicative
import arrow.mtl.extensions.writert.divisible.divisible
import arrow.mtl.extensions.writert.monad.monad
import arrow.mtl.extensions.writert.monoidK.monoidK
import arrow.core.extensions.option.monadFilter.monadFilter
import arrow.mtl.extensions.writert.monadFilter.monadFilter
import arrow.mtl.extensions.writert.monadWriter.monadWriter
import arrow.test.UnitSpec
import arrow.test.generators.intSmall
import arrow.test.generators.tuple2
import arrow.test.laws.AsyncLaws
import arrow.test.laws.DivisibleLaws
import arrow.test.laws.MonadFilterLaws
import arrow.test.laws.MonadWriterLaws
import arrow.test.laws.MonoidKLaws
import arrow.typeclasses.Const
import arrow.typeclasses.ConstPartialOf
import arrow.typeclasses.Eq
import arrow.typeclasses.const
import arrow.typeclasses.value
import io.kotlintest.properties.Gen
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class WriterTTest : UnitSpec() {

  private fun IOEQ(): Eq<Kind<WriterTPartialOf<ForIO, Int>, Int>> = Eq { a, b ->
    a.value().attempt().unsafeRunSync() == b.value().attempt().unsafeRunSync()
  }

  private fun IOEitherEQ(): Eq<Kind<WriterTPartialOf<ForIO, Int>, Either<Throwable, Int>>> = Eq { a, b ->
    a.value().attempt().unsafeRunSync() == b.value().attempt().unsafeRunSync()
  }

  init {

    testLaws(
      DivisibleLaws.laws(
        WriterT.divisible<ConstPartialOf<Int>, Int>(Const.divisible(Int.monoid())),
        { WriterT(it.const()) },
        Eq { a, b -> a.value().value() == b.value().value() }
      ),
      AsyncLaws.laws(WriterT.async(IO.async(), Int.monoid()), IOEQ(), IOEitherEQ()),
      MonoidKLaws.laws(
        WriterT.monoidK<ForListK, Int>(ListK.monoidK()),
        WriterT.applicative(ListK.monad(), Int.monoid()),
        Eq { a, b -> a.value() == b.value() }),

      MonadWriterLaws.laws(WriterT.monad(Option.monad(), Int.monoid()),
        WriterT.monadWriter(Option.monad(), Int.monoid()),
        Int.monoid(),
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
