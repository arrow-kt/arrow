package arrow.core

import arrow.core.continuations.ensureNotNull
import arrow.core.continuations.maybe
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
        } shouldBeMaybe if (predicate) Just(i) else Maybe.Nothing
      }
    }

    "ensureNotNull in maybe computation" {
      fun square(i: Int): Int = i * i
      checkAll(Arb.int().orNull()) { i: Int? ->
        maybe {
          val ii = i
          ensureNotNull(ii)
          square(ii) // Smart-cast by contract
        } shouldBeMaybe i.toMaybe().map(::square)
      }
    }

    "short circuit null" {
      maybe {
        val number: Int = "s".length
        ensureNotNull(number.takeIf { it > 1 })
        throw IllegalStateException("This should not be executed")
      } shouldBeMaybe Maybe.Nothing
    }

    "tap applies effects returning the original value" {
      checkAll(Arb.long().orNull()) { long ->
        val maybe = long.toMaybe()
        var effect = 0
        val res = maybe.tap { effect += 1 }
        val expected = maybe.fold({ 0 }, { 1 })
        effect shouldBe expected
        res shouldBeMaybe maybe
      }
    }

    "tapNothing applies effects returning the original value" {
      checkAll(Arb.long().orNull()) { long ->
        val maybe = long.toMaybe()
        var effect = 0
        val res = maybe.tapNothing { effect += 1 }
        val expected = maybe.fold({ 1 }, { 0 })
        effect shouldBe expected
        res shouldBeMaybe maybe
      }
    }

    "fromNullable should work for both null and non-null values of nullable types" {
      checkAll(Arb.int().orNull()) { a: Int? ->
        // This seems to be generating only non-null values, so it is complemented by the next test
        val o: Maybe<Int> = Maybe.fromNullable(a)
        if (a == null) o shouldBeMaybe Maybe.Nothing else o shouldBeMaybe Just(a)
      }
    }

    "fromNullable should return maybe.Nothing for null values of nullable types" {
      val a: Int? = null
      Maybe.fromNullable(a) shouldBeMaybe Maybe.Nothing
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
      just.map(String::uppercase) shouldBeMaybe Just("KOTLIN")
      nothing.map(String::uppercase) shouldBeMaybe Maybe.Nothing
    }

    "zip" {
      checkAll(Arb.int()) { a: Int ->
        val op: Maybe<Int> = a.just()
        just.zip(op) { a, b -> a + b } shouldBeMaybe Just("kotlin$a")
        nothing.zip(op) { a, b -> a + b } shouldBeMaybe Maybe.Nothing
        just.zip(op) shouldBeMaybe Just(Pair("kotlin", a))
      }
    }

    "mapNotNull" {
      just.mapNotNull { it.toIntOrNull() } shouldBeMaybe Maybe.Nothing
      just.mapNotNull { it.uppercase() } shouldBeMaybe Just("KOTLIN")
    }

    "fold" {
      just.fold({ 0 }) { it.length } shouldBe 6
      nothing.fold({ 0 }) { it.length } shouldBe 0
    }

    "flatMap" {
      just.flatMap { Just(it.uppercase()) } shouldBeMaybe Just("KOTLIN")
      nothing.flatMap { Just(it.uppercase()) } shouldBeMaybe Maybe.Nothing
    }

    "align" {
      just align just shouldBeMaybe Just(Ior.Both("kotlin", "kotlin"))
      just align nothing shouldBeMaybe Just(Ior.Left("kotlin"))
      nothing align just shouldBeMaybe Just(Ior.Right("kotlin"))
      nothing align nothing shouldBeMaybe Maybe.Nothing

      just.align(just) { "$it" } shouldBeMaybe Just("Ior.Both(kotlin, kotlin)")
      just.align(nothing) { "$it" } shouldBeMaybe Just("Ior.Left(kotlin)")
      nothing.align(just) { "$it" } shouldBeMaybe Just("Ior.Right(kotlin)")
      nothing.align(nothing) { "$it" } shouldBeMaybe Maybe.Nothing

      val nullable = null.just<Any?>()
      just align nullable shouldBeMaybe Just(Ior.Both("kotlin", null))
      nullable align just shouldBeMaybe Just(Ior.Both(null, "kotlin"))
      nullable align nullable shouldBeMaybe Just(Ior.Both(null, null))

      just.align(nullable) { "$it" } shouldBeMaybe Just("Ior.Both(kotlin, null)")
      nullable.align(just) { "$it" } shouldBeMaybe Just("Ior.Both(null, kotlin)")
      nullable.align(nullable) { "$it" } shouldBeMaybe Just("Ior.Both(null, null)")
    }

    "filter" {
      just.filter { it == "java" } shouldBeMaybe Maybe.Nothing
      nothing.filter { it == "java" } shouldBeMaybe Maybe.Nothing
      just.filter { it.startsWith('k') } shouldBeMaybe Just("kotlin")
    }

    "filterNot" {
      just.filterNot { it == "java" } shouldBeMaybe Just("kotlin")
      nothing.filterNot { it == "java" } shouldBeMaybe Maybe.Nothing
      just.filterNot { it.startsWith('k') } shouldBeMaybe Maybe.Nothing
    }

    "filterIsInstance" {
      val justAny: Maybe<Any> = just
      justAny.filterIsInstance<String>() shouldBeMaybe Just("kotlin")
      justAny.filterIsInstance<Int>() shouldBeMaybe Maybe.Nothing

      val someNullableAny: Maybe<Any?> = null.just()
      someNullableAny.filterIsInstance<String?>() shouldBeMaybe Just(null)
      someNullableAny.filterIsInstance<String>() shouldBeMaybe Maybe.Nothing

      val nothingAny: Maybe<Any> = nothing
      nothingAny.filterIsInstance<String>() shouldBeMaybe Maybe.Nothing
      nothingAny.filterIsInstance<Int>() shouldBeMaybe Maybe.Nothing
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
      just.orElse { Just("java") } shouldBeMaybe Just("kotlin")
      nothing.orElse { Just("java") } shouldBeMaybe Just("java")
    }

    "toList" {
      just.toList() shouldBe listOf("kotlin")
      nothing.toList() shouldBe listOf()
    }

    "Iterable.firstOrNothing" {
      val iterable = iterableOf(1, 2, 3, 4, 5, 6)
      iterable.firstOrNothing() shouldBeMaybe Just(1)
      iterable.firstOrNothing { it > 2 } shouldBeMaybe Just(3)
      iterable.firstOrNothing { it > 7 } shouldBeMaybe Maybe.Nothing

      val emptyIterable = iterableOf<Int>()
      emptyIterable.firstOrNothing() shouldBeMaybe Maybe.Nothing

      val nullableIterable1 = iterableOf(null, 2, 3, 4, 5, 6)
      nullableIterable1.firstOrNothing() shouldBeMaybe Just(null)

      val nullableIterable2 = iterableOf(1, 2, 3, null, 5, null)
      nullableIterable2.firstOrNothing { it == null } shouldBeMaybe Just(null)
    }

    "Collection.firstOrNothing" {
      val list = listOf(1, 2, 3, 4, 5, 6)
      list.firstOrNothing() shouldBeMaybe Just(1)

      val emptyList = emptyList<Int>()
      emptyList.firstOrNothing() shouldBeMaybe Maybe.Nothing

      val nullableList = listOf(null, 2, 3, 4, 5, 6)
      nullableList.firstOrNothing() shouldBeMaybe Just(null)
    }

    "Iterable.singleOrNothing" {
      val iterable = iterableOf(1, 2, 3, 4, 5, 6)
      iterable.singleOrNothing() shouldBeMaybe Maybe.Nothing
      iterable.singleOrNothing { it > 2 } shouldBeMaybe Maybe.Nothing

      val singleIterable = iterableOf(3)
      singleIterable.singleOrNothing() shouldBeMaybe Just(3)
      singleIterable.singleOrNothing { it == 3 } shouldBeMaybe Just(3)

      val nullableSingleIterable1 = iterableOf<Int?>(null)
      nullableSingleIterable1.singleOrNothing() shouldBeMaybe Just(null)

      val nullableSingleIterable2 = iterableOf(1, 2, 3, null, 5, 6)
      nullableSingleIterable2.singleOrNothing { it == null } shouldBeMaybe Just(null)

      val nullableSingleIterable3 = iterableOf(1, 2, 3, null, 5, null)
      nullableSingleIterable3.singleOrNothing { it == null } shouldBeMaybe Maybe.Nothing
    }

    "Collection.singleOrNothing" {
      val list = listOf(1, 2, 3, 4, 5, 6)
      list.singleOrNothing() shouldBeMaybe Maybe.Nothing

      val singleList = listOf(3)
      singleList.singleOrNothing() shouldBeMaybe Just(3)

      val nullableSingleList = listOf(null)
      nullableSingleList.singleOrNothing() shouldBeMaybe Just(null)
    }

    "Iterable.lastOrNothing" {
      val iterable = iterableOf(1, 2, 3, 4, 5, 6)
      iterable.lastOrNothing() shouldBeMaybe Just(6)
      iterable.lastOrNothing { it < 4 } shouldBeMaybe Just(3)
      iterable.lastOrNothing { it > 7 } shouldBeMaybe Maybe.Nothing

      val emptyIterable = iterableOf<Int>()
      emptyIterable.lastOrNothing() shouldBeMaybe Maybe.Nothing

      val nullableIterable1 = iterableOf(1, 2, 3, 4, 5, null)
      nullableIterable1.lastOrNothing() shouldBeMaybe Just(null)

      val nullableIterable2 = iterableOf(null, 2, 3, null, 5, 6)
      nullableIterable2.lastOrNothing { it == null } shouldBeMaybe Just(null)
    }

    "Collection.lastOrNothing" {
      val list = listOf(1, 2, 3, 4, 5, 6)
      list.lastOrNothing() shouldBeMaybe Just(6)

      val emptyList = emptyList<Int>()
      emptyList.lastOrNothing() shouldBeMaybe Maybe.Nothing

      val nullableList = listOf(1, 2, 3, 4, 5, null)
      nullableList.lastOrNothing() shouldBeMaybe Just(null)
    }

    "Iterable.elementAtOrNothing" {
      val iterable = iterableOf(1, 2, 3, 4, 5, 6)
      iterable.elementAtOrNothing(index = 3 - 1) shouldBeMaybe Just(3)
      iterable.elementAtOrNothing(index = -1) shouldBeMaybe Maybe.Nothing
      iterable.elementAtOrNothing(index = 100) shouldBeMaybe Maybe.Nothing

      val nullableIterable = iterableOf(1, 2, null, 4, 5, 6)
      nullableIterable.elementAtOrNothing(index = 3 - 1) shouldBeMaybe Just(null)
    }

    "Collection.elementAtOrNothing" {
      val list = listOf(1, 2, 3, 4, 5, 6)
      list.elementAtOrNothing(index = 3 - 1) shouldBeMaybe Just(3)
      list.elementAtOrNothing(index = -1) shouldBeMaybe Maybe.Nothing
      list.elementAtOrNothing(index = 100) shouldBeMaybe Maybe.Nothing

      val nullableList = listOf(1, 2, null, 4, 5, 6)
      nullableList.elementAtOrNothing(index = 3 - 1) shouldBeMaybe Just(null)
    }

    "and" {
      val x = Just(2)
      val y = Just("Foo")
      x and y shouldBeMaybe Just("Foo")
      x and Maybe.Nothing shouldBeMaybe Maybe.Nothing
      Maybe.Nothing and x shouldBeMaybe Maybe.Nothing
      Maybe.Nothing and Maybe.Nothing shouldBeMaybe Maybe.Nothing
    }

    "or" {
      val x = Just(2)
      val y = Just(100)
      x or y shouldBeMaybe Just(2)
      x or Maybe.Nothing shouldBeMaybe Just(2)
      Maybe.Nothing or x shouldBeMaybe Just(2)
      Maybe.Nothing or Maybe.Nothing shouldBeMaybe Maybe.Nothing
    }

    "toLeftMaybe" {
      1.leftIor().leftOrNull() shouldBe 1
      2.rightIor().leftOrNull() shouldBe null
      (1 to 2).bothIor().leftOrNull() shouldBe 1
    }

    "pairLeft" {
      val just: Maybe<Int> = Just(2)
      val nothing: Maybe<Int> = Maybe.Nothing
      just.pairLeft("key") shouldBeMaybe Just("key" to 2)
      nothing.pairLeft("key") shouldBeMaybe Maybe.Nothing
    }

    "pairRight" {
      val just: Maybe<Int> = Just(2)
      val nothing: Maybe<Int> = Maybe.Nothing
      just.pairRight("right") shouldBeMaybe Just(2 to "right")
      nothing.pairRight("right") shouldBeMaybe Maybe.Nothing
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
      Maybe.catch(recover) { 1 } shouldBeMaybe Just(1)
    }

    "catch with default recover should return Just(result) when f does not throw" {
      Maybe.catch { 1 } shouldBeMaybe Just(1)
    }

    "catch should return Just(recoverValue) when f throws" {
      val exception = Exception("Boom!")
      val recoverValue = 10
      val recover: (Throwable) -> Maybe<Int> = { _ -> Just(recoverValue) }
      Maybe.catch(recover) { throw exception } shouldBeMaybe Just(recoverValue)
    }

    "catch should return Maybe.Nothing when f throws" {
      val exception = Exception("Boom!")
      Maybe.catch { throw exception } shouldBeMaybe Maybe.Nothing
    }
  }
}
// Utils

private fun <T> iterableOf(vararg elements: T): Iterable<T> = Iterable {
  iterator { yieldAll(elements.toList()) }
}

// Doesn't box, which makes it easier to inspect decompiled bytecode
@OptIn(MaybeInternals::class)
private infix fun <T, U : T> Maybe<T>?.shouldBeMaybe(expected: Maybe<U>?): Unit =
  this?.underlying shouldBe expected?.underlying
