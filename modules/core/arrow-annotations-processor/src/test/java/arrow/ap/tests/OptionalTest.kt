package arrow.ap.tests

import arrow.optics.OpticsProcessor

class OptionalTest : APTest("arrow.ap.objects.optional") {

  init {

    testProcessor(AnnotationProcessor(
      name = "Optionals cannot be generated for sealed class",
      sourceFile = "OptionalSealed.java",
      errorMessage = "Optionals can only be generated for data classes",
      processor = OpticsProcessor()
    ))

    testProcessor(AnnotationProcessor(
      name = "Optionals will be generated for data class",
      sourceFile = "Optional.java",
      destFile = "Optional.kt",
      processor = OpticsProcessor()
    ))

  }

}