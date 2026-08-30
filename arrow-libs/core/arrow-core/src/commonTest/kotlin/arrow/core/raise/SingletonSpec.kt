package arrow.core.raise

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import arrow.core.shouldBeTypeOf
import arrow.core.test.nonEmptyList
import arrow.core.test.nonEmptySet
import arrow.core.test.option
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.collections.component1
import kotlin.collections.component2
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
      val recovered = emptyMap<Int, Int>()
      singleton({ recovered }) { a.bindAll() } shouldBe if (a.containsValue(null)) recovered else a
    }
  }

  @Test
  fun mapOptionBindAll() = runTest {
    checkAll(iterations, Arb.map(Arb.int(), Arb.option(Arb.int().orNull()), range.first, range.last)) { a ->
      val recovered = emptyMap<Int, Int?>()
      singleton({ recovered }) { a.bindAll() } shouldBe if (a.containsValue(None)) recovered else a.mapValues { (_, v) -> v.shouldBeSome() }
    }
  }

  @Test
  fun iterableNullableBindAll() = runTest {
    checkAll(iterations, Arb.list(Arb.int().orNull(), range)) { a ->
      val recovered = emptyList<Int>()
      singleton({ recovered }) { a.bindAll() } shouldBe if (a.contains(null)) recovered else a
    }
  }

  @Test
  fun iterableOptionBindAll() = runTest {
    checkAll(iterations, Arb.list(Arb.option(Arb.int().orNull()), range)) { a ->
      val recovered = emptyList<Int?>()
      singleton({ recovered }) { a.bindAll() } shouldBe if (a.contains(None)) recovered else a.map { it.shouldBeSome() }
    }
  }

  @Test
  fun nelNullableBindAll() = runTest {
    checkAll(iterations, Arb.nonEmptyList(Arb.int().orNull(), nelRange)) { a ->
      nullable { a.bindAll() } shouldBe if (a.contains(null)) null else a
    }
  }

  @Test
  fun nelOptionBindAll() = runTest {
    checkAll(iterations, Arb.nonEmptyList(Arb.option(Arb.int().orNull()), nelRange)) { a ->
      nullable { a.bindAll() } shouldBe if (a.contains(None)) null else a.map { it.shouldBeSome() }
    }
  }

  @Test
  fun nesNullableBindAll() = runTest {
    checkAll(iterations, Arb.nonEmptySet(Arb.int().orNull(), nelRange)) { a ->
      nullable { a.bindAll() } shouldBe if (a.contains(null)) null else a
    }
  }

  @Test
  fun nesOptionBindAll() = runTest {
    checkAll(iterations, Arb.nonEmptySet(Arb.option(Arb.int().orNull()), nelRange)) { a ->
      nullable { a.bindAll() } shouldBe if (a.contains(None)) null else a.map { it.shouldBeSome() }.toNonEmptySet()
    }
  }

  @Test
  fun recover() = runTest {
    checkAll(iterations, Arb.int().orNull()) { a ->
      singleton({ "" }) { recover({ a.bind().toString() }, { "recovered" }) } shouldBe (a?.toString() ?: "recovered")
    }
  }

  @Test
  fun ignoreErrors() = runTest {
    checkAll(iterations, Arb.int().orNull()) { a ->
      singleton({ 0 }) { ignoreErrors { a.bind() } } shouldBe (a ?: 0)
    }
  }
}

private fun <A> Option<A>.shouldBeSome(): A = shouldBeTypeOf<Some<A>>().value
