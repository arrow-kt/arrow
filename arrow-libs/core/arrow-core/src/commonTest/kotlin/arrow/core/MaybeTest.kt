package arrow.core

import arrow.core.continuations.ensureNotNull
import arrow.core.continuations.maybe
import arrow.core.traverse
import arrow.core.test.UnitSpec
import arrow.core.test.generators.maybe
import arrow.core.test.laws.MonoidLaws
import arrow.typeclasses.Monoid
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.orNull

class MaybeTest : UnitSpec() {

  val just: Maybe<String> = Just("kotlin")
  val nothing: Maybe<String> = Maybe.Nothing

  init {

    testLaws(MonoidLaws.laws(Monoid.maybe(Monoid.int()), Arb.maybe(Arb.int())))

    "ensure null in maybe computation" {
      checkAll(Arb.boolean(), Arb.int()) { predicate, i ->
        maybe {
          ensure(predicate)
          i
        } shouldBe if (predicate) Just(i) else Maybe.Nothing
      }
    }

    "ensureNotNull in maybe computation" {
      fun square(i: Int): Int = i * i
      checkAll(Arb.int().orNull()) { i: Int? ->
        maybe {
          val ii = i
          ensureNotNull(ii)
          square(ii) // Smart-cast by contract
        } shouldBe i.toMaybe().map(::square)
      }
    }

    "short circuit null" {
      maybe {
        val number: Int = "s".length
        ensureNotNull(number.takeIf { it > 1 })
        throw IllegalStateException("This should not be executed")
      } shouldBe Maybe.Nothing
    }

    "tap applies effects returning the original value" {
      checkAll(Arb.long().orNull()) { long ->
        val maybe = long.toMaybe()
        var effect = 0
        val res = maybe.tap { effect += 1 }
        val expected = maybe.fold({ 0 }, { 1 })
        effect shouldBe expected
        res shouldBe maybe
      }
    }

    "tapNothing applies effects returning the original value" {
      checkAll(Arb.long().orNull()) { long ->
        val maybe = long.toMaybe()
        var effect = 0
        val res = maybe.tapNothing { effect += 1 }
        val expected = maybe.fold({ 1 }, { 0 })
        effect shouldBe expected
        res shouldBe maybe
      }
    }

    "fromNullable should work for both null and non-null values of nullable types" {
      checkAll(Arb.int().orNull()) { a: Int? ->
        // This seems to be generating only non-null values, so it is complemented by the next test
        val o: Maybe<Int> = Maybe.fromNullable(a)
        if (a == null) o shouldBe Maybe.Nothing else o shouldBe Just(a)
      }
    }

    "fromNullable should return maybe.Nothing for null values of nullable types" {
      val a: Int? = null
      Maybe.fromNullable(a) shouldBe Maybe.Nothing
    }

    "getOrElse" {
      just.getOrElse { "java" } shouldBe "kotlin"
      nothing.getOrElse { "java" } shouldBe "java"
    }

    "orNull" {
      just.orNull() shouldNotBe null
      nothing.orNull() shouldBe null
    }

    "map" {
      just.map(String::uppercase) shouldBe Just("KOTLIN")
      nothing.map(String::uppercase) shouldBe Maybe.Nothing
    }

    "zip" {
      checkAll(Arb.int()) { a: Int ->
        val op: Maybe<Int> = a.just()
        just.zip(op) { a, b -> a + b } shouldBe Just("kotlin$a")
        nothing.zip(op) { a, b -> a + b } shouldBe Maybe.Nothing
        just.zip(op) shouldBe Just(Pair("kotlin", a))
      }
    }

    "mapNotNull" {
      just.mapNotNull { it.toIntOrNull() } shouldBe Maybe.Nothing
      just.mapNotNull { it.uppercase() } shouldBe Just("KOTLIN")
    }

    "fold" {
      just.fold({ 0 }) { it.length } shouldBe 6
      nothing.fold({ 0 }) { it.length } shouldBe 0
    }

    "flatMap" {
      just.flatMap { Just(it.uppercase()) } shouldBe Just("KOTLIN")
      nothing.flatMap { Just(it.uppercase()) } shouldBe Maybe.Nothing
    }

    "align" {
      just align just shouldBe Just(Ior.Both("kotlin", "kotlin"))
      just align nothing shouldBe Just(Ior.Left("kotlin"))
      nothing align just shouldBe Just(Ior.Right("kotlin"))
      nothing align nothing shouldBe Maybe.Nothing

      just.align(just) { "$it" } shouldBe Just("Ior.Both(kotlin, kotlin)")
      just.align(nothing) { "$it" } shouldBe Just("Ior.Left(kotlin)")
      nothing.align(just) { "$it" } shouldBe Just("Ior.Right(kotlin)")
      nothing.align(nothing) { "$it" } shouldBe Maybe.Nothing

      val nullable = Just(Maybe.fromNullable(null))
      just align nullable shouldBe Just(Ior.Both("kotlin", Maybe.Nothing))
      nullable align just shouldBe Just(Ior.Both(Maybe.Nothing, "kotlin"))
      nullable align nullable shouldBe Just(Ior.Both(Maybe.Nothing, Maybe.Nothing))

      just.align(nullable) { "$it" } shouldBe Just("Ior.Both(kotlin, Maybe.Nothing)")
      nullable.align(just) { "$it" } shouldBe Just("Ior.Both(Maybe.Nothing, kotlin)")
      nullable.align(nullable) { "$it" } shouldBe Just("Ior.Both(Maybe.Nothing, Maybe.Nothing)")
    }

    "filter" {
      just.filter { it == "java" } shouldBe Maybe.Nothing
      nothing.filter { it == "java" } shouldBe Maybe.Nothing
      just.filter { it.startsWith('k') } shouldBe Just("kotlin")
    }

    "filterNot" {
      just.filterNot { it == "java" } shouldBe Just("kotlin")
      nothing.filterNot { it == "java" } shouldBe Maybe.Nothing
      just.filterNot { it.startsWith('k') } shouldBe Maybe.Nothing
    }

    "filterIsInstance" {
      val justAny: Maybe<Any> = just
      justAny.filterIsInstance<String>() shouldBe Just("kotlin")
      justAny.filterIsInstance<Int>() shouldBe Maybe.Nothing

      val nothingAny: Maybe<Any> = nothing
      nothingAny.filterIsInstance<String>() shouldBe Maybe.Nothing
      nothingAny.filterIsInstance<Int>() shouldBe Maybe.Nothing
    }

    "exists" {
      just.exists { it.startsWith('k') } shouldBe true
      just.exists { it.startsWith('j') } shouldBe false
      nothing.exists { it.startsWith('k') } shouldBe false
    }

    "all" {
      just.all { it.startsWith('k') } shouldBe true
      just.all { it.startsWith('j') } shouldBe false
      nothing.all { it.startsWith('k') } shouldBe true
    }

    "orElse" {
      just.orElse { Just("java") } shouldBe Just("kotlin")
      nothing.orElse { Just("java") } shouldBe Just("java")
    }

    "toList" {
      just.toList() shouldBe listOf("kotlin")
      nothing.toList() shouldBe listOf()
    }

    "Iterable.firstOrNothing" {
      val iterable = iterableOf(1, 2, 3, 4, 5, 6)
      iterable.firstOrNothing() shouldBe Just(1)
      iterable.firstOrNothing { it > 2 } shouldBe Just(3)
      iterable.firstOrNothing { it > 7 } shouldBe Maybe.Nothing

      val emptyIterable = iterableOf<Int>()
      emptyIterable.firstOrNothing() shouldBe Maybe.Nothing

      val nullableIterable1 = iterableOf(null, 2, 3, 4, 5, 6).map { it.toMaybe() }
      nullableIterable1.firstOrNothing() shouldBe Just(Maybe.Nothing)

      val nullableIterable2 = iterableOf(1, 2, 3, null, 5, null).map { it.toMaybe() }
      nullableIterable2.firstOrNothing { it == Maybe.Nothing } shouldBe Just(Maybe.Nothing)
    }

    "Collection.firstOrNothing" {
      val list = listOf(1, 2, 3, 4, 5, 6)
      list.firstOrNothing() shouldBe Just(1)

      val emptyList = emptyList<Int>()
      emptyList.firstOrNothing() shouldBe Maybe.Nothing

      val nullableList = listOf(null, 2, 3, 4, 5, 6).map { it.toMaybe() }
      nullableList.firstOrNothing() shouldBe Just(Maybe.Nothing)
    }

    "Iterable.singleOrNothing" {
      val iterable = iterableOf(1, 2, 3, 4, 5, 6)
      iterable.singleOrNothing() shouldBe Maybe.Nothing
      iterable.singleOrNothing { it > 2 } shouldBe Maybe.Nothing

      val singleIterable = iterableOf(3)
      singleIterable.singleOrNothing() shouldBe Just(3)
      singleIterable.singleOrNothing { it == 3 } shouldBe Just(3)

      val nullableSingleIterable1 = iterableOf<Int?>(null).map { it.toMaybe() }
      nullableSingleIterable1.singleOrNothing() shouldBe Just(Maybe.Nothing)

      val nullableSingleIterable2 = iterableOf(1, 2, 3, null, 5, 6).map { it.toMaybe() }
      nullableSingleIterable2.singleOrNothing { it == Maybe.Nothing } shouldBe Just(Maybe.Nothing)

      val nullableSingleIterable3 = iterableOf(1, 2, 3, null, 5, null).map { it.toMaybe() }
      nullableSingleIterable3.singleOrNothing { it == Maybe.Nothing } shouldBe Maybe.Nothing
    }

    "Collection.singleOrNothing" {
      val list = listOf(1, 2, 3, 4, 5, 6)
      list.singleOrNothing() shouldBe Maybe.Nothing

      val singleList = listOf(3)
      singleList.singleOrNothing() shouldBe Just(3)

      val nullableSingleList = listOf(null).map { it.toMaybe() }
      nullableSingleList.singleOrNothing() shouldBe Just(Maybe.Nothing)
    }

    "Iterable.lastOrNothing" {
      val iterable = iterableOf(1, 2, 3, 4, 5, 6)
      iterable.lastOrNothing() shouldBe Just(6)
      iterable.lastOrNothing { it < 4 } shouldBe Just(3)
      iterable.lastOrNothing { it > 7 } shouldBe Maybe.Nothing

      val emptyIterable = iterableOf<Int>()
      emptyIterable.lastOrNothing() shouldBe Maybe.Nothing

      val nullableIterable1 = iterableOf(1, 2, 3, 4, 5, null).map { it.toMaybe() }
      nullableIterable1.lastOrNothing() shouldBe Just(Maybe.Nothing)

      val nullableIterable2 = iterableOf(null, 2, 3, null, 5, 6).map { it.toMaybe() }
      nullableIterable2.lastOrNothing { it == Maybe.Nothing } shouldBe Just(Maybe.Nothing)
    }

    "Collection.lastOrNothing" {
      val list = listOf(1, 2, 3, 4, 5, 6)
      list.lastOrNothing() shouldBe Just(6)

      val emptyList = emptyList<Int>()
      emptyList.lastOrNothing() shouldBe Maybe.Nothing

      val nullableList = listOf(1, 2, 3, 4, 5, null).map { it.toMaybe() }
      nullableList.lastOrNothing() shouldBe Just(Maybe.Nothing)
    }

    "Iterable.elementAtOrNothing" {
      val iterable = iterableOf(1, 2, 3, 4, 5, 6)
      iterable.elementAtOrNothing(index = 3 - 1) shouldBe Just(3)
      iterable.elementAtOrNothing(index = -1) shouldBe Maybe.Nothing
      iterable.elementAtOrNothing(index = 100) shouldBe Maybe.Nothing

      val nullableIterable = iterableOf(1, 2, null, 4, 5, 6).map { it.toMaybe() }
      nullableIterable.elementAtOrNothing(index = 3 - 1) shouldBe Just(Maybe.Nothing)
    }

    "Collection.elementAtOrNothing" {
      val list = listOf(1, 2, 3, 4, 5, 6)
      list.elementAtOrNothing(index = 3 - 1) shouldBe Just(3)
      list.elementAtOrNothing(index = -1) shouldBe Maybe.Nothing
      list.elementAtOrNothing(index = 100) shouldBe Maybe.Nothing

      val nullableList = listOf(1, 2, null, 4, 5, 6).map { it.toMaybe() }
      nullableList.elementAtOrNothing(index = 3 - 1) shouldBe Just(Maybe.Nothing)
    }

    "and" {
      val x = Just(2)
      val y = Just("Foo")
      x and y shouldBe Just("Foo")
      x and Maybe.Nothing shouldBe Maybe.Nothing
      Maybe.Nothing and x shouldBe Maybe.Nothing
      Maybe.Nothing and Maybe.Nothing shouldBe Maybe.Nothing
    }

    "or" {
      val x = Just(2)
      val y = Just(100)
      x or y shouldBe Just(2)
      x or Maybe.Nothing shouldBe Just(2)
      Maybe.Nothing or x shouldBe Just(2)
      Maybe.Nothing or Maybe.Nothing shouldBe Maybe.Nothing
    }

    "toLeftMaybe" {
      1.leftIor().leftOrNull() shouldBe 1
      2.rightIor().leftOrNull() shouldBe null
      (1 to 2).bothIor().leftOrNull() shouldBe 1
    }

    "pairLeft" {
      val just: Maybe<Int> = Just(2)
      val nothing: Maybe<Int> = Maybe.Nothing
      just.pairLeft("key") shouldBe Just("key" to 2)
      nothing.pairLeft("key") shouldBe Maybe.Nothing
    }

    "pairRight" {
      val just: Maybe<Int> = Just(2)
      val nothing: Maybe<Int> = Maybe.Nothing
      just.pairRight("right") shouldBe Just(2 to "right")
      nothing.pairRight("right") shouldBe Maybe.Nothing
    }

    "Maybe<Pair<L, R>>.toMap()" {
      val just: Maybe<Pair<String, String>> = Just("key" to "value")
      val nothing: Maybe<Pair<String, String>> = Maybe.Nothing
      just.toMap() shouldBe mapOf("key" to "value")
      nothing.toMap() shouldBe emptyMap()
    }

    "traverse should yield list of maybe" {
      val just: Maybe<String> = Just("value")
      val nothing: Maybe<String> = Maybe.Nothing
      just.traverse { listOf(it) } shouldBe listOf(Just("value"))
      nothing.traverse { listOf(it) } shouldBe emptyList()
    }

    "sequence should be consistent with traverse" {
      checkAll(Arb.int().orNull()) { int ->
        val maybe = int.toMaybe()
        maybe.map { listOf(it) }.sequence() shouldBe maybe.traverse { listOf(it) }
      }
    }

    "traverseEither should yield either of maybe" {
      val just: Maybe<String> = Just("value")
      val nothing: Maybe<String> = Maybe.Nothing
      just.traverse { it.right() } shouldBe just.right()
      nothing.traverse { it.right() } shouldBe nothing.right()
    }

    "sequenceEither should be consistent with traverseEither" {
      checkAll(Arb.int().orNull()) { int ->
        val maybe = int.toMaybe()
        maybe.map { it.right() }.sequence() shouldBe maybe.traverse { it.right() }
      }
    }

    "traverseValidated should yield validated of maybe" {
      val just: Maybe<String> = Just("value")
      val nothing: Maybe<String> = Maybe.Nothing
      just.traverse { it.valid() } shouldBe just.valid()
      nothing.traverse { it.valid() } shouldBe nothing.valid()
    }

    "sequenceValidated should be consistent with traverseValidated" {
      checkAll(Arb.int().orNull()) { int ->
        val maybe = int.toMaybe()
        maybe.map { it.valid() }.sequence() shouldBe maybe.traverse { it.valid() }
      }
    }

    "catch should return Just(result) when f does not throw" {
      val recover: (Throwable) -> Maybe<Int> = { _ -> Maybe.Nothing }
      Maybe.catch(recover) { 1 } shouldBe Just(1)
    }

    "catch with default recover should return Just(result) when f does not throw" {
      Maybe.catch { 1 } shouldBe Just(1)
    }

    "catch should return Just(recoverValue) when f throws" {
      val exception = Exception("Boom!")
      val recoverValue = 10
      val recover: (Throwable) -> Maybe<Int> = { _ -> Just(recoverValue) }
      Maybe.catch(recover) { throw exception } shouldBe Just(recoverValue)
    }

    "catch should return Maybe.Nothing when f throws" {
      val exception = Exception("Boom!")
      Maybe.catch { throw exception } shouldBe Maybe.Nothing
    }
  }
}
// Utils

private fun <T> iterableOf(vararg elements: T): Iterable<T> = Iterable {
  iterator { yieldAll(elements.toList()) }
}
