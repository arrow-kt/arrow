package arrow.ap.tests

import arrow.renzu.RenzuProcessor

class RenzuTest : APTest("arrow.ap.objects.renzu") {

  init {
    testProcessor(AnnotationProcessor(
      name = "Generates UML for nomnoml",
      sourceFiles = listOf("OptionMonoidInstance.java", "OptionSemigroupInstance.java"),
      destFile = "SampleUML.txt",
      processor = RenzuProcessor()
    ))
  }
}