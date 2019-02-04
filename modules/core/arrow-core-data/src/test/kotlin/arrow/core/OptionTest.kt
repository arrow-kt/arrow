package arrow.core

import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.monoid
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.option.fx.fx
import arrow.core.extensions.option.hash.hash
import arrow.core.extensions.option.monoid.monoid
import arrow.core.extensions.option.show.show
import arrow.mtl.extensions.option.monadFilter.monadFilter
import arrow.mtl.extensions.option.traverseFilter.traverseFilter
import arrow.syntax.collections.firstOption
import arrow.test.UnitSpec
import arrow.test.generators.option
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class OptionTest : UnitSpec() {

  val some: Option<String> = Some("kotlin")
  val none: Option<String> = Option.empty()

  init {

    testLaws(
      ShowLaws.laws(Option.show(), Option.eq(Int.eq())) { Some(it) },
      MonoidLaws.laws(Option.monoid(Int.monoid()), Gen.option(Gen.int()), Option.eq(Int.eq())),
      //testLaws(MonadErrorLaws.laws(monadError<ForOption, Unit>(), Eq.any(), EQ_EITHER)) TODO reenable once the MonadErrorLaws are parametric to `E`
      FunctorFilterLaws.laws(Option.traverseFilter(), { Option(it) }, Eq.any()),
      TraverseFilterLaws.laws(Option.traverseFilter(), Option.applicative(), ::Some, Eq.any()),
      MonadFilterLaws.laws(Option.monadFilter(), ::Some, Eq.any()),
      HashLaws.laws(Option.hash(Int.hash()), Option.eq(Int.eq())) { it.some() }
    )

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

    "forall" {
      some.forall { it.startsWith('k') } shouldBe true
      some.forall { it.startsWith('j') } shouldBe false
      none.forall { it.startsWith('k') } shouldBe true
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
      l.firstOption() shouldBe Some(1)
      l.firstOption { it > 2 } shouldBe Some(3)
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

    "fx can turn effects into pure kinded values" {
      suspend fun sideEffect(): Int =
        1
      fx {
        val (result) = effect { sideEffect() }
        result
      } shouldBe Some(1)
    }

    "fx lets thrown exception pass through for monads that can handle Throwable" {
      suspend fun sideEffect(): Int =
        throw Throwable()
      shouldThrow<Throwable> {
        fx {
          val (result) = effect { sideEffect() }
          result
        }
      }
    }

  }

}
