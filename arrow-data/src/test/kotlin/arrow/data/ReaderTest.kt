package arrow.data

import arrow.core.Id
import arrow.core.Tuple2
import arrow.core.value
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith
import arrow.test.UnitSpec

@RunWith(KTestJUnitRunner::class)
class ReaderTest : UnitSpec() {
    init {

        "map should return mapped value" {
            Reader<Int, Int> { it * 2 }.map { it -> it * 3 }.runId(2) shouldBe 12
        }

        "map should be callable without explicit functor instance" {
            Reader<Int, Int> { it -> it * 2 }.map { it -> it * 3 }.runId(2) shouldBe 12
        }

        "flatMap should map over the inner value" {
            Reader<Int, Int> { it -> it * 2 }
                    .flatMap { a -> Reader().pure<Int, Int>(a * 3) }
                    .runId(2) shouldBe 12
        }

        "flatMap should be callable without explicit monad instance" {
            Reader<Int, Int> { it -> it * 2 }
                    .flatMap { a -> Reader().pure<Int, Int>(a * 3) }
                    .runId(2) shouldBe 12
        }

        "zip should return a new Reader zipping two given ones" {
            val r1 = Reader<Int, Int> { it -> it * 2 }
            val r2 = Reader<Int, Int> { it -> it * 3 }
            r1.zip(r2).runId(2) shouldBe Tuple2(4, 6)
        }

        "zip should be callable without explicit monad instance" {
            val r1 = Reader<Int, Int> { it -> it * 2 }
            val r2 = Reader<Int, Int> { it -> it * 3 }
            r1.zip(r2).runId(2) shouldBe Tuple2(4, 6)
        }

        "local should switch context to be able to combine Readers with different contexts" {
            val r = Reader<Int, Int> { it -> it * 2 }
            r.local<Boolean> { it -> if (it) 1 else 3 }.runId(false) shouldBe 6
            r.local<Boolean> { it -> if (it) 1 else 3 }.runId(true) shouldBe 2
        }

        "reader should lift a reader from any (A) -> B function" {
            val r = { x: Int -> Id(x * 2) }.reader()
            r::class.java shouldBe Kleisli::class.java
            r.runId(2).value() shouldBe 4
        }

    }
}
