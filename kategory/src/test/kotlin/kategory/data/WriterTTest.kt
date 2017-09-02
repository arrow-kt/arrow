package kategory

import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class WriterTTest : UnitSpec() {
    init {
        testLaws(MonadLaws.laws(WriterT.monad(NonEmptyList.monad(), IntMonoid), Eq.any()))
        testLaws(MonoidKLaws.laws(
                WriterT.monoidK<ListKWHK, Int>(ListKW.monad(), ListKW.monoidK()),
                WriterT.applicative(ListKW.monad(), IntMonoid),
                Eq { a, b ->
                    a.ev().value == b.ev().value
                }))

        testLaws(MonadWriterLaws.laws(WriterT.monad(Option.monad(), IntMonoid),
                WriterT.monadWriter(Option.monad(), IntMonoid),
                IntMonoid,
                genIntSmall(),
                genTuple(genIntSmall(), genIntSmall()),
                Eq { a, b ->
                    a.ev().value.ev().let { optionA: Option<Tuple2<Int, Int>> ->
                        val optionB = a.ev().value.ev()
                        optionA.fold({ optionB.fold({ true }, { false }) }, { value: Tuple2<Int, Int> -> optionB.fold({ false }, { value == it }) })
                    }
                },
                Eq { a, b ->
                    a.ev().value.ev().let { optionA: Option<Tuple2<Int, Tuple2<Int, Int>>> ->
                        val optionB = a.ev().value.ev()
                        optionA.fold({ optionB.fold({ true }, { false }) }, { value: Tuple2<Int, Tuple2<Int, Int>> -> optionB.fold({ false }, { value == it }) })
                    }
                }
        ))
    }
}
