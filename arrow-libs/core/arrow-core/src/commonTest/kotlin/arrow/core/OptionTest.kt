package arrow.core

import arrow.core.raise.option
import arrow.core.test.laws.MonoidLaws
import arrow.core.test.option
import arrow.core.test.testLaws
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class OptionTest : StringSpec({

  val some: Option<String> = Some("kotlin")
  val none: Option<String> = None

    testLaws(
      MonoidLaws("Option", None, { x, y -> x.combine(y, Int::plus) }, Arb.option(Arb.int()))
    )

    "ensure null in option computation" {
      checkAll(Arb.boolean(), Arb.int()) { predicate, i ->
        option {
          ensure(predicate)
          i
        } shouldBe if (predicate) Some(i) else None
      }
    }

    "ensureNotNull in option computation" {
      fun square(i: Int): Int = i * i
      checkAll(Arb.int().orNull()) { i: Int? ->
        option {
          ensureNotNull(i)
          square(i) // Smart-cast by contract
        } shouldBe i.toOption().map(::square)
      }
    }

    "short circuit null" {
      option {
        val number: Int = "s".length
        ensureNotNull(number.takeIf { it > 1 })
        throw IllegalStateException("This should not be executed")
      } shouldBe None
    }

    "tap applies effects returning the original value" {
      checkAll(Arb.option(Arb.long())) { option ->
        var effect = 0
        val res = option.onSome { effect += 1 }
        val expected = when (option) {
          is Some -> 1
          is None -> 0
        }
        effect shouldBe expected
        res shouldBe option
      }
    }

    "tapNone applies effects returning the original value" {
      checkAll(Arb.option(Arb.long())) { option ->
        var effect = 0
        val res = option.onNone { effect += 1 }
        val expected = when (option) {
          is Some -> 0
          is None -> 1
        }
        effect shouldBe expected
        res shouldBe option
      }
    }

    "fromNullable should work for both null and non-null values of nullable types" {
      checkAll(Arb.int().orNull()) { a: Int? ->
        // This seems to be generating only non-null values, so it is complemented by the next test
        val o: Option<Int> = Option.fromNullable(a)
        if (a == null) o shouldBe None else o shouldBe Some(a)
      }
    }

    "fromNullable should return none for null values of nullable types" {
      val a: Int? = null
      Option.fromNullable(a) shouldBe None
    }

    "getOrElse" {
      some.getOrElse { "java" } shouldBe "kotlin"
      none.getOrElse { "java" } shouldBe "java"
    }

    "getOrNull" {
      some.getOrNull() shouldNotBe null
      none.getOrNull() shouldBe null
    }

    "map" {
      some.map(String::uppercase) shouldBe Some("KOTLIN")
      none.map(String::uppercase) shouldBe None
    }

    "fold" {
      some.fold({ 0 }) { it.length } shouldBe 6
      none.fold({ 0 }) { it.length } shouldBe 0
    }

    "flatMap" {
      some.flatMap { Some(it.uppercase()) } shouldBe Some("KOTLIN")
      none.flatMap { Some(it.uppercase()) } shouldBe None
    }

    "filter" {
      some.filter { it == "java" } shouldBe None
      none.filter { it == "java" } shouldBe None
      some.filter { it.startsWith('k') } shouldBe Some("kotlin")
    }

    "filterNot" {
      some.filterNot { it == "java" } shouldBe Some("kotlin")
      none.filterNot { it == "java" } shouldBe None
      some.filterNot { it.startsWith('k') } shouldBe None
    }

    "filterIsInstance" {
      val someAny: Option<Any> = some
      someAny.filterIsInstance<String>() shouldBe Some("kotlin")
      someAny.filterIsInstance<Int>() shouldBe None

      val someNullableAny: Option<Any?> = null.some()
      someNullableAny.filterIsInstance<String?>() shouldBe Some(null)
      someNullableAny.filterIsInstance<String>() shouldBe None

      val noneAny: Option<Any> = none
      noneAny.filterIsInstance<String>() shouldBe None
      noneAny.filterIsInstance<Int>() shouldBe None
    }

    "toList" {
      some.toList() shouldBe listOf("kotlin")
      none.toList() shouldBe listOf()
    }

    "Iterable.firstOrNone" {
      val iterable = iterableOf(1, 2, 3, 4, 5, 6)
      iterable.firstOrNone() shouldBe Some(1)
      iterable.firstOrNone { it > 2 } shouldBe Some(3)
      iterable.firstOrNone { it > 7 } shouldBe None

      val emptyIterable = iterableOf<Int>()
      emptyIterable.firstOrNone() shouldBe None

      val nullableIterable1 = iterableOf(null, 2, 3, 4, 5, 6)
      nullableIterable1.firstOrNone() shouldBe Some(null)

      val nullableIterable2 = iterableOf(1, 2, 3, null, 5, null)
      nullableIterable2.firstOrNone { it == null } shouldBe Some(null)
    }

    "Collection.firstOrNone" {
      val list = listOf(1, 2, 3, 4, 5, 6)
      list.firstOrNone() shouldBe Some(1)

      val emptyList = emptyList<Int>()
      emptyList.firstOrNone() shouldBe None

      val nullableList = listOf(null, 2, 3, 4, 5, 6)
      nullableList.firstOrNone() shouldBe Some(null)
    }

    "Iterable.singleOrNone" {
      val iterable = iterableOf(1, 2, 3, 4, 5, 6)
      iterable.singleOrNone() shouldBe None
      iterable.singleOrNone { it > 2 } shouldBe None

      val singleIterable = iterableOf(3)
      singleIterable.singleOrNone() shouldBe Some(3)
      singleIterable.singleOrNone { it == 3 } shouldBe Some(3)

      val nullableSingleIterable1 = iterableOf<Int?>(null)
      nullableSingleIterable1.singleOrNone() shouldBe Some(null)

      val nullableSingleIterable2 = iterableOf(1, 2, 3, null, 5, 6)
      nullableSingleIterable2.singleOrNone { it == null } shouldBe Some(null)

      val nullableSingleIterable3 = iterableOf(1, 2, 3, null, 5, null)
      nullableSingleIterable3.singleOrNone { it == null } shouldBe None
    }

    "Collection.singleOrNone" {
      val list = listOf(1, 2, 3, 4, 5, 6)
      list.singleOrNone() shouldBe None

      val singleList = listOf(3)
      singleList.singleOrNone() shouldBe Some(3)

      val nullableSingleList = listOf(null)
      nullableSingleList.singleOrNone() shouldBe Some(null)
    }

    "Iterable.lastOrNone" {
      val iterable = iterableOf(1, 2, 3, 4, 5, 6)
      iterable.lastOrNone() shouldBe Some(6)
      iterable.lastOrNone { it < 4 } shouldBe Some(3)
      iterable.lastOrNone { it > 7 } shouldBe None

      val emptyIterable = iterableOf<Int>()
      emptyIterable.lastOrNone() shouldBe None

      val nullableIterable1 = iterableOf(1, 2, 3, 4, 5, null)
      nullableIterable1.lastOrNone() shouldBe Some(null)

      val nullableIterable2 = iterableOf(null, 2, 3, null, 5, 6)
      nullableIterable2.lastOrNone { it == null } shouldBe Some(null)
    }

    "Collection.lastOrNone" {
      val list = listOf(1, 2, 3, 4, 5, 6)
      list.lastOrNone() shouldBe Some(6)

      val emptyList = emptyList<Int>()
      emptyList.lastOrNone() shouldBe None

      val nullableList = listOf(1, 2, 3, 4, 5, null)
      nullableList.lastOrNone() shouldBe Some(null)
    }

    "Iterable.elementAtOrNone" {
      val iterable = iterableOf(1, 2, 3, 4, 5, 6)
      iterable.elementAtOrNone(index = 3 - 1) shouldBe Some(3)
      iterable.elementAtOrNone(index = -1) shouldBe None
      iterable.elementAtOrNone(index = 100) shouldBe None

      val nullableIterable = iterableOf(1, 2, null, 4, 5, 6)
      nullableIterable.elementAtOrNone(index = 3 - 1) shouldBe Some(null)
    }

    "Collection.elementAtOrNone" {
      val list = listOf(1, 2, 3, 4, 5, 6)
      list.elementAtOrNone(index = 3 - 1) shouldBe Some(3)
      list.elementAtOrNone(index = -1) shouldBe None
      list.elementAtOrNone(index = 100) shouldBe None

      val nullableList = listOf(1, 2, null, 4, 5, 6)
      nullableList.elementAtOrNone(index = 3 - 1) shouldBe Some(null)
    }

    "toLeftOption" {
      1.leftIor().leftOrNull() shouldBe 1
      2.rightIor().leftOrNull() shouldBe null
      (1 to 2).bothIor().leftOrNull() shouldBe 1
    }

    "Option<Pair<L, R>>.toMap()" {
      val some: Option<Pair<String, String>> = Some("key" to "value")
      val none: Option<Pair<String, String>> = None
      some.toMap() shouldBe mapOf("key" to "value")
      none.toMap() shouldBe emptyMap()
    }

    "catch should return Some(result) when f does not throw" {
      val recover: (Throwable) -> Option<Int> = { _ -> None}
      Option.catch(recover) { 1 } shouldBe Some(1)
    }

    "catch with default recover should return Some(result) when f does not throw" {
      Option.catch { 1 } shouldBe Some(1)
    }

    "catch should return Some(recoverValue) when f throws" {
      val exception = Exception("Boom!")
      val recoverValue = 10
      val recover: (Throwable) -> Option<Int> = { _ -> Some(recoverValue) }
      Option.catch(recover) { throw exception } shouldBe Some(recoverValue)
    }

    "catch should return None when f throws" {
      val exception = Exception("Boom!")
      Option.catch { throw exception } shouldBe None
    }

    "invoke operator should return Some" {
      checkAll(Arb.int()) { a: Int ->
        Option(a) shouldBe Some(a)
      }
    }

    "isNone should return true if None and false if Some" {
      none.isNone() shouldBe true
      none.isSome() shouldBe false
    }

    "isSome should return true if Some and false if None" {
      some.isSome() shouldBe true
      some.isNone() shouldBe false
    }

    "isSome with predicate" {
      some.isSome { it.startsWith('k') } shouldBe true
      some.isSome { it.startsWith('j') } shouldBe false
      none.isSome { it.startsWith('k') } shouldBe false
    }

    "flatten" {
      checkAll(Arb.int()) { a: Int ->
        Some(Some(a)).flatten() shouldBe Some(a)
        Some(None).flatten() shouldBe None
      }
    }

    "widen" {
      checkAll(Arb.string()) { a: String ->
        val widen: Option<CharSequence> = Option(a).widen()
        widen.map { it.length } shouldBe Some(a.length)
      }
    }

    "compareTo with Some values" {
      checkAll(Arb.int(), Arb.int()) { a: Int, b: Int ->
        val opA = Option(a)
        val opB = Option(b)
        (opA > opB) shouldBe (a > b)
        (opA >= opB) shouldBe (a >= b)
        (opA < opB) shouldBe (a < b)
        (opA <= opB) shouldBe (a <= b)
        (opA == opB) shouldBe (a == b)
        (opA != opB) shouldBe (a != b)
      }
    }

    "compareTo with None values" {
      val opA = Option(1)
      val opB = None
      (opA > opB) shouldBe true
      (opA >= opB) shouldBe true
      (opA < opB) shouldBe false
      (opA <= opB) shouldBe false
      (opA == opB) shouldBe false
      (opA != opB) shouldBe true

      (none > some) shouldBe false
      (none >= some) shouldBe false
      (none < some) shouldBe true
      (none <= some) shouldBe true
      (none == some) shouldBe false
      (none != some) shouldBe true
    }
})

// Utils

private fun <T> iterableOf(vararg elements: T): Iterable<T> = Iterable { iterator { yieldAll(elements.toList()) } }
