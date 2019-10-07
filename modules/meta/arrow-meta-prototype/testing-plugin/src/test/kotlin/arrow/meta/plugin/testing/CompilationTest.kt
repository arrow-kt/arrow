package arrow.meta.plugin.testing

import org.junit.Test

class CompilationTest {

  @Test
  fun `metadebug consideration works as expected`() {

    testCompilation(
      CompilationData(
        sourceFileName = "Example.kt",
        sourceContent = contentFromResource(javaClass, "Example.kt.source"),
        generatedFileContent = contentFromResource(javaClass, "Example.kt.meta"),
        generatedClasses = arrayListOf("ExampleKt", "ForId2", "Id2", "ForId2\$Companion"),
        compilationResult = CompilationResult.OK
      )
    )
  }

  @Test
  fun `compilation errors are detected`() {

    testCompilation(
      CompilationData(
        sourceFileName = "Example.kt",
        sourceContent = "classs Error",
        generatedFileContent = "",
        generatedClasses = arrayListOf(),
        compilationResult = CompilationResult.COMPILATION_ERROR
      )
    )
  }

}
