package arrow.optics

import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.toT
import arrow.mtl.State
import arrow.mtl.map
import arrow.mtl.run
import arrow.mtl.runId
import arrow.optics.mtl.ask
import arrow.optics.mtl.asks
import arrow.optics.mtl.assign
import arrow.optics.mtl.assignOld
import arrow.optics.mtl.assign_
import arrow.optics.mtl.extract
import arrow.optics.mtl.extractMap
import arrow.optics.mtl.toReader
import arrow.optics.mtl.toState
import arrow.optics.mtl.update
import arrow.optics.mtl.updateOld
import arrow.optics.mtl.update_
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class LensTest : UnitSpec() {

  init {

    "Asking for the focus in a Reader" {
      forAll(genToken) { token: Token ->
        tokenLens.ask().runId(token) == token.value
      }
    }

    "toReader is an alias for ask" {
      forAll(genToken) { token: Token ->
        tokenLens.ask().runId(token) == tokenLens.toReader().runId(token)
      }
    }

    "Asks with f is the same as applying f to the focus of the lens" {
      forAll(genToken, Gen.functionAToB<String, String>(Gen.string())) { token, f ->
        tokenLens.asks(f).runId(token) == f(token.value)
      }
    }

    "Extract should extract the focus from the state" {
      forAll(genToken) { generatedToken ->
        tokenLens.extract().run(generatedToken) ==
          State { token: Token ->
            token toT tokenLens.get(token)
          }.run(generatedToken)
      }
    }

    "toState should be an alias to extract" {
      forAll(genToken) { token ->
        tokenLens.toState().run(token) == tokenLens.extract().run(token)
      }
    }

    "Extracts with f should be same as extract and map" {
      forAll(genToken, Gen.functionAToB<String, String>(Gen.string())) { generatedToken, f ->
        tokenLens.extractMap(f).run(generatedToken) == tokenLens.extract().map(f).run(generatedToken)
      }
    }

    "update f should be same modify f within State and returning new state" {
      forAll(genToken, Gen.functionAToB<String, String>(Gen.string())) { generatedToken, f ->
        tokenLens.update(f).run(generatedToken) ==
          State { token: Token ->
            tokenLens.modify(token, f)
              .let { it toT it.value }
          }.run(generatedToken)
      }
    }

    "updateOld f should be same as modify f within State and returning old state" {
      forAll(genToken, Gen.functionAToB<String, String>(Gen.string())) { generatedToken, f ->
        tokenLens.updateOld(f).run(generatedToken) ==
          State { token: Token ->
            tokenLens.modify(token, f) toT tokenLens.get(token)
          }.run(generatedToken)
      }
    }

    "update_ f should be as modify f within State and returning Unit" {
      forAll(genToken, Gen.functionAToB<String, String>(Gen.string())) { generatedToken, f ->
        tokenLens.update_(f).run(generatedToken) ==
          State { token: Token ->
            tokenLens.modify(token, f) toT Unit
          }.run(generatedToken)
      }
    }

    "assign a should be same set a within State and returning new value" {
      forAll(genToken, Gen.string()) { generatedToken, string ->
        tokenLens.assign(string).run(generatedToken) ==
          State { token: Token ->
            tokenLens.set(token, string)
              .let { it toT it.value }
          }.run(generatedToken)
      }
    }

    "assignOld f should be same as modify f within State and returning old state" {
      forAll(genToken, Gen.string()) { generatedToken, string ->
        tokenLens.assignOld(string).run(generatedToken) ==
          State { token: Token ->
            tokenLens.set(token, string) toT tokenLens.get(token)
          }.run(generatedToken)
      }
    }

    "assign_ f should be as modify f within State and returning Unit" {
      forAll(genToken, Gen.string()) { generatedToken, string ->
        tokenLens.assign_(string).run(generatedToken) ==
          State { token: Token ->
            tokenLens.set(token, string) toT Unit
          }.run(generatedToken)
      }
    }
  }
}
