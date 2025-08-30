package arrow.core.raise

import arrow.core.test.result
import arrow.core.toNonEmptyListOrThrow
import arrow.core.toNonEmptySetOrThrow
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.set
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ResultSpec {

  val boom = RuntimeException("Boom!")
  val range = 0..3
  val nelRange = 1..4
  val iterationsSmall = 10
  val iterations = 20

  @Test fun resultException() = runTest {
    result {
      throw boom
    } shouldBe Result.failure(boom)
  }

  @Test fun resultSuccess() = runTest {
    result { 1 } shouldBe Result.success(1)
  }

  @Test fun resultRaise() = runTest {
    result { raise(boom) } shouldBe Result.failure(boom)
  }

  @Test fun recoverWorksAsExpected() = runTest {
    result {
      val one: Int = recover({ Result.failure<Int>(boom).bind() }) { 1 }
      val two = Result.success(2).bind()
      one + two
    } shouldBe Result.success(3)
  }

  @Test
  fun bind() = runTest {
    checkAll(iterationsSmall, Arb.result(Arb.int())) { a ->
      result {
        a.bind()
      } shouldBe a
    }
  }

  @Test
  fun bindAllMap() = runTest {
    checkAll(iterations, Arb.map(Arb.int(), Arb.result(Arb.int()), range.first, range.last)) { a ->
      val expected = a.values.firstOrNull { it.isFailure }
        ?: Result.success(
          a.mapValues {
            it.value.getOrThrow()
          },
        )

      result {
        a.bindAll()
      } shouldBe expected
    }
  }

  @Test
  fun bindAllIterable() = runTest {
    checkAll(iterations, Arb.list(Arb.result(Arb.int()), range)) { a ->
      val expected = a.firstOrNull { it.isFailure }
        ?: Result.success(
          a.map {
            it.getOrThrow()
          },
        )

      result {
        a.bindAll()
      } shouldBe expected
    }
  }

  @Test
  fun bindAllNel() = runTest {
    checkAll(iterations, Arb.list(Arb.result(Arb.int()), nelRange)) { generated ->
      val a = generated.toNonEmptyListOrThrow()
      val expected = a.firstOrNull { it.isFailure }
        ?: Result.success(
          a.map {
            it.getOrThrow()
          },
        )

      result {
        a.bindAll()
      } shouldBe expected
    }
  }

  @Test
  fun bindAllNes() = runTest {
    checkAll(iterations, Arb.set(Arb.result(Arb.int()), nelRange)) { generated ->
      val a = generated.toNonEmptySetOrThrow()
      val expected = a.firstOrNull { it.isFailure }
        ?: Result.success(
          a.map {
            it.getOrThrow()
          }.toNonEmptySetOrThrow(),
        )

      result {
        a.bindAll()
      } shouldBe expected
    }
  }

  @Test
  fun recover() = runTest {
    checkAll(iterationsSmall, Arb.result(Arb.int())) { a ->
      val expected = a.fold(
        onSuccess = { Result.success(it.toString()) },
        onFailure = { Result.success("recovered") },
      )

      result {
        recover(
          {
            a.bind().toString()
          },
          { "recovered" },
        )
      } shouldBe expected
    }
  }
}
