package arrow.core

import arrow.core.computations.OptionEffect
import arrow.core.computations.RestrictedOptionEffect
import arrow.core.computations.option
import arrow.core.test.UnitSpec
import arrow.core.test.generators.option
import arrow.core.test.laws.FxLaws
import arrow.core.test.laws.MonoidLaws
import arrow.typeclasses.Monoid
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.checkAll
import io.kotest.property.Arb
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string

class OptionTest : UnitSpec() {

  val some: Option<String> = Some("kotlin")
  val none: Option<String> = None

  init {

    testLaws(
      MonoidLaws.laws(Monoid.option(Monoid.int()), Arb.option(Arb.int())),
      FxLaws.suspended<OptionEffect<*>, Option<String>, String>(
        Arb.string().map(Option.Companion::invoke),
        Arb.option(Arb.string()),
        Option<String>::equals,
        option::invoke
      ) {
        it.bind()
      },
      FxLaws.eager<RestrictedOptionEffect<*>, Option<String>, String>(
        Arb.string().map(Option.Companion::invoke),
        Arb.option(Arb.string()),
        Option<String>::equals,
        option::eager
      ) {
        it.bind()
      }
    )

    "bind null in option computation" {
      option {
        "s".length.bind()
      } shouldBe Some(1)
    }

    "short circuit null" {
      option {
        val number: Int = "s".length
        (number.takeIf { it > 1 }?.toString()).bind()
        throw IllegalStateException("This should not be executed")
      } shouldBe None
    }

    "fromNullable should work for both null and non-null values of nullable types" {
      checkAll { a: Int? ->
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
      some.map(String::toUpperCase) shouldBe Some("KOTLIN")
      none.map(String::toUpperCase) shouldBe None
    }

    "zip" {
      checkAll { a: Int ->
        val op: Option<Int> = a.some()
        some.zip(op) { a, b -> a + b } shouldBe Some("kotlin$a")
        none.zip(op) { a, b -> a + b } shouldBe None
        some.zip(op) shouldBe Some(Pair("kotlin", a))
      }
    }

    "mapNotNull" {
      some.mapNotNull { it.toIntOrNull() } shouldBe None
      some.mapNotNull { it.toUpperCase() } shouldBe Some("KOTLIN")
    }

    "fold" {
      some.fold({ 0 }) { it.length } shouldBe 6
      none.fold({ 0 }) { it.length } shouldBe 0
    }

    "flatMap" {
      some.flatMap { Some(it.toUpperCase()) } shouldBe Some("KOTLIN")
      none.flatMap { Some(it.toUpperCase()) } shouldBe None
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

    "firstOption" {
      val l = listOf(1, 2, 3, 4, 5, 6)
      l.firstOrNone() shouldBe Some(1)
      l.firstOrNone { it > 2 } shouldBe Some(3)
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

    "traverse should yield list of option" {
      val some: Option<String> = Some("value")
      val none: Option<String> = None
      some.traverse { listOf(it) } shouldBe listOf(Some("value"))
      none.traverse { listOf(it) } shouldBe emptyList()
    }

    "sequence should be consistent with traverse" {
      checkAll(Arb.option(Arb.int())) { option ->
        option.map { listOf(it) }.sequence() shouldBe option.traverse { listOf(it) }
      }
    }

    "traverseEither should yield either of option" {
      val some: Option<String> = Some("value")
      val none: Option<String> = None
      some.traverseEither { it.right() } shouldBe some.right()
      none.traverseEither { it.right() } shouldBe none.right()
    }

    "sequenceEither should be consistent with traverseEither" {
      checkAll(Arb.option(Arb.int())) { option ->
        option.map { it.right() }.sequenceEither() shouldBe option.traverseEither { it.right() }
      }
    }

    "traverseValidated should yield validated of option" {
      val some: Option<String> = Some("value")
      val none: Option<String> = None
      some.traverseValidated { it.valid() } shouldBe some.valid()
      none.traverseValidated { it.valid() } shouldBe none.valid()
    }

    "sequenceValidated should be consistent with traverseValidated" {
      checkAll(Arb.option(Arb.int())) { option ->
        option.map { it.valid() }.sequenceValidated() shouldBe option.traverseValidated { it.valid() }
      }
    }

    "catch should return Some(result) when f does not throw" {
      val recover: (Throwable) -> Option<Int> = { _ -> None}
      Option.catch(recover) { 1 } shouldBe Some(1)
    }

    "catch should return Some(recoverValue) when f throws" {
      val exception = Exception("Boom!")
      val recoverValue = 10
      val recover: (Throwable) -> Option<Int> = { _ -> Some(recoverValue) }
      Option.catch(recover) { throw exception } shouldBe Some(recoverValue)
    }
  }
}
