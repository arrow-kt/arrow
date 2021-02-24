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
import arrow.optics.mtl.extract
import arrow.optics.mtl.extractMap
import arrow.optics.mtl.toReader
import arrow.optics.mtl.toState
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class GetterTest : UnitSpec() {

  init {

    "Asking for the focus in a Reader" {
      forAll(genToken) { token: Token ->
        tokenGetter.ask().runId(token) == token.value
      }
    }

    "toReader is an alias for ask" {
      forAll(genToken) { token: Token ->
        tokenGetter.ask().runId(token) == tokenLens.toReader().runId(token)
      }
    }

    "Asks with f is the same as applying f to the focus of the lens" {
      forAll(genToken, Gen.functionAToB<String, String>(Gen.string())) { token, f ->
        tokenGetter.asks(f).runId(token) == f(token.value)
      }
    }

    "Extract should extract the focus from the state" {
      forAll(genToken) { generatedToken ->
        tokenGetter.extract().run(generatedToken) ==
          State { token: Token ->
            token toT tokenGetter.get(token)
          }.run(generatedToken)
      }
    }

    "toState should be an alias to extract" {
      forAll(genToken) { token ->
        tokenGetter.toState().run(token) == tokenGetter.extract().run(token)
      }
    }

    "extractMap with f should be same as extract and map" {
      forAll(genToken, Gen.functionAToB<String, String>(Gen.string())) { token, f ->
        tokenGetter.extractMap(f).run(token) == tokenGetter.extract().map(f).run(token)
      }
    }
  }
}
