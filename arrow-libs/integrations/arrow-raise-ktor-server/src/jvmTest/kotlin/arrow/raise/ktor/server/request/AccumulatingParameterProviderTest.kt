package arrow.raise.ktor.server.request

import arrow.core.Either
import arrow.core.left
import arrow.core.leftNel
import arrow.core.nonEmptyListOf
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.RaiseAccumulate
import arrow.core.raise.accumulate
import arrow.core.raise.either
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beOfType
import io.ktor.http.*
import kotlin.test.Test

@OptIn(ExperimentalRaiseAccumulateApi::class)
class AccumulatingParameterProviderTest {

  private fun withDelegate(
    parameters: ParametersBuilder.() -> Unit = {},
    parameter: (String) -> Parameter = Parameter::Query,
    test: RaiseAccumulate<RequestError>.(delegate: AccumulatingParameterProvider) -> Unit
  ) = accumulate(::either) {
    val parameters = parameters { parameters() }
    val delegate = AccumulatingParameterProvider(this, parameters, parameter)
    test(delegate)
  }

  @Test
  @Suppress("UnusedVariable")
  fun `unused delegates are all raised`() {
    withDelegate { delegate ->
      val missingStringInvoke by delegate()
      val missingIntReifiedInvoke by delegate<Int>()
      val missingIntTransform by delegate { it.toInt() }
      val missingIntReified: Int by delegate
      val missingStringExplicitName by delegate("explicit")
      val missingIntReifiedExplicitName by delegate<Int>("explicit")
    } shouldBe nonEmptyListOf(
      MissingParameter(Parameter.Query("missingStringInvoke")),
      MissingParameter(Parameter.Query("missingIntReifiedInvoke")),
      MissingParameter(Parameter.Query("missingIntTransform")),
      MissingParameter(Parameter.Query("missingIntReified")),
      MissingParameter(Parameter.Query("explicit")),
      MissingParameter(Parameter.Query("explicit")),
    ).left()
  }

  @Test
  fun `successful delegates don't raise on use`() {
    withDelegate(parameters = {
      append("stringInvoke", "one")
      append("intReifiedInvoke", "2")
      append("intTransform", "3")
      append("intReified", "4")
      append("explicit", "five")
      append("explicitInt", "6")
    }) { delegate ->
      val stringInvoke by delegate()
      val intReifiedInvoke by delegate<Int>()
      val intTransform by delegate { it.toInt() }
      val intReified: Int by delegate
      val stringExplicitName by delegate("explicit")
      val intReifiedExplicitName by delegate<Int>("explicitInt")
      val intTransformExplicitName by delegate("explicit") { it.toInt(36) }

      stringInvoke shouldBe "one"
      intReifiedInvoke shouldBe 2
      intTransform shouldBe 3
      intReified shouldBe 4
      stringExplicitName shouldBe "five"
      intReifiedExplicitName shouldBe 6
      intTransformExplicitName shouldBe "five".toInt(36)

    } should beOfType<Either.Right<Int>>()
  }

  @Test
  fun `all erroneous delegates are raised on first use`() {
    withDelegate { delegate ->
      val missing by delegate()
      val alsoMissing by delegate()
      alsoMissing + missing
      val notIncluded by delegate()
    } shouldBe nonEmptyListOf(
      MissingParameter(Parameter.Query("missing")),
      MissingParameter(Parameter.Query("alsoMissing")),
    ).left()
  }

  @Test
  fun `transforming delegate raises on error`() {
    withDelegate(parameters = {
      append("missing", "found!")
    }) { delegate ->
      val missing: String by delegate { raise(it.uppercase()) }
      missing
    } shouldBe Malformed(Parameter.Query("missing"), "FOUND!").leftNel()
  }
}
