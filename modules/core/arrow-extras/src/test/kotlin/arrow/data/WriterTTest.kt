package arrow.data

import arrow.Kind
import arrow.core.*
import arrow.core.extensions.const.divisible.divisible
import arrow.core.extensions.monoid
import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.extensions.io.applicativeError.attempt
import arrow.effects.extensions.io.async.async
import arrow.effects.extensions.writert.async.async
import arrow.data.extensions.listk.monad.monad
import arrow.data.extensions.listk.monoidK.monoidK
import arrow.core.extensions.option.monad.monad
import arrow.data.extensions.writert.applicative.applicative
import arrow.data.extensions.writert.divisible.divisible
import arrow.data.extensions.writert.monad.monad
import arrow.data.extensions.writert.monoidK.monoidK
import arrow.mtl.extensions.option.monadFilter.monadFilter
import arrow.mtl.extensions.writert.monadFilter.monadFilter
import arrow.mtl.extensions.writert.monadWriter.monadWriter
import arrow.test.UnitSpec
import arrow.test.generators.intSmall
import arrow.test.generators.tuple2
import arrow.test.laws.*
import arrow.typeclasses.*
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
