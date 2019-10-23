package arrow.meta.plugins.eq

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import io.github.classgraph.ClassGraph
import org.junit.Test
import java.io.File

// TODO no actual tests here, but unit testing will need to be written for the proof extensions and so on
// focus for now is just creating an IR tree dump and seeing what gets spit out
class EqOperatorTest {

  companion object {
    const val EQ_FUNCTION = "@extension fun isEqual(): Boolean = 1 == 2"
    const val EQ_EXTENSION = """
      | object Id
      | 
      | @extension
      | fun IdEq() : Eq<Id> = Id.eq().run {
      |   Id.eqv(Id)
      | }
      | val x = Id == Id
      |
      """
    const val EQ_OPERATOR_CLASS = """
      |  sealed class Either<out A, out B> {
      |    class Left<out A> : Either<A, Nothing>()
      |    class Right<out B>(val value: B) : Either<Nothing, B>()
      |
      |    fun <A, B> isEitherEqual(value: B): Boolean = Left<A>() == Right(value)
      |  }
      """
  }

  @Test
  fun `simple_case_function`() {
    compileSourceCode(EQ_FUNCTION)
    assert(true)
  }

  @Test
  fun `simple_case_extension_function`() {
    compileSourceCode(EQ_EXTENSION)
    assert(true)
  }

  private fun compileSourceCode(sourceCode: String) {
    KotlinCompilation().apply {
      sources = listOf(SourceFile.kotlin("Example.kt", sourceCode))
      classpaths = listOf(classpathOf("arrow-annotations:rr-meta-prototype-integration-SNAPSHOT"))
      pluginClasspaths = listOf(classpathOf("compiler-plugin"))
    }.compile()
  }

  private fun classpathOf(dependency: String): File {
    val regex = Regex(".*${dependency.replace(':', '-')}.*")
    return ClassGraph().classpathFiles.first { classpath -> classpath.name.matches(regex) }
  }

}
