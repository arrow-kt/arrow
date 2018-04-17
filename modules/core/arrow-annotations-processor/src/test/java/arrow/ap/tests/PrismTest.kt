package arrow.ap.tests

import arrow.optics.OpticsProcessor

class PrismTest : APTest("arrow.ap.objects.prism") {

  init {

    testProcessor(AnnotationProcessor(
      name = "Prisms cannot be generated for data class",
      sourceFile = "PrismDataClass.java",
      errorMessage = "Prisms can only be generated for sealed classes",
      processor = OpticsProcessor()
    ))

    testProcessor(AnnotationProcessor(
      name = "Prisms are generated for sealed class",
      sourceFile = "Prism.java",
      destFile = "Prism.kt",
      processor = OpticsProcessor()
    ))


  }

}