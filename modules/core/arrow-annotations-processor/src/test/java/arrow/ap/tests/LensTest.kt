package arrow.ap.tests

import arrow.optics.OpticsProcessor

class LensTest : APTest("arrow.ap.objects.lens") {

  init {

    testProcessor(AnnotationProcessor(
      name = "Lenses cannot be generated for sealed classes",
      sourceFile = "LensSealed.java",
      errorMessage = "Lenses can only be generated for data classes",
      processor = OpticsProcessor()
    ))

    testProcessor(AnnotationProcessor(
      name = "Lenses will be generated for data class",
      sourceFile = "Lens.java",
      destFile = "Lens.kt",
      processor = OpticsProcessor()
    ))

  }

}