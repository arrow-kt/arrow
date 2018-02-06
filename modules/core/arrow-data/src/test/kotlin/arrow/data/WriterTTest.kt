package arrow.data

import arrow.HK
import arrow.core.*
import arrow.instances.IntMonoid
import arrow.instances.monad
import arrow.mtl.monadFilter
import arrow.mtl.monadWriter
import arrow.test.UnitSpec
import arrow.test.generators.genIntSmall
import arrow.test.generators.genTuple
import arrow.test.laws.MonadFilterLaws
import arrow.test.laws.MonadLaws
import arrow.test.laws.MonadWriterLaws
import arrow.test.laws.MonoidKLaws
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class WriterTTest : UnitSpec() {
    init {

        "instances can be resolved implicitly" {
            functor<WriterTKindPartial<ForId, Int>>() shouldNotBe null
            applicative<WriterTKindPartial<ForId, Int>>() shouldNotBe null
            monad<WriterTKindPartial<ForId, Int>>() shouldNotBe null
            monadFilter<WriterTKindPartial<ForOption, Int>>() shouldNotBe null
            monadWriter<WriterTKindPartial<ForOption, Int>, Int>() shouldNotBe null
            semigroupK<WriterTKindPartial<ForListKW, Int>>() shouldNotBe null
            monoidK<WriterTKindPartial<ForListKW, Int>>() shouldNotBe null
        }

        testLaws(
            MonadLaws.laws(WriterT.monad(NonEmptyList.monad(), IntMonoid), Eq.any()),
            MonoidKLaws.laws(
                WriterT.monoidK<ForListKW, Int>(ListKW.monoidK()),
                WriterT.applicative(),
                Eq { a, b ->
                    a.ev().value == b.ev().value
                }),

            MonadWriterLaws.laws(WriterT.monad(Option.monad(), IntMonoid),
                WriterT.monadWriter(Option.monad(), IntMonoid),
                IntMonoid,
                genIntSmall(),
                genTuple(genIntSmall(), genIntSmall()),
                Eq { a, b ->
                    a.ev().value.ev().let { optionA: Option<Tuple2<Int, Int>> ->
                        val optionB = b.ev().value.ev()
                        optionA.fold({ optionB.fold({ true }, { false }) }, { value: Tuple2<Int, Int> -> optionB.fold({ false }, { value == it }) })
                    }
                },
                Eq { a, b ->
                    a.ev().value.ev().let { optionA: Option<Tuple2<Int, Tuple2<Int, Int>>> ->
                        val optionB = b.ev().value.ev()
                        optionA.fold({ optionB.fold({ true }, { false }) }, { value: Tuple2<Int, Tuple2<Int, Int>> -> optionB.fold({ false }, { value == it }) })
                    }
                }
            ),

            MonadFilterLaws.laws(WriterT.monadFilter(Option.monadFilter(), IntMonoid),
                { WriterT(Option(Tuple2(it, it))) },
                object : Eq<HK<WriterTKindPartial<ForOption, Int>, Int>> {
                    override fun eqv(a: HK<WriterTKindPartial<ForOption, Int>, Int>, b: HK<WriterTKindPartial<ForOption, Int>, Int>): Boolean =
                            a.ev().value.ev().let { optionA: Option<Tuple2<Int, Int>> ->
                                val optionB = b.ev().value.ev()
                                optionA.fold({ optionB.fold({ true }, { false }) }, { value: Tuple2<Int, Int> -> optionB.fold({ false }, { value == it }) })
                            }
                })
        )

    }
}
