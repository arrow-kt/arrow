package arrow.ap.tests

import arrow.optics.OpticsProcessor

class IsoTest : APTest("arrow.ap.objects.iso") {

  init {

    testProcessor(AnnotationProcessor(
      name = "Isos cannot be generated for sealed classes",
      sourceFile = "IsoSealed.java",
      errorMessage = "Isos can only be generated for data classes",
      processor = OpticsProcessor()
    ))

    testProcessor(AnnotationProcessor(
      name = "Isos cannot be generated for huge classes",
      sourceFile = "IsoXXL.java",
      errorMessage = "Iso generation is not supported for data classes with more than 22 constructor parameters",
      processor = OpticsProcessor()
    ))

    testProcessor(AnnotationProcessor(
      name = "Isos will be generated for data class",
      sourceFile = "Iso.java",
      destFile = "Iso.kt",
      processor = OpticsProcessor()
    ))


  }

}