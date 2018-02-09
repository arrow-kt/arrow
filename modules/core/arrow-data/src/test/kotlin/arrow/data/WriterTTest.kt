package arrow.data

import arrow.Kind
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
            functor<WriterTPartialOf<ForId, Int>>() shouldNotBe null
            applicative<WriterTPartialOf<ForId, Int>>() shouldNotBe null
            monad<WriterTPartialOf<ForId, Int>>() shouldNotBe null
            monadFilter<WriterTPartialOf<ForOption, Int>>() shouldNotBe null
            monadWriter<WriterTPartialOf<ForOption, Int>, Int>() shouldNotBe null
            semigroupK<WriterTPartialOf<ForListK, Int>>() shouldNotBe null
            monoidK<WriterTPartialOf<ForListK, Int>>() shouldNotBe null
        }

        testLaws(
            MonadLaws.laws(WriterT.monad(NonEmptyList.monad(), IntMonoid), Eq.any()),
            MonoidKLaws.laws(
                WriterT.monoidK<ForListK, Int>(ListK.monoidK()),
                WriterT.applicative(),
                Eq { a, b ->
                    a.extract().value == b.extract().value
                }),

            MonadWriterLaws.laws(WriterT.monad(Option.monad(), IntMonoid),
                WriterT.monadWriter(Option.monad(), IntMonoid),
                IntMonoid,
                genIntSmall(),
                genTuple(genIntSmall(), genIntSmall()),
                Eq { a, b ->
                    a.extract().value.extract().let { optionA: Option<Tuple2<Int, Int>> ->
                        val optionB = b.extract().value.extract()
                        optionA.fold({ optionB.fold({ true }, { false }) }, { value: Tuple2<Int, Int> -> optionB.fold({ false }, { value == it }) })
                    }
                },
                Eq { a, b ->
                    a.extract().value.extract().let { optionA: Option<Tuple2<Int, Tuple2<Int, Int>>> ->
                        val optionB = b.extract().value.extract()
                        optionA.fold({ optionB.fold({ true }, { false }) }, { value: Tuple2<Int, Tuple2<Int, Int>> -> optionB.fold({ false }, { value == it }) })
                    }
                }
            ),

            MonadFilterLaws.laws(WriterT.monadFilter(Option.monadFilter(), IntMonoid),
                { WriterT(Option(Tuple2(it, it))) },
                object : Eq<Kind<WriterTPartialOf<ForOption, Int>, Int>> {
                    override fun eqv(a: Kind<WriterTPartialOf<ForOption, Int>, Int>, b: Kind<WriterTPartialOf<ForOption, Int>, Int>): Boolean =
                            a.extract().value.extract().let { optionA: Option<Tuple2<Int, Int>> ->
                                val optionB = b.extract().value.extract()
                                optionA.fold({ optionB.fold({ true }, { false }) }, { value: Tuple2<Int, Int> -> optionB.fold({ false }, { value == it }) })
                            }
                })
        )

    }
}
