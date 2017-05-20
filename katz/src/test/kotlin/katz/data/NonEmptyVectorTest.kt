package katz

import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import java.util.*

@RunWith(KTestJUnitRunner::class)
class NonEmptyVectorTest : UnitSpec() {
    init {
        "map should modify values" {
            NonEmptyVector.of(14).map { it * 3 } shouldBe NonEmptyVector.of(42)
        }

        "flatMap should work" {
            val nel = NonEmptyVector.of(1, 2)
            val nel2 = nel.flatMap { it -> NonEmptyVector.of(it, it) }
            nel2 shouldBe NonEmptyVector.of(1, 1, 2, 2)
        }

        "NonEmptyVector.flatMap should be consistent with NonEmptyVector#flatMap" {
            val nel = NonEmptyVector.of(1, 2)
            val nel2 = NonEmptyVector.of(1, 2)
            nel.flatMap { nel2 } shouldBe NonEmptyVector.flatMap(nel) { nel2 }
        }

        "NonEmptyVector.binding should for comprehend over NonEmptyVector" {
            val result = NonEmptyVector.binding {
                val x = !NonEmptyVector.of(1)
                val y = NonEmptyVector.of(2).bind()
                val z = bind { NonEmptyVector.of(3) }
                yields(x + y + z)
            }
            result shouldBe NonEmptyVector.of(6)
        }

        "NonEmptyVector.binding should for comprehend over complex NonEmptyVector" {
            val result = NonEmptyVector.binding {
                val x = !NonEmptyVector.of(1, 2)
                val y = NonEmptyVector.of(3).bind()
                val z = bind { NonEmptyVector.of(4) }
                yields(x + y + z)
            }
            result shouldBe NonEmptyVector.of(8, 9)
        }

        "NonEmptyVector.binding should for comprehend over all values of multiple NonEmptyVector" {
            forAll(Gen.int(), ArrayGenerator, { a: Int, b: Array<Int> ->
                val nel: NonEmptyVector<Int> = NonEmptyVector(a, b)
                val nel2 = NonEmptyVector.of(1, 2)
                val nel3 = NonEmptyVector.of(3, 4, 5)
                val result: HK<NonEmptyVector.F, Int> = NonEmptyVector.binding {
                    val x = !nel
                    val y = nel2.bind()
                    val z = bind { nel3 }
                    yields(x + y + z)
                }
                result.ev().size == nel.size * nel2.size * nel3.size
            })
        }

        "NonEmptyVector.cobinding should for comprehend over NonEmptyVector" {
            val result = NonEmptyVector.cobinding {
                val x = !NonEmptyVector.of(1)
                val y = NonEmptyVector.of(2).extract()
                val z = extract { NonEmptyVector.of(3) }
                yields(x + y + z)
            }
            result shouldBe 6
        }

        "NonEmptyVector.cobinding should for comprehend over complex NonEmptyVector" {
            val result = NonEmptyVector.cobinding {
                val x = !NonEmptyVector.of(1, 2)
                val y = NonEmptyVector.of(3).extract()
                val z = extract { NonEmptyVector.of(4) }
                yields(x + y + z)
            }
            result shouldBe 8
        }

        "NonEmptyVectorComonad.cobinding should for comprehend over all values of multiple NonEmptyVector" {
            forAll(Gen.int(), ArrayGenerator, { a: Int, b: Array<Int> ->
                val nel: NonEmptyVector<Int> = NonEmptyVector(a, b)
                val nel2 = NonEmptyVector.of(1, 2)
                val nel3 = NonEmptyVector.of(3, 4, 5)
                val result: Int = NonEmptyVector.cobinding {
                    val x = !nel
                    val y = nel2.extract()
                    val z = extract { nel3 }
                    yields(x + y + z)
                }
                result == 1 + 3 + a
            })
        }

        "NonEmptyVectorComonad.duplicate should create an instance of NonEmptyVector<NonEmptyVector<A>>" {
            NonEmptyVector.duplicate(NonEmptyVector.of(3)) shouldBe NonEmptyVector.of(NonEmptyVector.of(3))
        }
    }

    object ArrayGenerator : Gen<Array<Int>> {
        override fun generate(): Array<Int> = Array(Gen.choose(0,100).generate()) { Gen.int().generate() }
    }

}