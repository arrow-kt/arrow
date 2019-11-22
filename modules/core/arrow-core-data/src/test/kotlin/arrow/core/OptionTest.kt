package arrow.core

import arrow.Kind
import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.monoid
import arrow.core.extensions.option.align.align
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.option.eqK.eqK
import arrow.core.extensions.option.foldable.foldable
import arrow.core.extensions.option.hash.hash
import arrow.core.extensions.option.monadCombine.monadCombine
import arrow.core.extensions.option.monadFilter.monadFilter
import arrow.core.extensions.option.monoid.monoid
import arrow.core.extensions.option.monoidal.monoidal
import arrow.core.extensions.option.show.show
import arrow.core.extensions.option.traverseFilter.traverseFilter
import arrow.core.extensions.option.unalign.unalign
import arrow.core.extensions.tuple2.eq.eq
import arrow.test.UnitSpec
import arrow.test.generators.option
import arrow.test.laws.AlignLaws
import arrow.test.laws.EqKLaws
import arrow.test.laws.FunctorFilterLaws
import arrow.test.laws.HashLaws
import arrow.test.laws.MonadCombineLaws
import arrow.test.laws.MonadFilterLaws
import arrow.test.laws.MonoidLaws
import arrow.test.laws.MonoidalLaws
import arrow.test.laws.ShowLaws
import arrow.test.laws.TraverseFilterLaws
import arrow.test.laws.UnalignLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe

class OptionTest : UnitSpec() {

  val some: Option<String> = Some("kotlin")
  val none: Option<String> = Option.empty()

  val associativeSemigroupalEq = object : Eq<Kind<ForOption, Tuple2<Int, Tuple2<Int, Int>>>> {
    override fun Kind<ForOption, Tuple2<Int, Tuple2<Int, Int>>>.eqv(b: Kind<ForOption, Tuple2<Int, Tuple2<Int, Int>>>): Boolean {
      return Option.eq(Tuple2.eq(Int.eq(), Tuple2.eq(Int.eq(), Int.eq()))).run {
        this@eqv.fix().eqv(b.fix())
      }
    }
  }

  init {

    testLaws(
      MonadCombineLaws.laws(Option.monadCombine(), { it.some() }, { i: Int -> { j: Int -> i + j }.some() }, Eq.any()),
      ShowLaws.laws(Option.show(), Option.eq(Int.eq())) { Some(it) },
      MonoidLaws.laws(Option.monoid(Int.monoid()), Gen.option(Gen.int()), Option.eq(Int.eq())),
      // testLaws(MonadErrorLaws.laws(monadError<ForOption, Unit>(), Eq.any(), EQ_EITHER)) TODO reenable once the MonadErrorLaws are parametric to `E`
      FunctorFilterLaws.laws(Option.traverseFilter(), { Option(it) }, Eq.any()),
      TraverseFilterLaws.laws(Option.traverseFilter(), Option.applicative(), ::Some, Eq.any()),
      MonadFilterLaws.laws(Option.monadFilter(), ::Some, Eq.any()),
      HashLaws.laws(Option.hash(Int.hash()), Option.eq(Int.eq())) { it.some() },
      MonoidalLaws.laws(Option.monoidal(), ::Some, Eq.any(), ::bijection, associativeSemigroupalEq),
      EqKLaws.laws(
        Option.eqK(),
        Option.eq(Int.eq()) as Eq<Kind<ForOption, Int>>,
        Gen.option(Gen.int()) as Gen<Kind<ForOption, Int>>
      ) {
        Option.just(it)
      },
      AlignLaws.laws(Option.align(),
        Gen.option(Gen.int()) as Gen<Kind<ForOption, Int>>,
        Option.eqK(),
        Option.foldable()
      ),
      UnalignLaws.laws(Option.unalign(),
        Gen.option(Gen.int()) as Gen<Kind<ForOption, Int>>,
        Option.eqK()
      )
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

    "map2" {
      forAll { a: Int ->
        val op: Option<Int> = a.some()
        some.map2(op) { (a, b): Tuple2<String, Int> -> a + b } == Some("kotlin$a")
        none.map2(op) { (a, b): Tuple2<String, Int> -> a + b } == None
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
      1.leftIor().toLeftOption() shouldBe Some(1)
      2.rightIor().toLeftOption() shouldBe None
      (1 toT 2).bothIor().toLeftOption() shouldBe Some(1)
    }
  }

  private fun bijection(from: Kind<ForOption, Tuple2<Tuple2<Int, Int>, Int>>): Option<Tuple2<Int, Tuple2<Int, Int>>> {
    val ot = (from as Some<Tuple2<Tuple2<Int, Int>, Int>>)
    return Tuple2(ot.t.a.a, Tuple2(ot.t.a.b, ot.t.b)).toOption()
  }
}
