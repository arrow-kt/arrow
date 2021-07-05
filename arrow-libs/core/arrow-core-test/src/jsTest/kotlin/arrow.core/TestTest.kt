package arrow.core

import io.kotest.core.spec.style.StringSpec

class TestTest : StringSpec({
  "JS fails on CI" {
    throw RuntimeException("Fails on CI!")
  }
})
