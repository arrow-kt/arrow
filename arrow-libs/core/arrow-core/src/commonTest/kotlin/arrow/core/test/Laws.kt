package arrow.core.test

import arrow.core.test.laws.Law
import io.kotest.core.names.TestName
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.scopes.StringSpecScope
import io.kotest.core.spec.style.scopes.addTest

public fun StringSpec.testLaws(vararg laws: List<Law>): Unit = laws
  .flatMap { list: List<Law> -> list.asIterable() }
  .distinctBy { law: Law -> law.name }
  .forEach { law: Law ->
    addTest(TestName(null, law.name, false), false, null) {
      law.test(StringSpecScope(this.coroutineContext, testCase))
    }
  }

public fun StringSpec.testLaws(prefix: String, vararg laws: List<Law>): Unit = laws
  .flatMap { list: List<Law> -> list.asIterable() }
  .distinctBy { law: Law -> law.name }
  .forEach { law: Law ->
    addTest(TestName(prefix, law.name, false), false, null) {
      law.test(StringSpecScope(this.coroutineContext, testCase))
    }
  }
