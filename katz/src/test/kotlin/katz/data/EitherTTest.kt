package katz

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class EitherTTest : UnitSpec() {
    init {
        "map should modify value" {
            forAll { a: String ->
                val ot = EitherT(Id(Either.Right(a)))
                val mapped = ot.map({ "$it power" })
                val expected = EitherT(Id(Either.Right("$a power")))

                mapped == expected
            }
        }

       "flatMap should modify entity" {
            forAll { a: String ->
                val ot = EitherT(NonEmptyList.of(Either.Right(a)))
                val mapped = ot.flatMap{ EitherT(NonEmptyList.of(Either.Right(3))) }
                val expected = EitherT.pure<NonEmptyList.F, Int>(3)

                mapped == expected
            }

            forAll { ignored: String ->
                val ot: EitherT<NonEmptyList.F, Int, String> = EitherT(NonEmptyList.of(Either.Right(ignored)))
                val mapped = ot.flatMap { EitherT(NonEmptyList.of(Either.Left(3))) }
                val expected = EitherT.impure<NonEmptyList.F, Int>(3)

                mapped == expected
            }

            forAll { ignored: String ->
                val ot = EitherT.impure<NonEmptyList.F, Int>(3)
                val mapped =  ot.flatMap{  EitherT<NonEmptyList.F, Int, Int>(NonEmptyList.of(Either.Right(2))) }
                val expected = EitherT(NonEmptyList.of(Either.Left(3)))

                mapped == expected
            }
        }

       "from option should build a correct EitherT" {
            forAll { a: String ->
                EitherT.fromEither<NonEmptyList.F, Int, String>(Either.Right(a)) == EitherT.pure<NonEmptyList.F, String>(a)
            }
        }

        "EitherTMonad.flatMap should be consistent with EitherT#flatMap" {
            forAll { a: Int ->
                val x = { b: Int -> EitherT.pure<Id.F, Int>(b * a) }
                val option = EitherT.pure<Id.F, Int>(a)
                option.flatMap(x) == EitherTMonad<Id.F, Int>(Id).flatMap(option, x)
            }
        }

        "EitherTMonad.binding should for comprehend over option" {
            val M = EitherTMonad<NonEmptyList.F, Int>(NonEmptyList)
            val result = M.binding {
                val x = !M.pure(1)
                val y = M.pure(1).bind()
                val z = bind { M.pure(1) }
                yields(x + y + z)
            }
            result shouldBe M.pure(3)
        }

        "Cartesian builder should build products over option" {
            EitherTMonad<Id.F, Int>(Id).map(EitherT.pure(1), EitherT.pure("a"), EitherT.pure(true), { (a, b, c) ->
                "$a $b $c"
            }) shouldBe EitherT.pure<Id.F, String>("1 a true")
        }

        "Cartesian builder works inside for comprehensions" {
            val M = EitherTMonad<NonEmptyList.F, Int>(NonEmptyList)
            val result = M.binding {
                val (x, y, z) = !M.tupled(M.pure(1), M.pure(1), M.pure(1))
                val a = M.pure(1).bind()
                val b = bind { M.pure(1) }
                yields(x + y + z + a + b)
            }
            result shouldBe M.pure(5)
        }
    }
}
