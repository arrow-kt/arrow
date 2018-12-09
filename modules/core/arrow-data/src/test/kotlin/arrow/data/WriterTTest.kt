package arrow.data

import arrow.Kind
import arrow.core.*
import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.instances.io.applicativeError.attempt
import arrow.effects.instances.io.async.async
import arrow.effects.instances.writert.async.async
import arrow.instances.*
import arrow.instances.listk.monad.monad
import arrow.instances.listk.monoidK.monoidK
import arrow.instances.option.monad.monad
import arrow.instances.writert.applicative.applicative
import arrow.instances.writert.monad.monad
import arrow.instances.writert.monoidK.monoidK
import arrow.mtl.instances.option.monadFilter.monadFilter
import arrow.mtl.instances.writert.monadFilter.monadFilter
import arrow.mtl.instances.writert.monadWriter.monadWriter
import arrow.test.UnitSpec
import arrow.test.generators.genIntSmall
import arrow.test.generators.genTuple
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class WriterTTest : UnitSpec() {

  private fun IOEQ(): Eq<Kind<WriterTPartialOf<ForIO, Int>, Int>> = Eq { a, b ->
    a.value().attempt().unsafeRunSync() == b.value().attempt().unsafeRunSync()
  }

  private fun IOEitherEQ(): Eq<Kind<WriterTPartialOf<ForIO, Int>, Either<Throwable, Int>>> = Eq { a, b ->
    a.value().attempt().unsafeRunSync() == b.value().attempt().unsafeRunSync()
  }

  init {

    testLaws(
      AsyncLaws.laws(WriterT.async(IO.async(), Int.monoid()), IOEQ(), IOEitherEQ()),

      MonoidKLaws.laws(
        WriterT.  monoidK<ForListK, Int>(ListK.monoidK()),
        WriterT.applicative(ListK.monad(), Int.monoid()),
        Eq { a, b ->
          a.value() == b.value()
        }),

      MonadWriterLaws.laws(WriterT.monad(Option.monad(), Int.monoid()),
        WriterT.monadWriter(Option.monad(), Int.monoid()),
        Int.monoid(),
        genIntSmall(),
        genTuple(genIntSmall(), genIntSmall()),
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

      MonadFilterLaws.laws(WriterT.monadFilter(Option.monadFilter(), Int.monoid()),
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
