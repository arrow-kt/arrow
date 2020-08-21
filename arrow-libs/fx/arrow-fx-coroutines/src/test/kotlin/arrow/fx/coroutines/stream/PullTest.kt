package arrow.fx.coroutines.stream

import arrow.fx.coroutines.ArrowFxSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

class PullTest : ArrowFxSpec(spec = {

  "pull can output chunks" {
    checkAll(Arb.chunk(Arb.int())) { ch ->
      Stream(Pull.output(ch))
        .toList() shouldBe ch.toList()
    }
  }
})
