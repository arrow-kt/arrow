package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class PartialFunctionTests : UnitSpec() {

    init {

        "case syntax can be used to create partial functions" {
            val predicate = { n: Int -> n > 0 }
            val fa = case({ n: Int -> n > 0 } then { it * 2 })
            val fb = object : PartialFunction<Int, Int>() {
                override fun isDefinedAt(a: Int): Boolean = predicate(a)
                override fun invoke(p1: Int): Int = p1 * 2
            }
            fa(1) shouldBe fb(1)
            fa.isDefinedAt(-1) shouldBe false
        }

        "match statements " {
            val value: Any = "1"
            val result: Int = match(value)(
                    case(typeOf<String>() then { (it).toInt() }),
                    case(typeOf<Option.Some<Int>>() then { it.value }),
                    case(typeOf<Int>() then { it }),
                    default = { 0 }
            )
            result shouldBe 1
        }

        "match nested statements" {
            val result: Int = match("1".some())(
                    case(typeOf<String>() then { (it).toInt() }),
                    case(typeOf<Option.Some<Int>>() then { it.value + 1 }),
                    case(typeOf<Option.Some<String>>() then { it.value.toInt() * 10 }),
                    case(typeOf<Option.Some<Double>>() then { it.value.toInt() }),
                    case(typeOf<Int>() then { it }),
                    default = { 0 }
            )
            result shouldBe 10
        }

        "match nested statements" {
            val a: Option<Either<Option<String>, Int>> = "1".some().left().some()
            val result: Int = match(a)(
                    case(typeOf<String>() then { (it).toInt() }),
                    case(typeOf<Option.None>() then { 3 }),
                    case(typeOf<Option.Some<Int>>() then { it.value + 1 }),
                    case(typeOf<Option<Either<Option<Int>, Int>>>() then { it.fold({ 111.right() }, { it }).fold({ it.fold({ 222 }, { 333 }) }, { it }) }),
                    case(typeOf<Option<Either<Option<String>, Int>>>() then { it.fold({ 444.right() }, { it }).fold({ it.fold({ 555 }, { 666 }) }, { it }) }),
                    case(typeOf<Option.Some<Double>>() then { it.value.toInt() }),
                    case(typeOf<Int>() then { it }),
                    default = { 0 }
            )
            result shouldBe 666
        }

        "match with Nothing" {
            val result: Int = match("1".some())(
                    case(typeOf<String>() then { (it).toInt() }),
                    case(typeOf<Option.Some<Nothing?>>() then { 666 }),
                    case(typeOf<Option.Some<String>>() then { it.value.toInt() * 10 }),
                    case(typeOf<Option.Some<Double>>() then { it.value.toInt() }),
                    case(typeOf<Int>() then { it }),
                    default = { 0 }
            )
            result shouldBe 666
        }


        "match with Any?" {
            val result: Int = match("1".some())(
                    case(typeOf<String>() then { (it).toInt() }),
                    case(typeOf<Option.Some<Any?>>() then { 666 }),
                    case(typeOf<Option.Some<String>>() then { it.value.toInt() * 10 }),
                    case(typeOf<Option.Some<Double>>() then { it.value.toInt() }),
                    case(typeOf<Int>() then { it }),
                    default = { 0 }
            )
            result shouldBe 666
        }

    }
}
