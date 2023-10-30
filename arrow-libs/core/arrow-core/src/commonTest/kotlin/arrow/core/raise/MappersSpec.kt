package arrow.core.raise

import arrow.core.Ior
import arrow.core.None
import arrow.core.merge
import arrow.core.none
import arrow.core.test.either
import arrow.core.toOption
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

class MappersSpec {
  @Test fun effectToEither() = runTest {
    checkAll(Arb.either(Arb.int(), Arb.string())) { a ->
      effect { a.bind() }.toEither() shouldBe a
    }
  }

  @Test fun eagerEffectToEither() = runTest {
    checkAll(Arb.either(Arb.int(), Arb.string())) { a ->
      eagerEffect { a.bind() }.toEither() shouldBe a
    }
  }

  @Test fun effectToIor() = runTest {
    checkAll(Arb.either(Arb.int(), Arb.string())) { a ->
      effect { a.bind() }.toIor() shouldBe a.fold({ Ior.Left(it) }, { Ior.Right(it) })
    }
  }

  @Test fun eagerEffectToIor() = runTest {
    checkAll(Arb.either(Arb.int(), Arb.string())) { a ->
      eagerEffect { a.bind() }.toIor() shouldBe a.fold({ Ior.Left(it) }, { Ior.Right(it) })
    }
  }

  @Test fun effectGetOrNull() = runTest {
    checkAll(Arb.either(Arb.int(), Arb.string())) { a ->
      effect { a.bind() }.getOrNull() shouldBe a.getOrNull()
    }
  }

  @Test fun eagerEffectGetOrNull() = runTest {
    checkAll(Arb.either(Arb.int(), Arb.string())) { a ->
      eagerEffect { a.bind() }.getOrNull() shouldBe a.getOrNull()
    }
  }

  @Test fun effectToOptionNone() = runTest {
    checkAll(Arb.either(Arb.int(), Arb.string())) { a ->
      effect { a.bind() }.toOption { none() } shouldBe a.getOrNull().toOption()
    }
  }

  @Test fun eagerEffectToOptionNone() = runTest {
    checkAll(Arb.either(Arb.int(), Arb.string())) { a ->
      eagerEffect { a.bind() }.toOption { none() } shouldBe a.getOrNull().toOption()
    }
  }

  @Test fun effectToOption() = runTest {
    checkAll(Arb.either(Arb.constant(None), Arb.string())) { a ->
      effect { a.bind() }.toOption() shouldBe a.getOrNull().toOption()
    }
  }

  @Test fun eagerEffectToOption() = runTest {
    checkAll(Arb.either(Arb.constant(None), Arb.string())) { a ->
      eagerEffect { a.bind() }.toOption() shouldBe a.getOrNull().toOption()
    }
  }

  @Test fun effectToResultWithBlock() = runTest {
    checkAll(Arb.either(Arb.int(), Arb.string())) { a ->
      effect { a.bind() }.toResult { success(it) } shouldBe a.fold({ success(it) }, { success(it) })
    }
  }

  @Test fun eagerEffectToResultWithBlock() = runTest {
    checkAll(Arb.either(Arb.int(), Arb.string())) { a ->
      eagerEffect { a.bind() }.toResult { success(it) } shouldBe a.fold({ success(it) }, { success(it) })
    }
  }

  val boom = RuntimeException("Boom!")

  @Test fun effectToResultWithBlockException() = runTest {
    effect<Int, String> { throw boom }.toResult { success(it) } shouldBe failure(boom)
  }

  @Test fun eagerEffectToResultWithBlockException() = runTest {
    checkAll(Arb.string()) { a ->
      eagerEffect<Int, String> { throw boom }.toResult { success(it) } shouldBe failure(boom)
    }
  }

  @Test fun effectToResult() = runTest {
    checkAll(Arb.either(Arb.string().map { RuntimeException(it) }, Arb.string())) { a ->
      effect { a.bind() }.toResult() shouldBe a.fold({ failure(it) }, { success(it) })
    }
  }

  @Test fun eagerEffectToResult() = runTest {
    checkAll(Arb.either(Arb.string().map { RuntimeException(it) }, Arb.string())) { a ->
      eagerEffect { a.bind() }.toResult() shouldBe a.fold({ failure(it) }, { success(it) })
    }
  }

  @Test fun effectToResultException() = runTest {
    effect<Throwable, String> { throw boom }.toResult() shouldBe failure(boom)
  }

  @Test fun eagerEffectToResultException() = runTest {
    checkAll(Arb.string()) { a ->
      eagerEffect<Throwable, String> { throw boom }.toResult() shouldBe failure(boom)
    }
  }

  @Test fun effectMerge() = runTest {
    checkAll(Arb.either(Arb.string(), Arb.string())) { a ->
      effect { a.bind() }.merge() shouldBe a.merge()
    }
  }

  @Test fun eagerEffectMerge() = runTest {
    checkAll(Arb.either(Arb.string(), Arb.string())) { a ->
      eagerEffect { a.bind() }.merge() shouldBe a.merge()
    }
  }
}
