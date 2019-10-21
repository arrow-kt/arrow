package arrow.mtl

import arrow.core.Id
import arrow.core.Tuple2
import arrow.core.value
import arrow.test.UnitSpec
import io.kotlintest.shouldBe

class ReaderTest : UnitSpec() {
  init {

    "map should return mapped value" {
      { a: Int -> a * 2 }.reader().map { it * 3 }.runId(2) shouldBe 12
    }

    "map should be callable without explicit functor instance" {
      { a: Int -> a * 2 }.reader().map { it * 3 }.runId(2) shouldBe 12
    }

    "flatMap should map over the inner value" {
      { a: Int -> a * 2 }.reader()
        .flatMap { a -> ReaderApi.just<Int, Int>(a * 3) }
        .runId(2) shouldBe 12
    }

    "flatMap should be callable without explicit monad instance" {
      { a: Int -> a * 2 }.reader()
        .flatMap { a -> ReaderApi.just<Int, Int>(a * 3) }
        .runId(2) shouldBe 12
    }

    "zip should return a new Reader zipping two given ones" {
      val r1 = { a: Int -> a * 2 }.reader()
      val r2 = { a: Int -> a * 3 }.reader()
      r1.zip(r2).runId(2) shouldBe Tuple2(4, 6)
    }

    "zip should be callable without explicit monad instance" {
      val r1 = { a: Int -> a * 2 }.reader()
      val r2 = { a: Int -> a * 3 }.reader()
      r1.zip(r2).runId(2) shouldBe Tuple2(4, 6)
    }

    "local should switch context to be able to combine Readers with different contexts" {
      val r = { a: Int -> a * 2 }.reader()
      r.local<Boolean> { if (it) 1 else 3 }.runId(false) shouldBe 6
      r.local<Boolean> { if (it) 1 else 3 }.runId(true) shouldBe 2
    }

    "reader should lift a reader from any (A) -> B function" {
      val r = { x: Int -> Id(x * 2) }.reader()
      r::class.java shouldBe Kleisli::class.java
      r.runId(2).value() shouldBe 4
    }
  }
}
