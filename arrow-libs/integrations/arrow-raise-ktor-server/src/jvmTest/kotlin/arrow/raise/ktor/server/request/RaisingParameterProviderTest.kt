package arrow.raise.ktor.server.request

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beOfType
import io.ktor.http.*
import kotlin.test.Test

class RaisingParameterProviderTest {

  private fun withDelegate(
    parameters: ParametersBuilder.() -> Unit = {},
    parameter: (String) -> Parameter = Parameter::Query,
    test: Raise<RequestError>.(delegate: RaisingParameterProvider) -> Unit
  ) = either {
    val parameters = parameters { parameters() }
    val delegate = RaisingParameterProvider(this, parameters, parameter)
    test(delegate)
  }

  @Test
  fun `unused delegates all missing doesn't raise`() {
    withDelegate { delegate ->
      val missingStringInvoke by delegate()
      val missingIntReifiedInvoke by delegate<Int>()
      val missingIntTransform by delegate { it.toInt() }
      val missingIntReified: Int by delegate
      val missingStringExplicitName by delegate("explicit")
      val missingIntReifiedExplicitName by delegate<Int>("explicit")
    } shouldBe Either.Right(Unit)
  }

  @Test
  fun `successful delegates don't raise on use`() {
    withDelegate({
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
  fun `first used erroneous delegate raises`() {
    withDelegate { delegate ->
      val missing by delegate()
      val alsoMissing by delegate()
      alsoMissing + missing
    } shouldBe Either.Left(MissingParameter(Parameter.Query("alsoMissing")))
  }

  @Test
  fun `transforming delegate raises on error`() {
    withDelegate({
      append("missing", "found!")
    }) { delegate ->
      val missing: String by delegate { raise(it.uppercase()) }
      missing
    } shouldBe Either.Left(Malformed(Parameter.Query("missing"), "FOUND!"))
  }
}
