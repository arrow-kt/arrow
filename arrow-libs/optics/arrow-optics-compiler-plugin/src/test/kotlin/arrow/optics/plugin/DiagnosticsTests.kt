@file:OptIn(ExperimentalCompilerApi::class)

package arrow.optics.plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test

/** Every situation in which `@optics` cannot generate what was asked for is reported (review #2, 1.4, 2.3, 3.1). */
class DiagnosticsTests {
  private val ineligible = "Only data classes, value classes with a single field, and sealed classes and interfaces can be annotated with @optics"

  @Test
  fun `plain class is rejected`() {
    """
      |$`package`
      |$imports
      |@optics class Plain(val x: Int) { companion object }
      """.failsWith { ineligible in it }
  }

  @Test
  fun `non-sealed interface is rejected`() {
    """
      |$`package`
      |$imports
      |@optics interface I
      """.failsWith { ineligible in it }
  }

  @Test
  fun `enum class is rejected`() {
    """
      |$`package`
      |$imports
      |@optics enum class E { A, B }
      """.failsWith { ineligible in it }
  }

  @Test
  fun `object is rejected`() {
    """
      |$`package`
      |$imports
      |@optics object O
      """.failsWith { ineligible in it }
  }

  @Test
  fun `data object is rejected`() {
    """
      |$`package`
      |$imports
      |@optics data object D
      """.failsWith { ineligible in it }
  }

  @Test
  fun `aliased annotation is rejected`() {
    """
      |$`package`
      |import arrow.optics.optics as lenses
      |$imports
      |@lenses data class X(val a: Int) { companion object }
      """.failsWith { "import aliases and type aliases are not supported" in it }
  }

  @Test
  fun `aliased copy annotation is rejected`() {
    """
      |$`package`
      |import arrow.optics.optics.copy as builder
      |$imports
      |@optics @builder data class X(val a: Int) { companion object }
      """.failsWith { "import aliases and type aliases are not supported" in it }
  }

  @Test
  fun `fully qualified annotation is accepted`() {
    """
      |$`package`
      |$imports
      |@arrow.optics.optics data class X(val a: Int) { companion object }
      |
      |val r = X.a.get(X(1)) == 1
      """.evals("r" to true)
  }

  @Test
  fun `annotation named Serializable without a companion is reported`() {
    """
      |$`package`
      |$imports
      |annotation class Serializable
      |@Serializable @optics data class X(val a: Int)
      """.failsWith { "annotation named 'Serializable'" in it }
  }

  @Test
  fun `annotation named Serializable with a companion is fine`() {
    """
      |$`package`
      |$imports
      |annotation class Serializable
      |@Serializable @optics data class X(val a: Int) { companion object }
      |
      |val r = X.a.get(X(1)) == 1
      """.evals("r" to true)
  }

  @Test
  fun `DSL target alone generates nothing and warns`() {
    """
      |$`package`
      |$imports
      |@optics([OpticsTarget.DSL]) data class Foo(val x: Int) { companion object }
      """.warnsWith { "No optics will be generated for this class" in it }
  }

  @Test
  fun `PRISM target on a data class warns`() {
    """
      |$`package`
      |$imports
      |@optics([OpticsTarget.PRISM]) data class Foo(val x: Int) { companion object }
      """.warnsWith { "No optics will be generated for this class" in it }
  }

  @Test
  fun `missing arrow-optics runtime is reported`() {
    val compilation = buildCompilation(
      sources = arrayOf(
        SourceFile.kotlin(
          SOURCE_FILENAME,
          """
          package p
          import arrow.optics.optics
          @optics data class X(val a: Int) { companion object }
          """.trimIndent(),
        ),
      ),
    )
    // classpaths are arrow-annotations, arrow-core, arrow-optics, in this order
    compilation.classpaths = compilation.classpaths.dropLast(1)
    val result = compilation.compile()
    result.exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
    result.messages shouldContain "@optics requires the arrow-optics library"
  }

  // ---- name clashes (review #2, 1.4) ---------------------------------------------------

  @Test
  fun `prism and shared-property lens with the same name are reported`() {
    """
      |$`package`
      |$imports
      |@optics sealed interface E {
      |  val id: String
      |  data class Id(override val id: String) : E
      |  data class Other(override val id: String) : E
      |  companion object
      |}
      """.failsWith { "Several optics would be named 'id'; none of them is generated" in it }
  }

  @Test
  fun `subclasses with the same simple name are reported`() {
    """
      |$`package`
      |$imports
      |@optics sealed interface S { companion object }
      |class A { data class Leaf(val v: Int) : S }
      |class B { data class Leaf(val v: Int) : S }
      """.failsWith { "Several optics would be named 'leaf'" in it }
  }

  @Test
  fun `user companion member with the same name wins, with a warning`() {
    """
      |$`package`
      |$imports
      |@optics data class U(val x: Int, val y: Int) { companion object { val x = 5 } }
      """.warnsWith { "The optic 'x' is not generated because the companion object already declares a member with that name" in it }
  }

  @Test
  fun `user companion member with the same name wins`() {
    """
      |$`package`
      |$imports
      |@optics data class U(val x: Int, val y: Int) { companion object { val x = 5 } }
      |
      |val r = U.x == 5 && U.y.get(U(1, 2)) == 2 && Lens.id<U>().y.get(U(1, 2)) == 2
      """.evals("r" to true)
  }
}
