package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.None
import kategory.Some
import kategory.laws.EqLaws
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class OptionTest : UnitSpec() {

    init {

        "instances can be resolved implicitly" {
            functor<OptionHK>() shouldNotBe null
            applicative<OptionHK>() shouldNotBe null
            monad<OptionHK>() shouldNotBe null
            foldable<OptionHK>() shouldNotBe null
            traverse<OptionHK>() shouldNotBe null
            traverseFilter<OptionHK>() shouldNotBe null
            semigroup<Option<Int>>() shouldNotBe null
            monoid<Option<Int>>() shouldNotBe null
            monadError<OptionHK, Unit>() shouldNotBe null
            eq<Option<Int>>() shouldNotBe null
        }

        val EQ_EITHER: Eq<HK<OptionHK, Either<Unit, Int>>> = Eq { a, b ->
                    a.ev().fold(
                            { b.ev().fold({ true }, { false }) },
                            { eitherA: Either<Unit, Int> ->
                                b.ev().fold(
                                        { false },
                                        { eitherB: Either<Unit, Int> ->
                                            eitherA.fold(
                                                    { eitherB.fold({ true /* Ignore the error kind */ }, { false }) },
                                                    { ia -> eitherB.fold({ false }, { ia == it }) })
                                        })
                            })
        }

        testLaws(EqLaws.laws(eq(), { genOption(Gen.int()).generate() }))
        //testLaws(MonadErrorLaws.laws(monadError<OptionHK, Unit>(), Eq.any(), EQ_EITHER)) TODO reenable once the MonadErrorLaws are parametric to `E`
        testLaws(TraverseFilterLaws.laws(Option.traverseFilter(), Option.monad(), ::Some, Eq.any()))
        testLaws(MonadFilterLaws.laws(Option.monadFilter(), ::Some, Eq.any()))

        "fromNullable should work for both null and non-null values of nullable types" {
            forAll { a: Int? ->
                // This seems to be generating only non-null values, so it is complemented by the next test
                val o: Option<Int> = Option.fromNullable(a)
                if (a == null) o == None else o == Some(a)
            }
        }

        "fromNullable should return none for null values of nullable types" {
            val a: Int? = null
            Option.fromNullable(a) shouldBe None
        }

    }

}
