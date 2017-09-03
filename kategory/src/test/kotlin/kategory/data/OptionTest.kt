package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import kategory.Option.None
import kategory.Option.Some
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class OptionTest : UnitSpec() {

    object OptionError : RuntimeException()

    init {
        val EQ_EITHER: Eq<HK<OptionHK, Either<Throwable, Int>>> = object : Eq<HK<OptionHK, Either<Throwable, Int>>> {
            override fun eqv(a: HK<OptionHK, Either<Throwable, Int>>, b: HK<OptionHK, Either<Throwable, Int>>): Boolean =
                    a.ev().fold(
                            { b.ev().fold({ true }, { false }) },
                            { eitherA: Either<Throwable, Int> ->
                                b.ev().fold(
                                        { false },
                                        { eitherB: Either<Throwable, Int> ->
                                            eitherA.fold(
                                                    { eitherB.fold({ true /* Ignore the error kind */ }, { false }) },
                                                    { ia -> eitherB.fold({ false }, { ia == it }) })
                                        })
                            })
        }


        testLaws(MonadErrorLaws.laws(Option.monadError<Throwable>(OptionError), Eq.any(), EQ_EITHER))
        testLaws(TraverseLaws.laws(Option.traverse(), Option.monad(), ::Some, Eq.any()))

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
