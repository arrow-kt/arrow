package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.forAll
import kategory.Option.None
import kategory.Option.Some
import kategory.laws.MonadFilterLaws
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
            semigroup<Option<Int>>() shouldNotBe null
            monoid<Option<Int>>() shouldNotBe null
            monadError<OptionHK, Unit>() shouldNotBe null
        }

        val EQ_EITHER: Eq<HK<OptionHK, Either<Unit, Int>>> = object : Eq<HK<OptionHK, Either<Unit, Int>>> {
            override fun eqv(a: HK<OptionHK, Either<Unit, Int>>, b: HK<OptionHK, Either<Unit, Int>>): Boolean =
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
        
        //testLaws(MonadErrorLaws.laws(monadError<OptionHK, Unit>(), Eq.any(), EQ_EITHER)) TODO reenable once the MonadErrorLaws are parametric to `E`
        testLaws(TraverseLaws.laws(Option.traverse(), Option.monad(), ::Some, Eq.any()))
        testLaws(MonadFilterLaws.laws(Option.monadFilter(), ::Some, Eq.any()))
        testLaws(MonadCombineLaws.laws(Option.monadCombine(), ::Some, { Some({ a: Int -> a * 2 }) }, Eq.any(), Eq.any()))

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
