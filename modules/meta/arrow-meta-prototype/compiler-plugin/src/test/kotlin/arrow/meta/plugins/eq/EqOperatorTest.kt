package arrow.meta.plugins.eq

import arrow.meta.plugin.testing.Check
import arrow.meta.plugin.testing.CompilationData
import arrow.meta.plugin.testing.CompilationStatus
import arrow.meta.plugin.testing.assertThis
import arrow.meta.plugin.testing.contentFromResource
import org.junit.Test

class EqOperatorTest {
  companion object {
    const val EQ_OPERATOR_CLASS = """
      |  sealed class Either<out A, out B> {
      |    class Left<out A> : Either<A, Nothing>()
      |    class Right<out B>(val value: B) : Either<Nothing, B>()
      |
      |    fun isEqual(): Boolean = 1 == 2
      |    fun <A, B> isEitherEqual(value: B): Boolean = Left<A>() == Right(value)
      |  }
      """
  }

  @Test
  fun `simple_case`() {
    assertThis(CompilationData(
      sourceFilename = "Example.kt",
      sourceCode = """
        $EQ_OPERATOR_CLASS
        |""".trimMargin(),
      checks = listOf(
        Check.GeneratedSourceCode(code = """
          |${contentFromResource(javaClass, "Example.kt.meta")}
          |
          | fun test(): IO<Int> =
          |   IO(1).flatMap { a : Int ->
          |     IO(2).flatMap { b : Int ->
          |       IO.just(a + b)
          |     }
          |   }
          |   
          |""".trimMargin()),
        Check.GeneratedClasses(filenamesWithoutExt = listOf(
          "ExampleKt", "Either", "Either\$Companion", "ExampleKt\$\$test\$lambda-1\$lambda-0\$1", "\$test\$lambda-1\$0")
        )
      ),
      compilationStatus = CompilationStatus.OK
    ))
  }
}
