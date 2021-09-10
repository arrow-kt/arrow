package arrow.core

import arrow.core.test.UnitSpec
import arrow.core.test.generators.any
import arrow.core.test.generators.suspendFunThatThrowsFatalThrowable
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.property.Arb

class EitherJvmTest : UnitSpec({
  "resolve should throw a Throwable when a fatal Throwable is thrown" {
    checkAll(
      Arb.suspendFunThatThrowsFatalThrowable(),
      Arb.any()
    ) { f: suspend () -> Either<Any, Any>, returnObject: Any ->

      val comparator: Comparator<Person> =
        Comparator.comparingInt(Person::age)
          .thenComparing(Person::name)

      shouldThrow<Throwable> {
        Either.resolve(
          f = { f() },
          success = { a -> handleWithPureFunction(a, returnObject) },
          error = { e -> handleWithPureFunction(e, returnObject) },
          throwable = { t -> handleWithPureFunction(t, returnObject) },
          unrecoverableState = { handleWithPureFunction(it) }
        )
      }
    }
  }
})
