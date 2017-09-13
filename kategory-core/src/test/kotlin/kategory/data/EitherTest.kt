package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.forAll
import kategory.Either.Left
import kategory.Either.Right
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class EitherTest : UnitSpec() {
    init {

        "instances can be resolved implicitly" {
            functor<EitherKindPartial<Throwable>>() shouldNotBe null
            applicative<EitherKindPartial<Throwable>>() shouldNotBe null
            monad<EitherKindPartial<Throwable>>() shouldNotBe null
            foldable<EitherKindPartial<Throwable>>() shouldNotBe null
            traverse<EitherKindPartial<Throwable>>() shouldNotBe null
            monadError<EitherKindPartial<Throwable>, Throwable>() shouldNotBe null
            semigroupK<EitherKindPartial<Throwable>>() shouldNotBe null
        }

        testLaws(MonadErrorLaws.laws(Either.monadError(), Eq.any(), Eq.any()))
        testLaws(TraverseLaws.laws(Either.traverse<Throwable>(), Either.applicative(), { it.right() }, Eq.any()))
        testLaws(SemigroupKLaws.laws(
                Either.semigroupK(),
                Either.applicative(),
                object : Eq<HK<EitherKindPartial<IdHK>, Int>> {
                    override fun eqv(a: HK<EitherKindPartial<IdHK>, Int>, b: HK<EitherKindPartial<IdHK>, Int>): Boolean =
                            a.ev() == b.ev()
                }))

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
                Right(a).toOption() == Option.Some(a)
                        && Left(a).toOption() == Option.None
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
