package arrow.core

import arrow.core.computations.OptionEffect
import arrow.core.computations.RestrictedOptionEffect
import arrow.core.computations.option
import arrow.core.test.UnitSpec
import arrow.core.test.generators.option
import arrow.core.test.laws.FxLaws
import arrow.core.test.laws.MonoidLaws
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe

class OptionTest : UnitSpec() {

  val some: Option<String> = Some("kotlin")
  val none: Option<String> = None

  init {

    testLaws(
      MonoidLaws.laws(Monoid.option(Monoid.int()), Gen.option(Gen.int())),
      FxLaws.suspended<OptionEffect<*>, Option<String>, String>(Gen.string().map(Option.Companion::invoke), Gen.option(Gen.string()), Option<String>::equals, option::invoke) {
        it.bind()
      },
      FxLaws.eager<RestrictedOptionEffect<*>, Option<String>, String>(Gen.string().map(Option.Companion::invoke), Gen.option(Gen.string()), Option<String>::equals, option::eager) {
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
      forAll { a: Int? ->
        // This seems to be generating only non-null values, so it is complemented by the next test
        val o: Option<Int> = Option.fromNullable(a)
        if (a == null) o == None else o == Some(a)
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
      forAll { a: Int ->
        val op: Option<Int> = a.some()
        some.zip(op) { a, b -> a + b } == Some("kotlin$a") &&
        none.zip(op) { a, b -> a + b } == None &&
          some.zip(op) == Some(Pair("kotlin", a))
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
  }
}
