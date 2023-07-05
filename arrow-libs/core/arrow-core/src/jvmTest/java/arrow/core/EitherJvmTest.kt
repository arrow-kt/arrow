package arrow.core

import arrow.core.test.any
import arrow.core.test.suspendFunThatThrowsFatalThrowable
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.checkAll

class EitherJvmTest : StringSpec({
  "resolve should throw a Throwable when a fatal Throwable is thrown" {
    checkAll(
      Arb.suspendFunThatThrowsFatalThrowable(),
      Arb.any(),
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
          unrecoverableState = { handleWithPureFunction(it) },
        )
      }
    }
  }
})
