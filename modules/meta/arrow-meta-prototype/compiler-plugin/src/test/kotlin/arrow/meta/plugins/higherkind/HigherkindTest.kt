package arrow.meta.plugins.higherkind

import arrow.meta.plugin.testing.Check.ExpectedGeneratedSourceCode
import arrow.meta.plugin.testing.Check.ExpectedGeneratedClasses
import arrow.meta.plugin.testing.CompilationData
import arrow.meta.plugin.testing.CompilationStatus
import arrow.meta.plugin.testing.assertThis
import arrow.meta.plugin.testing.contentFromResource
import org.junit.Test

class HigherkindTest {

  @Test
  fun `initial_test`() {
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

}
