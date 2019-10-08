package arrow.meta.plugins.higherkind

import arrow.meta.plugin.testing.CompilationData
import arrow.meta.plugin.testing.CompilationStatus
import arrow.meta.plugin.testing.contentFromResource
import arrow.meta.plugin.testing.testCompilation
import org.junit.Test
import java.util.Optional

class HigherkindTest {

  @Test
  fun `initial_test`() {

    testCompilation(
      CompilationData(
        sourceFileName = "Example.kt",
        sourceContent = contentFromResource(javaClass,"Example.kt.source"),
        generatedFileContent = Optional.of(contentFromResource(javaClass, "Example.kt.meta")),
        generatedClasses = arrayListOf("ExampleKt", "ForId2", "Id2", "ForId2\$Companion"),
        compilationStatus = CompilationStatus.OK
      )
    )
  }

}
