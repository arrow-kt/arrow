package arrow.core

import arrow.core.test.either
import arrow.typeclasses.Semigroup
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.float
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.short
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

data class Tuple10<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D,
  val fifth: E,
  val sixth: F,
  val seventh: G,
  val eighth: H,
  val ninth: I,
  val tenth: J
) {

  override fun toString(): String =
    "($first, $second, $third, $fourth, $fifth, $sixth, $seventh, $eighth, $ninth, $tenth)"

  companion object
}

class EitherZipTest : StringSpec({
  "zip results in all Right transformed, or all Left combined according to combine" {
    checkAll(
      Arb.either(Arb.string(), Arb.short()),
      Arb.either(Arb.string(), Arb.byte()),
      Arb.either(Arb.string(), Arb.int()),
      Arb.either(Arb.string(), Arb.long()),
      Arb.either(Arb.string(), Arb.float()),
      Arb.either(Arb.string(), Arb.double()),
      Arb.either(Arb.string(), Arb.char()),
      Arb.either(Arb.string(), Arb.string()),
      Arb.either(Arb.string(), Arb.boolean()),
      Arb.either(Arb.string(), Arb.boolean())
    ) { a, b, c, d, e, f, g, h, i, j ->
      val res = a.zip({ e1, e2 -> "$e1$e2" }, b, c, d, e, f, g, h, i, j, ::Tuple10)
      val all = listOf(a, b, c, d, e, f, g, h, i, j)
      
      val expected = if (all.any { it.isLeft() }) {
        all.filterIsInstance<Either.Left<String>>().fold("") { acc, t -> "$acc${t.value}" }.left()
      } else {
        all.filterIsInstance<Either.Right<Any?>>().map { it.value }.let {
          Tuple10(it[0], it[1], it[2], it[3], it[4], it[5], it[6], it[7], it[8], it[9]).right()
        }
      }
      
      res shouldBe expected
    }
  }
  
  "zip without Semigroup results in all Right transformed, or all Left in a NonEmptyList" {
    checkAll(
      Arb.either(Arb.string(), Arb.short()),
      Arb.either(Arb.string(), Arb.byte()),
      Arb.either(Arb.string(), Arb.int()),
      Arb.either(Arb.string(), Arb.long()),
      Arb.either(Arb.string(), Arb.float()),
      Arb.either(Arb.string(), Arb.double()),
      Arb.either(Arb.string(), Arb.char()),
      Arb.either(Arb.string(), Arb.string()),
      Arb.either(Arb.string(), Arb.boolean()),
      Arb.either(Arb.string(), Arb.boolean())
    ) { a, b, c, d, e, f, g, h, i, j ->
      val res = a.zip(b, c, d, e, f, g, h, i, j, ::Tuple10)
      val all = listOf(a, b, c, d, e, f, g, h, i, j)
      
      val expected = if (all.any { it.isLeft() }) {
        all.filterIsInstance<Either.Left<String>>().map { it.value }.toNonEmptyListOrNull()!!.left()
      } else {
        all.filterIsInstance<Either.Right<Any?>>().map { it.value }.let {
          Tuple10(it[0], it[1], it[2], it[3], it[4], it[5], it[6], it[7], it[8], it[9]).right()
        }
      }
      
      res shouldBe expected
    }
  }
  
  "zipping EitherNel results in all Right transformed, or all Left in a NonEmptyList" {
    fun <A> Arb.Companion.nonEmptyList(arb: Arb<A>): Arb<NonEmptyList<A>> =
      Arb.list(arb, 1..100).map { it.toNonEmptyListOrNull()!! }
    
    checkAll(
      Arb.either(Arb.nonEmptyList(Arb.string()), Arb.short()),
      Arb.either(Arb.nonEmptyList(Arb.string()), Arb.byte()),
      Arb.either(Arb.nonEmptyList(Arb.string()), Arb.int()),
      Arb.either(Arb.nonEmptyList(Arb.string()), Arb.long()),
      Arb.either(Arb.nonEmptyList(Arb.string()), Arb.float()),
      Arb.either(Arb.nonEmptyList(Arb.string()), Arb.double()),
      Arb.either(Arb.nonEmptyList(Arb.string()), Arb.char()),
      Arb.either(Arb.nonEmptyList(Arb.string()), Arb.string()),
      Arb.either(Arb.nonEmptyList(Arb.string()), Arb.boolean()),
      Arb.either(Arb.nonEmptyList(Arb.string()), Arb.boolean())
    ) { a, b, c, d, e, f, g, h, i, j ->
      val res = a.zip(b, c, d, e, f, g, h, i, j, ::Tuple10)
      val all = listOf(a, b, c, d, e, f, g, h, i, j)
      
      val expected = if (all.any { it.isLeft() }) {
        all.filterIsInstance<Either.Left<NonEmptyList<String>>>()
          .flatMap { it.value }
          .toNonEmptyListOrNull()!!.left()
      } else {
        all.filterIsInstance<Either.Right<Any?>>().map { it.value }.let {
          Tuple10(it[0], it[1], it[2], it[3], it[4], it[5], it[6], it[7], it[8], it[9]).right()
        }
      }
      
      res shouldBe expected
    }
  }
  
  "Can use Semigroup as combine function" {
    Either.Left(10).zip<Int, Int, Int, Int>(
      Semigroup.int(),
      Either.Right(5)
    ) { a, b -> a + b } shouldBe Either.Left(10)
  }
})
