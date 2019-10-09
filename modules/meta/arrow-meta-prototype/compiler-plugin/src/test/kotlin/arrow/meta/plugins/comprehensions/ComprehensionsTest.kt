package arrow.meta.plugins.comprehensions

import arrow.meta.plugin.testing.CompilationResult
import arrow.meta.plugin.testing.CompilationData
import arrow.meta.plugin.testing.assertCompilation
import arrow.meta.plugin.testing.CompilationStatus
import arrow.meta.plugin.testing.invoke
import arrow.meta.plugin.testing.getFieldFrom
import arrow.meta.plugin.testing.InvocationData
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ComprehensionsTest {

  companion object {
    const val IO_CLASS_4_TESTS = """
          import kotlin.reflect.KProperty
  
          //metadebug
  
          class IO<A>(val value: A) {
  
            operator fun getValue(value: Any?, property: KProperty<*>): A = TODO()
  
            fun <B> flatMap(f: (A) -> IO<B>): IO<B> =
              f(value)
  
            companion object {
              fun <A> fx(f: IO.Companion.() -> A): IO<A> = TODO()
              fun <A> just(a: A): IO<A> = IO(a)
            }
          }
    """
  }

  @Test
  fun `simple_case`() {

    val compilationResult: CompilationResult? = assertCompilation(
      CompilationData(
        sourceFileName = "SimpleCase.kt",
        sourceContent = """
          $IO_CLASS_4_TESTS
          
          fun test(): IO<Int> =
            IO.fx {
              val a: Int by IO(1)
              val b: Int by IO(2)
              a + b
            }
        """,
        generatedFileContent = null,
        generatedClasses = arrayListOf("SimpleCaseKt", "IO", "IO\$Companion", "SimpleCaseKt\$\$test\$lambda-1\$lambda-0\$1", "\$test\$lambda-1\$0"),
        compilationStatus = CompilationStatus.OK
      )
    )

    assertThat(compilationResult).isNotNull

    val resultForTest = invoke(
      InvocationData(
        classesDirectory = compilationResult?.classesDirectory,
        className = "SimpleCaseKt",
        methodName = "test"
      )
    )
    assertThat(resultForTest::class.simpleName).isEqualTo("IO")
    assertThat(getFieldFrom(resultForTest, "value")).isEqualTo(3)
  }

  @Test
  fun `simple_case_with_type_inference`() {

    val compilationResult: CompilationResult? = assertCompilation(
      CompilationData(
        sourceFileName = "SimpleCase.kt",
        sourceContent = """
          $IO_CLASS_4_TESTS
          
          fun test(): IO<Int> =
            IO.fx {
              val a by IO(1)
              val b by IO(2)
              a + b
            }
        """,
        generatedFileContent = null,
        generatedClasses = arrayListOf("SimpleCaseKt", "IO", "IO\$Companion", "SimpleCaseKt\$\$test\$lambda-1\$lambda-0\$1", "\$test\$lambda-1\$0"),
        compilationStatus = CompilationStatus.OK
      )
    )

    assertThat(compilationResult).isNotNull

    val resultForTest = invoke(
      InvocationData(
        classesDirectory = compilationResult?.classesDirectory,
        className = "SimpleCaseKt",
        methodName = "test"
      )
    )
    assertThat(resultForTest::class.simpleName).isEqualTo("IO")

    val field = getFieldFrom(resultForTest, "value")
    assertThat(field).isEqualTo(3)
    assertThat(field::class).isEqualTo(Int::class)
  }
}
