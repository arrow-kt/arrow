package arrow.ap.tests

import arrow.renzu.RenzuProcessor
import java.io.File

class RenzuTest : APTest("arrow.ap.objects.renzu", false) {

  init {
    testProcessor(AnnotationProcessor(
      name = "Generates UML for nomnoml",
      sourceFiles = listOf("OptionMonoidInstance.java", "OptionSemigroupInstance.java"),
      destFile = "SampleUML.txt",
      processor = RenzuProcessor(isolateForTests = true)
    ), generationDir = File("./infographic", "").also { it.mkdirs() })
  }
}
