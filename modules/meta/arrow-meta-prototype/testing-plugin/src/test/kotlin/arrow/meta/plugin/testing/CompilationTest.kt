package arrow.meta.plugin.testing

import arrow.meta.plugin.testing.Check.ExpectedCompilationError
import arrow.meta.plugin.testing.Check.ExpectedGeneratedClasses
import arrow.meta.plugin.testing.Check.ExpectedGeneratedSourceCode
import org.junit.Test

class CompilationTest {

  @Test
  fun `metadebug consideration works as expected`() {
    assertThis(CompilationData(
      //
      // TODO: waiting for the arrow-annotations release which contains higherkind annotation
      //    classpaths = listOf(classpathOf("arrow-annotations:x.x.x"))
      //
      dependencies = listOf("arrow-annotations:rr-meta-prototype-integration-SNAPSHOT"),
      sourceFilename = "Example.kt",
      sourceCode = contentFromResource(javaClass, "Example.kt.source"),
      expectedStatus = CompilationStatus.OK,
      checks = listOf(
        ExpectedGeneratedSourceCode(code = contentFromResource(javaClass, "Example.kt.meta")),
        ExpectedGeneratedClasses(filenamesWithoutExt = listOf("ExampleKt", "ForId2", "Id2", "ForId2\$Companion"))
      )
    ))
  }

  @Test
  fun `compilation errors are detected`() {
    assertThis(CompilationData(
      sourceFilename = "Example.kt",
      sourceCode = "classs Error",
      expectedStatus = CompilationStatus.COMPILATION_ERROR,
      checks = listOf(
        ExpectedCompilationError(partialMessage = "Expecting a top level declaration")
      )
    ))
  }
}
