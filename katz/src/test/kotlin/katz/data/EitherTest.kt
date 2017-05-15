package katz

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import katz.Either.Left
import katz.Either.Right
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class EitherTest : UnitSpec() {
    init {
        "map should modify value" {
            forAll { a: Int, b: String ->
                Right(a).map { b } == Right(b)
                        && Left(a).map { b } == Left(a)
            }
        }

        "flatMap should modify entity" {
            forAll { a: Int, b: String ->
                    val left: Either<Int, Int> = Left(a)

                    Right(a).flatMap { left } == left
                            && Right(a).flatMap { Right(b) } == Right(b)
                            && left.flatMap { Right(b) } == left
            }
        }

        "getOrElse should return value" {
            forAll { a: Int, b: Int ->
                Right(a).getOrElse { b } == a
                        && Left(a).getOrElse { b } == b
            }

        }

        "exits should evaluate value" {
            forAll { a: Int ->
                    val left: Either<Int, Int> = Left(a)

                    Right(a).exists { it > a - 1 } == true
                            && Right(a).exists { it > a + 1 } == false
                            && left.exists { it > a - 1 } == false
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

        "fold should call left function on Left" {
            forAll { a: Int, b: Int ->
                Left(a).fold({ b }, { a }) == b
            }
        }

        "fold should call right function on Right" {
            forAll { a: Int, b: Int ->
                Right(a).fold({ b }, { a }) == a
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

        "Either.monad.flatMap should be consistent with Either#flatMap" {
            forAll { a: Int ->
                val M = EitherMonad<Int>()
                val x = { b: Int -> Either.Right(b * a) }
                val option = Either.Right(a)
                option.flatMap(x) == M.flatMap(option, x)
            }
        }

        "Either.monad.binding should for comprehend over right either" {
            val result = EitherMonad<Int>().binding {
                val x = !Either.Right(1)
                val y = Either.Right(1).bind()
                val z = bind { Either.Right(1) }
                yields(x + y + z)
            }
            result shouldBe Either.Right(3)
        }
    }
}
