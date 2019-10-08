package arrow.meta.plugins.comprehensions

import arrow.meta.plugin.testing.CompilationResult
import arrow.meta.plugin.testing.CompilationData
import arrow.meta.plugin.testing.testCompilation
import arrow.meta.plugin.testing.CompilationStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.File
import java.net.URLClassLoader
import java.util.Optional

class ComprehensionsTest {

  @Test
  fun `simple_case`() {

    val compilationResult: Optional<CompilationResult> = testCompilation(
      CompilationData(
        sourceFileName = "SimpleCase.kt",
        sourceContent = """
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
  
          fun test1(): IO<Int> =
            IO.fx {
              val a: Int by IO(1)
              val b: Int by IO(2)
              a + b
            }
        """,
        generatedFileContent = Optional.empty(),
        generatedClasses = arrayListOf("\$test1\$lambda-1\$0", "IO\$Companion", "IO", "SimpleCaseKt\$\$test1\$lambda-1\$lambda-0\$1", "SimpleCaseKt"),
        compilationStatus = CompilationStatus.OK
      )
    )

    assertThat(compilationResult.isPresent).isTrue()

    val classLoader = URLClassLoader(arrayOf(File(compilationResult.get().classesDirectory).toURI().toURL()))
    val result = classLoader.loadClass("SimpleCaseKt").getMethod("test1").invoke(null)

    assertThat(result.javaClass.getField("value").get(result)).isEqualTo(3)
  }
}
