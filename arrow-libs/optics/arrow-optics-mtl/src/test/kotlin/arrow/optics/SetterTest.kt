package arrow.optics

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.toT
import arrow.mtl.State
import arrow.mtl.run
import arrow.optics.mtl.assign_
import arrow.optics.mtl.update_
import io.kotest.property.Arb
import io.kotest.property.checkAll

class SetterTest : UnitSpec() {

  init {
    "update_ f should be as modify f within State and returning Unit" {
      checkAll(genToken, Arb.functionAToB<String, String>(Arb.string())) { generatedToken, f ->
        tokenSetter.update_(f).run(generatedToken) ==
          State { token: Token ->
            tokenSetter.modify(token, f) toT Unit
          }.run(generatedToken)
      }
    }

    "assign_ f should be as modify f within State and returning Unit" {
      checkAll(genToken, Arb.string()) { generatedToken, string ->
        tokenSetter.assign_(string).run(generatedToken) ==
          State { token: Token ->
            tokenSetter.set(token, string) toT Unit
          }.run(generatedToken)
      }
    }
  }
}
