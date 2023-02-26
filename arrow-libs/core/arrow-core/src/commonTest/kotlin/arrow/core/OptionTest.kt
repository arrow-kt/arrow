package arrow.core

import arrow.core.raise.option
import arrow.core.test.laws.MonoidLaws
import arrow.core.test.option
import arrow.core.test.testLaws
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.orNull
import io.kotest.property.checkAll

class OptionTest : StringSpec({

  val some: Option<String> = Some("kotlin")
  val none: Option<String> = None

    testLaws(
      MonoidLaws.laws(Monoid.option(Monoid.int()), Arb.option(Arb.int())),
    )
    testLaws(
      MonoidLaws.laws(Monoid.option(Semigroup.option(Monoid.int())), Arb.option(Arb.option(Arb.int()))),
    )

    testLaws(
      MonoidLaws.laws(Monoid.option(Semigroup.option(Monoid.option(Monoid.int()))), Arb.option(Arb.option(Arb.option(Arb.int())))),
    )

    testLaws(
      MonoidLaws.laws(Monoid.option(Monoid.option(Semigroup.option(Monoid.int()))), Arb.option(Arb.option(Arb.option(Arb.int())))),
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
      option<Any?> {
        val number: Int = "s".length
        ensureNotNull(number.takeIf { it > 1 })
        throw IllegalStateException("This should not be executed")
      } shouldBe None
    }

    "tap applies effects returning the original value" {
      checkAll(Arb.option(Arb.long())) { option ->
        var effect = 0
        val res = option.tap { effect += 1 }
        val expected = option.fold({ 0 }, { 1 })
        effect shouldBe expected
        res shouldBe option
      }
    }

    "tapNone applies effects returning the original value" {
      checkAll(Arb.option(Arb.long())) { option ->
        var effect = 0
        val res = option.tapNone { effect += 1 }
        val expected = option.fold({ 1 }, { 0 })
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

    "orNull" {
      some.orNull() shouldNotBe null
      none.orNull() shouldBe null
    }

    "map" {
      some.map(String::uppercase) shouldBe Some("KOTLIN")
      none.map(String::uppercase) shouldBe None
    }

    "zip" {
      checkAll(Arb.int()) { a: Int ->
        val op: Option<Int> = a.some()
        some.zip(op) { a, b -> a + b } shouldBe Some("kotlin$a")
        none.zip(op) { a, b -> a + b } shouldBe None
        some.zip(op) shouldBe Some(Pair("kotlin", a))
      }
    }

    "mapNotNull" {
      some.mapNotNull { it.toIntOrNull() } shouldBe None
      some.mapNotNull { it.uppercase() } shouldBe Some("KOTLIN")
    }

    "fold" {
      some.fold({ 0 }) { it.length } shouldBe 6
      none.fold({ 0 }) { it.length } shouldBe 0
    }

    "flatMap" {
      some.flatMap { Some(it.uppercase()) } shouldBe Some("KOTLIN")
      none.flatMap { Some(it.uppercase()) } shouldBe None
    }

    "align" {
      some align some shouldBe Some(Ior.Both("kotlin", "kotlin"))
      some align none shouldBe Some(Ior.Left("kotlin"))
      none align some shouldBe Some(Ior.Right("kotlin"))
      none align none shouldBe None

      some.align(some) { "$it" } shouldBe Some("Ior.Both(kotlin, kotlin)")
      some.align(none) { "$it" } shouldBe Some("Ior.Left(kotlin)")
      none.align(some) { "$it" } shouldBe Some("Ior.Right(kotlin)")
      none.align(none) { "$it" } shouldBe None

      val nullable: Option<Any?> = null.some()
      some align nullable shouldBe Some(Ior.Both("kotlin", null))
      nullable align some shouldBe Some(Ior.Both(null, "kotlin"))
      nullable align nullable shouldBe Some(Ior.Both(null, null))

      some.align(nullable) { "$it" } shouldBe Some("Ior.Both(kotlin, null)")
      nullable.align(some) { "$it" } shouldBe Some("Ior.Both(null, kotlin)")
      nullable.align(nullable) { "$it" } shouldBe Some("Ior.Both(null, null)")
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
      someNullableAny.filterIsInstance<String?>() shouldBe Some<Any?>(null)
      someNullableAny.filterIsInstance<String>() shouldBe None

      val noneAny: Option<Any> = none
      noneAny.filterIsInstance<String>() shouldBe None
      noneAny.filterIsInstance<Int>() shouldBe None
    }

    "exists" {
      some.exists { it.startsWith('k') } shouldBe true
      some.exists { it.startsWith('j') } shouldBe false
      none.exists { it.startsWith('k') } shouldBe false
    }

    "all" {
      some.all { it.startsWith('k') } shouldBe true
      some.all { it.startsWith('j') } shouldBe false
      none.all { it.startsWith('k') } shouldBe true
    }

    "orElse" {
      some.orElse { Some("java") } shouldBe Some("kotlin")
      none.orElse { Some("java") } shouldBe Some("java")
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
      nullableIterable1.firstOrNone() shouldBe Some<Any?>(null)

      val nullableIterable2 = iterableOf(1, 2, 3, null, 5, null)
      nullableIterable2.firstOrNone { it == null } shouldBe Some<Any?>(null)
    }

    "Collection.firstOrNone" {
      val list = listOf(1, 2, 3, 4, 5, 6)
      list.firstOrNone() shouldBe Some(1)

      val emptyList = emptyList<Int>()
      emptyList.firstOrNone() shouldBe None

      val nullableList = listOf(null, 2, 3, 4, 5, 6)
      nullableList.firstOrNone() shouldBe Some<Any?>(null)
    }

    "Iterable.singleOrNone" {
      val iterable = iterableOf(1, 2, 3, 4, 5, 6)
      iterable.singleOrNone() shouldBe None
      iterable.singleOrNone { it > 2 } shouldBe None

      val singleIterable = iterableOf(3)
      singleIterable.singleOrNone() shouldBe Some(3)
      singleIterable.singleOrNone { it == 3 } shouldBe Some(3)

      val nullableSingleIterable1 = iterableOf<Int?>(null)
      nullableSingleIterable1.singleOrNone() shouldBe Some<Any?>(null)

      val nullableSingleIterable2 = iterableOf(1, 2, 3, null, 5, 6)
      nullableSingleIterable2.singleOrNone { it == null } shouldBe Some<Any?>(null)

      val nullableSingleIterable3 = iterableOf(1, 2, 3, null, 5, null)
      nullableSingleIterable3.singleOrNone { it == null } shouldBe None
    }

    "Collection.singleOrNone" {
      val list = listOf(1, 2, 3, 4, 5, 6)
      list.singleOrNone() shouldBe None

      val singleList = listOf(3)
      singleList.singleOrNone() shouldBe Some(3)

      val nullableSingleList: List<Any?> = listOf(null)
      nullableSingleList.singleOrNone() shouldBe Some<Any?>(null)
    }

    "Iterable.lastOrNone" {
      val iterable = iterableOf(1, 2, 3, 4, 5, 6)
      iterable.lastOrNone() shouldBe Some(6)
      iterable.lastOrNone { it < 4 } shouldBe Some(3)
      iterable.lastOrNone { it > 7 } shouldBe None

      val emptyIterable = iterableOf<Int>()
      emptyIterable.lastOrNone() shouldBe None

      val nullableIterable1 = iterableOf(1, 2, 3, 4, 5, null)
      nullableIterable1.lastOrNone() shouldBe Some<Any?>(null)

      val nullableIterable2 = iterableOf(null, 2, 3, null, 5, 6)
      nullableIterable2.lastOrNone { it == null } shouldBe Some<Any?>(null)
    }

    "Collection.lastOrNone" {
      val list = listOf(1, 2, 3, 4, 5, 6)
      list.lastOrNone() shouldBe Some(6)

      val emptyList = emptyList<Int>()
      emptyList.lastOrNone() shouldBe None

      val nullableList = listOf(1, 2, 3, 4, 5, null)
      nullableList.lastOrNone() shouldBe Some<Any?>(null)
    }

    "Iterable.elementAtOrNone" {
      val iterable = iterableOf(1, 2, 3, 4, 5, 6)
      iterable.elementAtOrNone(index = 3 - 1) shouldBe Some(3)
      iterable.elementAtOrNone(index = -1) shouldBe None
      iterable.elementAtOrNone(index = 100) shouldBe None

      val nullableIterable = iterableOf(1, 2, null, 4, 5, 6)
      nullableIterable.elementAtOrNone(index = 3 - 1) shouldBe Some<Any?>(null)
    }

    "Collection.elementAtOrNone" {
      val list = listOf(1, 2, 3, 4, 5, 6)
      list.elementAtOrNone(index = 3 - 1) shouldBe Some(3)
      list.elementAtOrNone(index = -1) shouldBe None
      list.elementAtOrNone(index = 100) shouldBe None

      val nullableList = listOf(1, 2, null, 4, 5, 6)
      nullableList.elementAtOrNone(index = 3 - 1) shouldBe Some<Any?>(null)
    }

    "and" {
      val x = Some(2)
      val y = Some("Foo")
      x and y shouldBe Some("Foo")
      x and None shouldBe None
      None and x shouldBe None
      None and None shouldBe None
    }

    "or" {
      val x = Some(2)
      val y = Some(100)
      x or y shouldBe Some(2)
      x or None shouldBe Some(2)
      None or x shouldBe Some(2)
      None or None shouldBe None
    }

    "toLeftOption" {
      1.leftIor().leftOrNull() shouldBe 1
      2.rightIor().leftOrNull() shouldBe null
      (1 to 2).bothIor().leftOrNull() shouldBe 1
    }

    "pairLeft" {
      val some: Option<Int> = Some(2)
      val none: Option<Int> = None
      some.pairLeft("key") shouldBe Some("key" to 2)
      none.pairLeft("key") shouldBe None
    }

    "pairRight" {
      val some: Option<Int> = Some(2)
      val none: Option<Int> = None
      some.pairRight("right") shouldBe Some(2 to "right")
      none.pairRight("right") shouldBe None
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
      Option.catch<Any?> { throw exception } shouldBe None
    }
})

// Utils

private fun <T> iterableOf(vararg elements: T): Iterable<T> = Iterable { iterator { yieldAll(elements.toList()) } }
