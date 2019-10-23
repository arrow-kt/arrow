package arrow.meta.plugins.higherkind

import arrow.meta.plugin.testing.Check.GeneratedSourceCode
import arrow.meta.plugin.testing.Check.GeneratedClasses
import arrow.meta.plugin.testing.CompilationData
import arrow.meta.plugin.testing.CompilationStatus
import arrow.meta.plugin.testing.assertThis
import arrow.meta.plugin.testing.contentFromResource
import org.junit.Test

class HigherkindTest {

  @Test
  fun `initial_test`() {
    assertThis(CompilationData(
      sourceFilename = "Example.kt",
      sourceCode = contentFromResource(javaClass, "Example.kt.source"),
      compilationStatus = CompilationStatus.OK,
      checks = listOf(
        GeneratedSourceCode(code = contentFromResource(javaClass, "Example.kt.meta")),
        GeneratedClasses(filenamesWithoutExt = listOf("ExampleKt", "ForId2", "Id2", "ForId2\$Companion"))
      )
    ))
  }

}
