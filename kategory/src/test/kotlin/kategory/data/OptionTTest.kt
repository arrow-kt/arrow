package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class OptionTTest : UnitSpec() {
    init {

        val OptionTFIdEq = object : Eq<HK<OptionTKindPartial<IdHK>, Int>> {
            override fun eqv(a: HK<OptionTKindPartial<IdHK>, Int>, b: HK<OptionTKindPartial<IdHK>, Int>): Boolean {
                return a.ev().value == b.ev().value
            }
        }

        testLaws(MonadLaws.laws(OptionT.monad(NonEmptyList.monad()), Eq.any()))
        testLaws(TraverseLaws.laws(OptionT.traverse(), OptionT.applicative(Id.monad()), { OptionT(Id(it.some())) }, Eq.any()))
        testLaws(SemigroupKLaws.laws(
                OptionT.semigroupK(Id.monad()),
                OptionT.applicative(Id.monad()),
                OptionTFIdEq))

        testLaws(MonoidKLaws.laws(
                OptionT.monoidK(Option.monad()),
                OptionT.applicative(Option.monad()),
                OptionT.invoke(Option(Option(1)), Option.monad()),
                Eq.any(),
                Eq.any()))

        "map should modify value" {
            forAll { a: String ->
                val ot = OptionT(Id(Option.Some(a)))
                val mapped = ot.map({ "$it power" })
                val expected = OptionT(Id(Option.Some("$a power")))

                mapped == expected
            }
        }

        "flatMap should modify entity" {
            forAll { a: String ->
                val ot = OptionT(NonEmptyList.of(Option.Some(a)))
                val mapped = ot.flatMap { OptionT(NonEmptyList.of(Option.Some(3))) }
                val expected: OptionT<NonEmptyListHK, Int> = OptionT.pure(3)

                mapped == expected
            }

            forAll { ignored: String ->
                val ot = OptionT(NonEmptyList.of(Option.Some(ignored)))
                val mapped = ot.flatMap { OptionT(NonEmptyList.of(Option.None)) }
                val expected = OptionT.none<NonEmptyListHK>()

                mapped == expected
            }

            OptionT.none<NonEmptyListHK>()
                    .flatMap { OptionT(NonEmptyList.of(Option.Some(2))) } shouldBe OptionT(NonEmptyList.of(Option.None))
        }

        "from option should build a correct OptionT" {
            forAll { a: String ->
                OptionT.fromOption<NonEmptyListHK, String>(Option.Some(a)) == OptionT.pure<NonEmptyListHK, String>(a)
            }
        }

        "OptionTMonad.flatMap should be consistent with OptionT#flatMap" {
            forAll { a: Int ->
                val x = { b: Int -> OptionT.pure<IdHK, Int>(b * a) }
                val option = OptionT.pure<IdHK, Int>(a)
                option.flatMap(x) == OptionT.monad(Id.monad()).flatMap(option, x)
            }
        }

        "OptionTMonad.binding should for comprehend over option" {
            val M = OptionT.monad(NonEmptyList.monad())
            val result = M.binding {
                val x = M.pure(1).bind()
                val y = bind { M.pure(1) }
                yields(x + y)
            }
            result shouldBe M.pure(2)
        }

        "Cartesian builder should build products over option" {
            OptionT.applicative(Id.monad()).map(OptionT.pure(1), OptionT.pure("a"), OptionT.pure(true), { (a, b, c) ->
                "$a $b $c"
            }) shouldBe OptionT.pure<IdHK, String>("1 a true")
        }

        "Cartesian builder works inside for comprehensions" {
            val M = OptionT.monad(NonEmptyList.monad())
            val result = M.binding {
                val (x, y, z) = M.tupled(M.pure(1), M.pure(1), M.pure(1)).bind()
                val a = bind { M.pure(1) }
                yields(x + y + z + a)
            }
            result shouldBe M.pure(4)
        }
    }
}
