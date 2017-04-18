package katz

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class OptionTTest : UnitSpec() {
    init {
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
                val mapped = ot.flatMap{ OptionT(NonEmptyList.of(Option.Some(3))) }
                val expected: OptionT<NonEmptyList.F, Int> = OptionT.pure(3)

                mapped == expected
            }

            forAll { ignored: String ->
                val ot = OptionT(NonEmptyList.of(Option.Some(ignored)))
                val mapped = ot.flatMap { OptionT(NonEmptyList.of(Option.None)) }
                val expected = OptionT.none<NonEmptyList.F>()

                mapped == expected
            }

            OptionT.none<NonEmptyList.F>()
                    .flatMap { OptionT(NonEmptyList.of(Option.Some(2))) } shouldBe OptionT(NonEmptyList.of(Option.None))
        }

        "from option should build a correct OptionT" {
            forAll { a: String ->
                OptionT.fromOption<NonEmptyList.F, String>(Option.Some(a)) == OptionT.pure<NonEmptyList.F, String>(a)
            }
        }

        "OptionTMonad.flatMap should be consistent with OptionT#flatMap" {
            forAll { a: Int ->
                val x = { b: Int -> OptionT.pure<Id.F, Int>(b * a) }
                val option = OptionT.pure<Id.F, Int>(a)
                option.flatMap(x) == OptionTMonad(Id).flatMap(option, x)
            }
        }

        "OptionTMonad.binding should for comprehend over option" {
            val M = OptionTMonad(NonEmptyList)
            val result = M.binding {
                val x = !M.pure(1)
                val y = M.pure(1).bind()
                val z = bind { M.pure(1) }
                yields(x + y + z)
            }
            result shouldBe M.pure(3)
        }

        "Cartesian builder should build products over option" {
            OptionTMonad(Id).map(OptionT.pure(1), OptionT.pure("a"), OptionT.pure(true), { (a, b, c) ->
                "$a $b $c"
            }) shouldBe OptionT.pure<Id.F, String>("1 a true")
        }

        "Cartesian builder works inside for comprehensions" {
            val M = OptionTMonad(NonEmptyList)
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
