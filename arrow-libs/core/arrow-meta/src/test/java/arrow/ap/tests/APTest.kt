package arrow.ap.tests

import com.google.common.collect.ImmutableList
import com.google.common.io.Files
import com.google.testing.compile.CompilationSubject.assertThat
import com.google.testing.compile.Compiler.javac
import com.google.testing.compile.JavaFileObjects
import io.kotlintest.shouldBe
import io.kotlintest.specs.AbstractStringSpec
import java.io.File
import java.nio.file.Paths

abstract class APTest(
  private val pckg: String,
  private val enforcePackage: Boolean = true
) : AbstractStringSpec() {

  fun testProcessor(
    vararg processor: AnnotationProcessor,
    generationDir: File = Files.createTempDir(),
    actualFileLocation: (File) -> String = { it.path }
  ) {

    processor.forEach { (name, sources, dest, proc, error) ->

      val parent = File(".").absoluteFile.parent

      val stubs = Paths.get(parent, "models", "build", "tmp", "kapt3", "stubs", "main", *pckg.split(".").toTypedArray()).toFile()
      val expectedDir = Paths.get("", "src", "test", "resources", *pckg.split(".").toTypedArray()).toFile()

      if (dest == null && error == null) {
        throw Exception("Destination file and error cannot be both null")
      }

      if (dest != null && error != null) {
        throw Exception("Destination file or error must be set")
      }

      name {

        val compilation = javac()
          .withProcessors(proc)
          .withOptions(ImmutableList.of("-Akapt.kotlin.generated=$generationDir", "-proc:only"))
          .compile(sources.map {
            val stub = File(stubs, it).toURI().toURL()
            JavaFileObjects.forResource(stub)
          })

        if (error != null) {

          assertThat(compilation)
            .failed()
          assertThat(compilation)
            .hadErrorContaining(error)
        } else {

          assertThat(compilation)
            .succeeded()

          val targetDir = if (enforcePackage) File("${generationDir.absolutePath}/${pckg.replace(".", "/")}") else generationDir
          targetDir.listFiles().size shouldBe 1

          val expected = File(expectedDir, dest).readText()
          val actual = File(actualFileLocation(targetDir)).listFiles()[0].readText()

          actual.replace("\r\n", "\n") shouldBe expected.replace("\r\n", "\n")
        }
      }
    }
  }
}
