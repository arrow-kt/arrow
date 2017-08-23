package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class EitherTTest : UnitSpec() {
    init {

        testLaws(MonadErrorLaws.laws(EitherT.monadError<IdHK, Throwable>(Id.monad()), Eq.any()))
        testLaws(TraverseLaws.laws(EitherT.traverse<IdHK, Int>(), EitherT.applicative(), { EitherT(Id(Either.Right(it))) }, Eq.any()))
        testLaws(SemigroupKLaws.laws<EitherTKindPartial<IdHK, Int>>(
                EitherT.semigroupK(Id.monad()),
                EitherT.applicative(Id.monad()),
                object : Eq<HK<EitherTKindPartial<IdHK, Int>, Int>> {
                    override fun eqv(a: HK<EitherTKindPartial<IdHK, Int>, Int>, b: HK<EitherTKindPartial<IdHK, Int>, Int>): Boolean =
                            a.ev() == b.ev()
                }))

        "map should modify value" {
            forAll { a: String ->
                val right = EitherT(Id(Either.Right(a)))
                val mapped = right.map({ "$it power" })
                val expected = EitherT(Id(Either.Right("$a power")))

                mapped == expected
            }
        }

        "flatMap should modify entity" {
            forAll { a: String ->
                val right = EitherT(NonEmptyList.of(Either.Right(a)))
                val mapped = right.flatMap { EitherT(NonEmptyList.of(Either.Right(3))) }
                val expected = EitherT.pure<NonEmptyListHK, Int, Int>(3)

                mapped == expected
            }

            forAll { ignored: String ->
                val right: EitherT<NonEmptyListHK, Int, String> = EitherT(NonEmptyList.of(Either.Right(ignored)))
                val mapped = right.flatMap { EitherT(NonEmptyList.of(Either.Left(3))) }
                val expected = EitherT.left<NonEmptyListHK, Int, Int>(3)

                mapped == expected
            }

            forAll { _: String ->
                val right = EitherT.left<NonEmptyListHK, Int, Int>(3)
                val mapped = right.flatMap { EitherT(NonEmptyList.of<Either<Int, Int>>(Either.Right(2))) }
                val expected = EitherT(NonEmptyList.of(Either.Left(3)))

                mapped == expected
            }
        }

        "cata should modify the return" {
            forAll { num: Int ->
                val right = EitherT.pure<NonEmptyListHK, Int, Int>(num)
                val expected = NonEmptyList.of(true)
                val result = right.cata({ false }, { true })

                expected == result
            }

            forAll { num: Int ->
                val right = EitherT.left<NonEmptyListHK, Int, Int>(num)
                val expected = NonEmptyList.of(true)
                val result = right.cata({ true }, { false })

                expected == result
            }
        }

        "semiFlatMap should map the right side of the inner either" {
            forAll { num: Int ->
                val right: EitherT<NonEmptyListHK, Int, Int> = EitherT(NonEmptyList.of(Either.Right(num)))
                val calculated = right.semiflatMap { NonEmptyList.of(it > 0) }
                val expected = EitherT(NonEmptyList.of(Either.Right(num > 0)))

                calculated == expected
            }

            forAll { num: Int ->
                val left: EitherT<NonEmptyListHK, Int, Int> = EitherT(NonEmptyList.of(Either.Left(num)))
                val calculated = left.semiflatMap { NonEmptyList.of(it > 0) }
                val expected = EitherT(NonEmptyList.of(Either.Left(num)))

                calculated == expected
            }
        }


        "subFlatMap should map the right side of the Either wrapped by EitherT" {
            forAll { num: Int ->
                val right: EitherT<NonEmptyListHK, Int, Int> = EitherT(NonEmptyList.of(Either.Right(num)))
                val calculated = right.subflatMap { Either.Right(it > 0) }
                val expected = EitherT(NonEmptyList.of(Either.Right(num > 0)))

                calculated == expected
            }

            forAll { num: Int ->
                val left: EitherT<NonEmptyListHK, Int, Int> = EitherT(NonEmptyList.of(Either.Right(num)))
                val calculated = left.subflatMap { Either.Left(num) }
                val expected = EitherT(NonEmptyList.of(Either.Left(num)))

                calculated == expected
            }

            forAll { num: Int ->
                val left: EitherT<NonEmptyListHK, Int, Int> = EitherT(NonEmptyList.of(Either.Left(num)))
                val calculated = left.subflatMap { Either.Right(num > 0) }
                val expected = EitherT(NonEmptyList.of(Either.Left(num)))

                calculated == expected
            }

            forAll { num: Int ->
                val left: EitherT<NonEmptyListHK, Int, Int> = EitherT(NonEmptyList.of(Either.Left(num)))
                val calculated = left.subflatMap { Either.Left(num + 1) }
                val expected = EitherT(NonEmptyList.of(Either.Left(num)))

                calculated == expected
            }
        }

        "exists evaluates a predicate on the right side and lifts it to the wrapped monad" {
            forAll { num: Int ->
                val right: EitherT<NonEmptyListHK, Int, Int> = EitherT(NonEmptyList.of(Either.Right(num)))
                val calculated = right.exists { it > 0 }.ev()
                val expected = NonEmptyList.of(num > 0)

                calculated == expected
            }

            forAll { num: Int ->
                val left: EitherT<NonEmptyListHK, Int, Int> = EitherT(NonEmptyList.of(Either.Left(num)))
                val calculated = left.exists { true }.ev()
                val expected = NonEmptyList.of(false)

                calculated == expected
            }
        }

        "to OptionT should transform to a correct OptionT" {
            forAll { a: String ->
                val right: EitherT<NonEmptyListHK, String, String> = EitherT(NonEmptyList.of(Either.Right(a)))
                val expected = OptionT.pure<NonEmptyListHK, String>(a)
                val calculated = right.toOptionT()

                expected == calculated
            }

            forAll { a: String ->
                val left: EitherT<NonEmptyListHK, String, String> = EitherT(NonEmptyList.of(Either.Left(a)))
                val expected = OptionT.none<NonEmptyListHK>()
                val calculated = left.toOptionT()

                expected == calculated
            }
        }

        "from option should build a correct EitherT" {
            forAll { a: String ->
                EitherT.fromEither<NonEmptyListHK, Int, String>(Either.Right(a)) == EitherT.pure<NonEmptyListHK, Int, String>(a)
            }
        }

        "EitherTMonad#flatMap should be consistent with EitherT#flatMap" {
            forAll { a: Int ->
                val x = { b: Int -> EitherT.pure<IdHK, Int, Int>(b * a) }
                val option = EitherT.pure<IdHK, Int, Int>(a)
                option.flatMap(x) == EitherT.monad<IdHK, Int>(Id.monad()).flatMap(option, x)
            }
        }

        "EitherTMonad#tailRecM should execute and terminate without blowing up the stack" {
            forAll { a: Int ->
                val value: EitherT<IdHK, Int, Int> = EitherT.monad<IdHK, Int>(Id.monad()).tailRecM(a) { b ->
                    EitherT.pure<IdHK, Int, Either<Int, Int>>(Either.Right(b * a))
                }.ev().ev()
                val expected = EitherT.pure<IdHK, Int, Int>(a * a)

                expected == value
            }

            forAll(Gen.oneOf(listOf(10000))) { limit: Int ->
                val value: EitherT<IdHK, Int, Int> = EitherT.monad<IdHK, Int>(Id.monad()).tailRecM(0) { current ->
                    if (current == limit)
                        EitherT.left(current)
                    else
                        EitherT.pure<IdHK, Int, Either<Int, Int>>(Either.Left(current + 1))
                }.ev().ev()
                val expected = EitherT.left<IdHK, Int, Int>(limit)

                expected == value
            }
        }

        "EitherT#foldL should fold with the instance of its content" {
            val eitherT = EitherT(Id(Either.Right(1)))
            val content: Id<Either<Nothing, Int>> = eitherT.value.ev()

            val expected = Id.foldable().foldL(content, 1, { a, _ -> a + 1 })
            val result = eitherT.foldL(1, { a, _ -> a + 1 }, Id.foldable())

            expected shouldBe result
        }

        "EitherT#foldR should fold with the instance of its content" {
            val eitherT = EitherT(Id(Either.Right(1)))
            val content: Id<Either<Nothing, Int>> = eitherT.value.ev()

            val expected = Id.foldable().foldR(content, Eval.now(1), { _, b-> Eval.now(b.value() + 1) })
            val result = eitherT.foldR(Eval.now(1), { a, b -> Eval.now(a + 1) }, Id.foldable())

            expected shouldBe result
        }

        "EitherT#traverse should traverse with the instance of its content" {
            val eitherT = EitherT(Id(Either.Right(1)))
            val either: Either<String, Int> = eitherT.value.ev().value()


            val f: (Int) -> Option<Int> = { Option.Some(it + 1) }
            val traverse = eitherT.traverse(f, Option.applicative(), Id.traverse(), Id.monad()).ev()
            val result = traverse.map { it.ev().value.value() }

            val expected = Either.traverse<String>().traverse(either, f, Option.applicative())
            result shouldBe expected
        }

        "EitherTMonad#binding should for comprehend over option" {
            val M = EitherT.monad<NonEmptyListHK, Int>(NonEmptyList)
            val result = M.binding {
                val x = M.pure(1).bind()
                val y = bind { M.pure(1) }
                yields(x + y)
            }
            result shouldBe M.pure(2)
        }

        "Cartesian builder should build products over option" {
            EitherT.applicative<IdHK, Int>(Id.monad()).map(EitherT.pure(1), EitherT.pure("a"), EitherT.pure(true), { (a, b, c) ->
                "$a $b $c"
            }) shouldBe EitherT.pure<IdHK, Int, String>("1 a true")
        }

        "Cartesian builder works inside for comprehensions" {
            val M = EitherT.monad<NonEmptyListHK, Int>(NonEmptyList)
            val result = M.binding {
                val (x, y, z) = bind { M.tupled(M.pure(1), M.pure(1), M.pure(1)) }
                val a = M.pure(1).bind()
                yields(x + y + z + a)
            }
            result shouldBe M.pure(4)
        }
    }
}
