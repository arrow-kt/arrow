package arrow.data

import arrow.HK
import arrow.core.*
import arrow.instances.IntMonoid
import arrow.instances.applicative
import arrow.instances.monad
import arrow.instances.monoidK
import arrow.mtl.instances.monadFilter
import arrow.mtl.instances.monadWriter
import arrow.mtl.monadFilter
import arrow.mtl.monadWriter
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith
import arrow.test.UnitSpec
import arrow.test.generators.genIntSmall
import arrow.test.generators.genTuple
import arrow.test.laws.MonadFilterLaws
import arrow.test.laws.MonadLaws
import arrow.test.laws.MonadWriterLaws
import arrow.test.laws.MonoidKLaws
import arrow.typeclasses.*

@RunWith(KTestJUnitRunner::class)
class WriterTTest : UnitSpec() {
    init {

        "instances can be resolved implicitly" {
            functor<WriterTKindPartial<IdHK, Int>>() shouldNotBe null
            applicative<WriterTKindPartial<IdHK, Int>>() shouldNotBe null
            monad<WriterTKindPartial<IdHK, Int>>() shouldNotBe null
            monadFilter<WriterTKindPartial<OptionHK, Int>>() shouldNotBe null
            monadWriter<WriterTKindPartial<OptionHK, Int>, Int>() shouldNotBe null
            semigroupK<WriterTKindPartial<ListKWHK, Int>>() shouldNotBe null
            monoidK<WriterTKindPartial<ListKWHK, Int>>() shouldNotBe null
        }

        testLaws(
            MonadLaws.laws(WriterT.monad(NonEmptyList.monad(), IntMonoid), Eq.any()),
            MonoidKLaws.laws(
                WriterT.monoidK<ListKWHK, Int>(ListKW.monoidK()),
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
                object : Eq<HK<WriterTKindPartial<OptionHK, Int>, Int>> {
                    override fun eqv(a: HK<WriterTKindPartial<OptionHK, Int>, Int>, b: HK<WriterTKindPartial<OptionHK, Int>, Int>): Boolean =
                            a.ev().value.ev().let { optionA: Option<Tuple2<Int, Int>> ->
                                val optionB = b.ev().value.ev()
                                optionA.fold({ optionB.fold({ true }, { false }) }, { value: Tuple2<Int, Int> -> optionB.fold({ false }, { value == it }) })
                            }
                })
        )

    }
}
