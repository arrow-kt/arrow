package arrow.meta.plugin.testing

import org.junit.Test

class CompilationTest {

  @Test
  fun `metadebug consideration works as expected`() {

    assertCompilation(
      CompilationData(
        sourceFileName = "Example.kt",
        sourceContent = contentFromResource(javaClass, "Example.kt.source"),
        generatedFileContent = contentFromResource(javaClass, "Example.kt.meta"),
        generatedClasses = arrayListOf("ExampleKt", "ForId2", "Id2", "ForId2\$Companion"),
        compilationStatus = CompilationStatus.OK
      )
    )
  }

  @Test
  fun `compilation errors are detected`() {

    assertCompilation(
      CompilationData(
        sourceFileName = "Example.kt",
        sourceContent = "classs Error",
        generatedFileContent = null,
        generatedClasses = arrayListOf(),
        compilationStatus = CompilationStatus.COMPILATION_ERROR
      )
    )
  }

}
