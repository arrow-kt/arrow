package arrow.meta.plugins.higherkind

import arrow.meta.plugin.testing.CompilationData
import arrow.meta.plugin.testing.CompilationResult
import arrow.meta.plugin.testing.contentFromResource
import arrow.meta.plugin.testing.testCompilation
import org.junit.Test

class HigherkindTest {

  @Test
  fun `initial_test`() {

    testCompilation(
      CompilationData(
        sourceFileName = "Example.kt",
        sourceContent = contentFromResource(javaClass,"Example.kt.source"),
        generatedFileContent = contentFromResource(javaClass, "Example.kt.meta"),
        generatedClasses = arrayListOf("ExampleKt", "ForId2", "Id2", "ForId2\$Companion"),
        compilationResult = CompilationResult.OK
      )
    )
  }

}
