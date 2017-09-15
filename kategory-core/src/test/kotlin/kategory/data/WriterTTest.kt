package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import kategory.laws.FunctorFilterLaws
import kategory.laws.MonadFilterLaws
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class WriterTTest : UnitSpec() {
    init {

        "instances can be resolved implicitly" {
            functor<WriterTKindPartial<IdHK, Int>>() shouldNotBe null
            applicative<WriterTKindPartial<IdHK, Int>>() shouldNotBe null
            monad<WriterTKindPartial<IdHK, Int>>() shouldNotBe null
            monadFilter<WriterTKindPartial<OptionHK, Int>>() shouldNotBe null
            monadWriter<WriterTKindPartial<OptionHK, Int>, Int>() shouldNotBe null
            semigroupK<WriterTKindPartial<IdHK, ListKWHK>>() shouldNotBe null
            monoidK<WriterTKindPartial<IdHK, ListKWHK>>() shouldNotBe null
        }

        testLaws(MonadLaws.laws(WriterT.monad(NonEmptyList.monad(), IntMonoid), Eq.any()))
        testLaws(MonoidKLaws.laws(
                WriterT.monoidK<ListKWHK, Int>(ListKW.monoidK()),
                WriterT.applicative(),
                object : Eq<WriterTKind<ListKWHK, Int, Int>> {
                    override fun eqv(a: WriterTKind<ListKWHK, Int, Int>, b: WriterTKind<ListKWHK, Int, Int>): Boolean =
                            a.ev().value == b.ev().value
                }))

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

        testLaws(MonadFilterLaws.laws(WriterT.monadFilter(IntMonoid, Option.monadFilter()),
                { WriterT(Option(Tuple2(it, it))) },
                object : Eq<HK<WriterTKindPartial<OptionHK, Int>, Int>> {
                    override fun eqv(a: HK<WriterTKindPartial<OptionHK, Int>, Int>, b: HK<WriterTKindPartial<OptionHK, Int>, Int>): Boolean =
                            a.ev().value.ev().let { optionA: Option<Tuple2<Int, Int>> ->
                                val optionB = a.ev().value.ev()
                                optionA.fold({ optionB.fold({ true }, { false }) }, { value: Tuple2<Int, Int> -> optionB.fold({ false }, { value == it }) })
                            }
                }))
    }
}
