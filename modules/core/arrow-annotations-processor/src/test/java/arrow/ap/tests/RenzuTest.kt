package arrow.ap.tests

import arrow.renzu.RenzuProcessor
import java.io.File

class RenzuTest : APTest("arrow.ap.objects.renzu") {

  init {
    testProcessor(AnnotationProcessor(
      name = "Generates UML for nomnoml",
      sourceFiles = listOf("OptionMonoidInstance.java", "OptionSemigroupInstance.java"),
      destFile = "SampleUML.txt",
      processor = RenzuProcessor()
    ), generationDir = File("./infographic/", "").also { it.mkdirs() })

    File("./infographic/", "").listFiles()?.forEach { it.delete() }
  }
}