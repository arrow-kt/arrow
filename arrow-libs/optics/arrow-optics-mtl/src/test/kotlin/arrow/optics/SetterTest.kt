package arrow.optics

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.toT
import arrow.mtl.State
import arrow.mtl.run
import arrow.optics.mtl.assign_
import arrow.optics.mtl.update_
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class SetterTest : UnitSpec() {

  init {
    "update_ f should be as modify f within State and returning Unit" {
      forAll(genToken, Gen.functionAToB<String, String>(Gen.string())) { generatedToken, f ->
        tokenSetter.update_(f).run(generatedToken) ==
          State { token: Token ->
            tokenSetter.modify(token, f) toT Unit
          }.run(generatedToken)
      }
    }

    "assign_ f should be as modify f within State and returning Unit" {
      forAll(genToken, Gen.string()) { generatedToken, string ->
        tokenSetter.assign_(string).run(generatedToken) ==
          State { token: Token ->
            tokenSetter.set(token, string) toT Unit
          }.run(generatedToken)
      }
    }
  }
}
