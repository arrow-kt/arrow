package arrow.core.raise

import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import arrow.core.None
import arrow.core.getOrElse
import arrow.core.mapValuesNotNull
import arrow.core.shouldBeTypeOf
import arrow.core.test.option
import arrow.core.toNonEmptyListOrNull
import arrow.core.toNonEmptyListOrThrow
import arrow.core.toNonEmptySetOrNull
import arrow.core.toNonEmptySetOrThrow
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.set
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SingletonSpec {

  val range = 0..3
  val nelRange = 1..4
  val iterationsSmall = 10
  val iterations = 20

  @Suppress("UnusedExpression")
  @Test
  fun exceptionProperlyEscapesSingleton() = runTest {
    catch(
      {
        singleton(
          { 0 },
          { throw ArithmeticException() },
        )
      },
      {
        it.shouldBeTypeOf<ArithmeticException>()
      },
    )
  }

  @Test
  fun successfulPath() = runTest {
    checkAll(iterationsSmall, Arb.int()) { a ->
      singleton(
        { 0 },
        { a },
      ) shouldBe a
    }
  }

  @Test
  fun anyRaiseInfoIsProperlySwallowed() = runTest {
    singleton(
      { "recovered" },
      { raise() },
    ) shouldBe "recovered"

    singleton(
      { "recovered" },
      { raise("raised") },
    ) shouldBe "recovered"

    singleton(
      { 1 },
      { raise(0) },
    ) shouldBe 1
  }

  @Test
  fun ensure() = runTest {
    checkAll(iterationsSmall, Arb.boolean(), Arb.string(range)) { predicate, a ->
      val expected = if (predicate) a else "recovered"

      singleton(
        { "recovered" },
        {
          ensure(predicate)
          a
        },
      ) shouldBe expected
    }
  }

  @Test
  fun optionBind() = runTest {
    checkAll(iterationsSmall, Arb.option(Arb.int())) { a ->
      val expected = a.getOrElse { 1 }

      singleton(
        { 1 },
        { a.bind() },
      ) shouldBe expected
    }
  }

  @Test
  fun bindNullable() = runTest {
    checkAll(iterationsSmall, Arb.int().orNull()) { a ->
      val expected = a ?: 1

      singleton(
        { 1 },
        { a.bind() },
      ) shouldBe expected
    }
  }

  @Test
  fun ensureNotNull() = runTest {
    checkAll(iterationsSmall, Arb.int().orNull()) { a ->
      val expected = a ?: 1

      singleton(
        { 1 },
        { ensureNotNull(a) },
      ) shouldBe expected
    }
  }

  @Test
  fun mapNullableBindAll() = runTest {
    checkAll(iterations, Arb.map(Arb.int(), Arb.int().orNull(), range.first, range.last)) { a ->
      val recovered: Map<Int, Int> = emptyMap()
      val expected = if (a.containsValue(null)) {
        recovered
      } else {
        a.mapValuesNotNull { it.value }
      }

      singleton(
        { recovered },
        { a.bindAll() },
      ) shouldBe expected
    }
  }

  @Test
  fun mapOptionBindAll() = runTest {
    checkAll(iterations, Arb.map(Arb.int(), Arb.option(Arb.int()), range.first, range.last)) { a ->
      val recovered: Map<Int, Int> = emptyMap()
      val expected = if (a.containsValue(None)) {
        recovered
      } else {
        a.mapValuesNotNull { it.value.getOrNull() }
      }

      singleton(
        { recovered },
        { a.bindAll() },
      ) shouldBe expected
    }
  }

  @Test
  fun iterableNullableBindAll() = runTest {
    checkAll(iterations, Arb.list(Arb.int().orNull(), range)) { a ->
      val recovered: List<Int> = emptyList()
      val expected = if (a.contains(null)) {
        recovered
      } else {
        a.mapNotNull { it }
      }

      singleton(
        { recovered },
        { a.bindAll() },
      ) shouldBe expected
    }
  }

  @Test
  fun iterableOptionBindAll() = runTest {
    checkAll(iterations, Arb.list(Arb.option(Arb.int()), range)) { a ->
      val recovered: List<Int> = emptyList()
      val expected = if (a.contains(None)) {
        recovered
      } else {
        a.mapNotNull { it.getOrNull() }
      }

      singleton(
        { recovered },
        { a.bindAll() },
      ) shouldBe expected
    }
  }

  @Test
  fun nelNullableBindAll() = runTest {
    checkAll(iterations, Arb.list(Arb.int().orNull(), nelRange)) { generated ->
      val a = generated.toNonEmptyListOrThrow()
      val recovered: NonEmptyList<Int>? = null

      val expected = if (a.contains(null)) {
        recovered
      } else {
        a.mapNotNull { it }.toNonEmptyListOrNull()
      }

      singleton(
        { recovered },
        { a.bindAll() },
      ) shouldBe expected
    }
  }

  @Test
  fun nelOptionBindAll() = runTest {
    checkAll(iterations, Arb.list(Arb.option(Arb.int()), nelRange)) { generated ->
      val a = generated.toNonEmptyListOrThrow()
      val recovered: NonEmptyList<Int>? = null

      val expected = if (a.contains(None)) {
        recovered
      } else {
        a.mapNotNull { it.getOrNull() }.toNonEmptyListOrNull()
      }

      singleton(
        { recovered },
        { a.bindAll() },
      ) shouldBe expected
    }
  }

  @Test
  fun nesNullableBindAll() = runTest {
    checkAll(iterations, Arb.set(Arb.int().orNull(), nelRange)) { generated ->
      val a = generated.toNonEmptySetOrThrow()
      val recovered: NonEmptySet<Int>? = null

      val expected = if (a.contains(null)) {
        recovered
      } else {
        a.mapNotNull { it }.toNonEmptySetOrNull()
      }

      singleton(
        { recovered },
        { a.bindAll() },
      ) shouldBe expected
    }
  }

  @Test
  fun nesOptionBindAll() = runTest {
    checkAll(iterations, Arb.set(Arb.option(Arb.int()), nelRange)) { generated ->
      val a = generated.toNonEmptySetOrThrow()
      val recovered: NonEmptySet<Int>? = null

      val expected = if (a.contains(None)) {
        recovered
      } else {
        a.mapNotNull { it.getOrNull() }.toNonEmptySetOrNull()
      }

      singleton(
        { recovered },
        { a.bindAll() },
      ) shouldBe expected
    }
  }

  @Test
  fun recover() = runTest {
    checkAll(iterations, Arb.int().orNull()) { a ->
      val expected = a?.toString() ?: "recovered"

      singleton(
        { "" },
        {
          recover(
            {
              a.bind().toString()
            },
            { "recovered" },
          )
        },
      ) shouldBe expected
    }
  }

  @Test
  fun ignoreErrors() = runTest {
    checkAll(iterations, Arb.int().orNull()) { a ->
      val expected = a ?: 0

      singleton(
        { 0 },
        {
          ignoreErrors {
            a.bind()
          }
        },
      ) shouldBe expected
    }
  }
}
