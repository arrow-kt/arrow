package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class WriterTTest : UnitSpec() {
    init {

        testLaws(MonadLaws.laws(WriterT.monad(NonEmptyList, IntMonoid), Eq.any()))
        testLaws(MonoidKLaws.laws<WriterF<OptionHK, Int>>(
                WriterT.monoidK(Option, OptionMonoidK()),
                WriterT.applicative(Option, IntMonoid),
                WriterT.invoke(Option(Tuple2(1, 2)), Option.monad()),
                Eq.any(),
                Eq.any()))

        "tell should accumulate write" {
            forAll { a: Int ->
                val right = WriterT(Id(NonEmptyList.of(a) toT a))
                val mapped = right.tell(NonEmptyList.of(a), NonEmptyList.semigroup<Int>()).value.ev()
                val expected = WriterT(Id(NonEmptyList.of(a, a) toT a)).value.ev()

                expected == mapped
            }
        }

        "value should accumulate value" {
            forAll { a: Int ->
                val right = WriterT(Id(NonEmptyList.of(a) toT a))
                val mapped = right.content().ev().value
                val expected = a

                expected == mapped
            }
        }

        "write should return accumulated write" {
            forAll { a: Int ->
                val right = WriterT(Id(NonEmptyList.of(a) toT a))
                val mapped = right.write().ev().value
                val expected = NonEmptyList.of(a)

                expected == mapped
            }
        }

        "reset should return write to its initial value" {
            forAll { a: Int ->
                val right = WriterT(Id(Option(NonEmptyList.of(a)) toT a))
                val mapped = right.reset(Option.monoid(NonEmptyList.semigroup<Int>())).value.ev()
                val expected: Id<Tuple2<Option<Int>, Int>> = WriterT(Id(Option.None toT a)).value.ev()

                expected == mapped
            }
        }

        "map should modify value" {
            forAll { a: Int ->
                val right = WriterT(Id(NonEmptyList.of(a) toT a))
                val mapped = right.map({ "$it power" }).value.ev()
                val expected = WriterT(Id(NonEmptyList.of(a) toT "$a power")).value.ev()

                expected == mapped
            }
        }

        "mapAcc should modify write" {
            forAll { a: Int ->
                val write = NonEmptyList.of(a)
                val right = WriterT(Id(write toT a))
                val mapped = right.mapAcc({ "$it power" }).value.ev()
                val expected = WriterT(Id("$write power" toT a)).value.ev()

                expected == mapped
            }
        }

        "bimap should modify both" {
            forAll { a: Int ->
                val write = NonEmptyList.of(a)
                val right = WriterT(Id(write toT a))
                val mapped = right.bimap({ "$it power" }, { "$it power" }).value.ev()
                val expected = WriterT(Id("$write power" toT "$a power")).value.ev()

                expected == mapped
            }
        }

        "swap should swap both" {
            forAll { a: Int ->
                val write = NonEmptyList.of(a)
                val right = WriterT(Id(write toT a))
                val mapped = right.swap().value.ev()
                val expected = WriterT(Id(a toT write)).value.ev()

                expected == mapped
            }
        }

        "flatMap should combine the writer and map the left side of the tuple" {
            forAll { a: Int ->
                val right = WriterT(NonEmptyList.of(NonEmptyList.of(a) toT a))
                val mapped = right.flatMap({ WriterT(NonEmptyList.of(NonEmptyList.of(a) toT it + 1)) }, NonEmptyList.semigroup<Int>()).value.ev()
                val expected = WriterT.both<NonEmptyList.F, NonEmptyList<Int>, Int>(NonEmptyList.of(a, a), a + 1).value.ev()

                mapped == expected
            }
        }

        "semiFlatMap should combine the writer and map the left side of the tuple" {
            forAll { num: Int ->
                val right: WriterT<IdHK, NonEmptyList<Int>, Int> = WriterT(Id(NonEmptyList.of(num) toT num))
                val calculated = right.semiflatMap({ Id(it > 0) }, NonEmptyList.semigroup<Int>()).value.ev()
                val expected = WriterT(Id(NonEmptyList.of(num, num) toT (num > 0))).value.ev()

                calculated == expected
            }
        }


        "subFlatMap should combine the writer and map the left side of the tuple" {
            forAll { num: Int ->
                val right: WriterT<IdHK, NonEmptyList<Int>, Int> = WriterT(Id(NonEmptyList.of(num) toT num))
                val calculated = right.subflatMap { NonEmptyList.of(it + 1) toT (it > 0) }
                val expected = WriterT(Id(NonEmptyList.of(num + 1) toT (num > 0)))

                calculated == expected
            }
        }

        "WriterTMonad#flatMap should be consistent with WriterT#flatMap" {
            forAll { a: Int ->
                val x = { b: Int -> WriterT.pure<IdHK, Int, Int>(b * a) }
                val option = WriterT.pure<IdHK, Int, Int>(a)
                option.flatMap(x, IntMonoid) == WriterT.monad(Id, IntMonoid).flatMap(option, x)
            }
        }

        "WriterTMonad#tailRecM should execute and terminate without blowing up the stack" {
            forAll { a: Int ->
                val value: WriterT<IdHK, Int, Int> = WriterT.monad(Id, IntMonoid).tailRecM(a) { b ->
                    WriterT.pure<IdHK, Int, Either<Int, Int>>(Either.Right(b * a))
                }.ev()
                val expected = WriterT.pure<IdHK, Int, Int>(a * a)

                expected == value
            }
        }

        "WriterTMonad#binding should for comprehend over option" {
            val M = WriterT.monad(NonEmptyList, IntMonoid)
            val result = M.binding {
                val x = !M.pure(1)
                val y = M.pure(1).bind()
                val z = bind { M.pure(1) }
                yields(x + y + z)
            }
            result shouldBe M.pure(3)
        }

        "Cartesian builder should build products over option" {
            WriterT.monad(NonEmptyList, IntMonoid).map(WriterT.pure(1), WriterT.pure("a"), WriterT.pure(true), { (a, b, c) ->
                "$a $b $c"
            }) shouldBe WriterT.pure<NonEmptyList.F, Int, String>("1 a true")
        }

        "Cartesian builder works inside for comprehensions" {
            val M = WriterT.monad(NonEmptyList, IntMonoid)
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
