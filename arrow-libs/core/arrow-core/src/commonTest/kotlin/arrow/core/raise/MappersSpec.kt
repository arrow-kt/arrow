package arrow.core.raise

import arrow.core.Ior
import arrow.core.None
import arrow.core.merge
import arrow.core.none
import arrow.core.test.either
import arrow.core.toOption
import arrow.core.raise.toOption
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

class MappersSpec : StringSpec({
  "effect - toEither" {
    checkAll(Arb.either(Arb.int(), Arb.string())) { a ->
      effect { a.bind() }.toEither() shouldBe a
    }
  }

  "eagerEffect - toEither" {
    checkAll(Arb.either(Arb.int(), Arb.string())) { a ->
      eagerEffect { a.bind() }.toEither() shouldBe a
    }
  }

  "effect - toIor" {
    checkAll(Arb.either(Arb.int(), Arb.string())) { a ->
      effect { a.bind() }.toIor() shouldBe a.fold({ Ior.Left(it) }, { Ior.Right(it) })
    }
  }

  "eagerEffect - toIor" {
    checkAll(Arb.either(Arb.int(), Arb.string())) { a ->
      eagerEffect { a.bind() }.toIor() shouldBe a.fold({ Ior.Left(it) }, { Ior.Right(it) })
    }
  }

  "effect - orNull" {
    checkAll(Arb.either(Arb.int(), Arb.string())) { a ->
      effect { a.bind() }.orNull() shouldBe a.getOrNull()
    }
  }

  "eagerEffect - orNull" {
    checkAll(Arb.either(Arb.int(), Arb.string())) { a ->
      eagerEffect { a.bind() }.orNull() shouldBe a.getOrNull()
    }
  }

  "effect - toOption { none() }" {
    checkAll(Arb.either(Arb.int(), Arb.string())) { a ->
      effect { a.bind() }.toOption { none() } shouldBe a.getOrNull().toOption()
    }
  }

  "eagerEffect - toOption { none() }" {
    checkAll(Arb.either(Arb.int(), Arb.string())) { a ->
      eagerEffect { a.bind() }.toOption { none() } shouldBe a.getOrNull().toOption()
    }
  }

  "effect - toOption" {
    checkAll(Arb.either(Arb.constant(None), Arb.string())) { a ->
      effect { a.bind() }.toOption() shouldBe a.getOrNull().toOption()
    }
  }

  "eagerEffect - toOption" {
    checkAll(Arb.either(Arb.constant(None), Arb.string())) { a ->
      eagerEffect { a.bind() }.toOption() shouldBe a.getOrNull().toOption()
    }
  }

  "effect - toResult { }" {
    checkAll(Arb.either(Arb.int(), Arb.string())) { a ->
      effect { a.bind() }.toResult { success(it) } shouldBe a.fold({ success(it) }, { success(it) })
    }
  }

  "eagerEffect - toResult { }" {
    checkAll(Arb.either(Arb.int(), Arb.string())) { a ->
      eagerEffect { a.bind() }.toResult { success(it) } shouldBe a.fold({ success(it) }, { success(it) })
    }
  }

  val boom = RuntimeException("Boom!")

  "effect - toResult { } - exception" {
    effect<Int, String> { throw boom }.toResult { success(it) } shouldBe failure(boom)
  }

  "eagerEffect - toResult { } - exception" {
    checkAll(Arb.string()) { a ->
      eagerEffect<Int, String> { throw boom }.toResult { success(it) } shouldBe failure(boom)
    }
  }

  "effect - toResult()" {
    checkAll(Arb.either(Arb.string().map { RuntimeException(it) }, Arb.string())) { a ->
      effect { a.bind() }.toResult() shouldBe a.fold({ failure(it) }, { success(it) })
    }
  }

  "eagerEffect - toResult()" {
    checkAll(Arb.either(Arb.string().map { RuntimeException(it) }, Arb.string())) { a ->
      eagerEffect { a.bind() }.toResult() shouldBe a.fold({ failure(it) }, { success(it) })
    }
  }

  "effect - toResult() - exception" {
    effect<Throwable, String> { throw boom }.toResult() shouldBe failure(boom)
  }

  "eagerEffect - toResult() - exception" {
    checkAll(Arb.string()) { a ->
      eagerEffect<Throwable, String> { throw boom }.toResult() shouldBe failure(boom)
    }
  }

  "effect - merge" {
    checkAll(Arb.either(Arb.string(), Arb.string())) { a ->
      effect { a.bind() }.merge() shouldBe a.merge()
    }
  }

  "eagerEffect - merge" {
    checkAll(Arb.either(Arb.string(), Arb.string())) { a ->
      eagerEffect { a.bind() }.merge() shouldBe a.merge()
    }
  }
})
