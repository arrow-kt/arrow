package arrow

import arrow.core.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.forAll
import arrow.test.laws.EqLaws
import org.junit.runner.RunWith
import arrow.test.UnitSpec
import arrow.test.laws.MonadErrorLaws
import arrow.test.laws.SemigroupKLaws
import arrow.test.laws.TraverseLaws

@RunWith(KTestJUnitRunner::class)
class EitherTest : UnitSpec() {
    val EQ: Eq<HK<EitherKindPartial<IdHK>, Int>> = Eq { a, b ->
        a.ev() == b.ev()
    }

    init {

        "instances can be resolved implicitly" {
            functor<EitherKindPartial<Throwable>>() shouldNotBe null
            applicative<EitherKindPartial<Throwable>>() shouldNotBe null
            monad<EitherKindPartial<Throwable>>() shouldNotBe null
            foldable<EitherKindPartial<Throwable>>() shouldNotBe null
            traverse<EitherKindPartial<Throwable>>() shouldNotBe null
            monadError<EitherKindPartial<Throwable>, Throwable>() shouldNotBe null
            semigroupK<EitherKindPartial<Throwable>>() shouldNotBe null
            eq<Either<String, Int>>() shouldNotBe null
        }

        testLaws(
            EqLaws.laws(eq<Either<String, Int>>(), { Right(it) }),
            MonadErrorLaws.laws(Either.monadError(), Eq.any(), Eq.any()),
            TraverseLaws.laws(Either.traverse<Throwable>(), Either.applicative(), { Right(it) }, Eq.any()),
            SemigroupKLaws.laws(Either.semigroupK(), Either.applicative(), EQ)
        )

        "getOrElse should return value" {
            forAll { a: Int, b: Int ->
                Right(a).getOrElse { b } == a
                        && Left(a).getOrElse { b } == b
            }

        }

        "filterOrElse should filters value" {
            forAll { a: Int, b: Int ->
                    val left: Either<Int, Int> = Left(a)

                    Right(a).filterOrElse({ it > a - 1 }, { b }) == Right(a)
                            && Right(a).filterOrElse({ it > a + 1 }, { b }) == Left(b)
                            && left.filterOrElse({ it > a - 1 }, { b }) == Left(a)
                            && left.filterOrElse({ it > a + 1 }, { b }) == Left(a)
            }
        }

        "swap should interchange values" {
            forAll { a: Int ->
                Left(a).swap() == Right(a)
                        && Right(a).swap() == Left(a)
            }
        }

        "toOption should convert" {
            forAll { a: Int ->
                Right(a).toOption() == Some(a)
                        && Left(a).toOption() == None
            }
        }

        "contains should check value" {
            forAll { a: Int, b: Int ->
                Right(a).contains(a)
                        && !Right(a).contains(b)
                        && !Left(a).contains(a)
            }
        }

    }
}
