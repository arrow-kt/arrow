package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class WriterTTest : UnitSpec() {
    init {

        testLaws(MonadLaws.laws(WriterT.monad(NonEmptyList, IntMonoid), Eq.any()))
        testLaws(MonoidKLaws.laws<WriterTKindPartial<OptionHK, Int>>(
                WriterT.monoidK(Option.monad(), OptionMonoidK()),
                WriterT.applicative(Option.monad(), IntMonoid),
                WriterT.invoke(Option(Tuple2(1, 2)), Option.monad()),
                Eq.any(),
                Eq.any()))

        testLaws(MonadWriterLaws.laws(WriterT.monad(Option.monad(), IntMonoid),
                WriterT.monadWriter(Option.monad(), IntMonoid),
                IntMonoid,
                genIntSmall(),
                genTuple(genIntSmall(), genIntSmall()),
                object : Eq<HK<WriterTKindPartial<OptionHK, Int>, Int>> {
                    override fun eqv(a: HK<WriterTKindPartial<OptionHK, Int>, Int>, b: HK<WriterTKindPartial<OptionHK, Int>, Int>): Boolean =
                            a.ev().value.ev().let { optionA: Option<Tuple2<Int, Int>> ->
                                val optionB = a.ev().value.ev()
                                optionA.fold({ optionB.fold({ true }, { false }) }, { value: Tuple2<Int, Int> -> optionB.fold({ false }, { value == it }) })
                            }
                },
                object : Eq<HK<WriterTKindPartial<OptionHK, Int>, Tuple2<Int, Int>>> {
                    override fun eqv(a: HK<WriterTKindPartial<OptionHK, Int>, Tuple2<Int, Int>>, b: HK<WriterTKindPartial<OptionHK, Int>, Tuple2<Int, Int>>): Boolean =
                            a.ev().value.ev().let { optionA: Option<Tuple2<Int, Tuple2<Int, Int>>> ->
                                val optionB = a.ev().value.ev()
                                optionA.fold({ optionB.fold({ true }, { false }) }, { value: Tuple2<Int, Tuple2<Int, Int>> -> optionB.fold({ false }, { value == it }) })
                            }
                }
        ))

    }
}
