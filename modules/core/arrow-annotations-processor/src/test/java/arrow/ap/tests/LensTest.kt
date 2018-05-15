package arrow.ap.tests

import arrow.optics.OpticsProcessor

class LensTest : APTest("arrow.ap.objects.lens") {

  init {

    testProcessor(AnnotationProcessor(
      name = "Lenses cannot be generated for sealed classes",
      sourceFiles = listOf("LensSealed.java"),
      errorMessage = """
    |Cannot generate arrow.optics.Lens for arrow.ap.objects.lens.LensSealed
    |                                         ^
    |  arrow.optics.OpticsTarget.LENS is an invalid @optics argument for arrow.ap.objects.lens.LensSealed.
    |  It is only valid for data classes.
    """.trimMargin(),
      processor = OpticsProcessor()
    ))

    testProcessor(AnnotationProcessor(
      name = "Lenses will be generated for data class",
      sourceFiles = listOf("Lens.java"),
      destFile = "Lens.kt",
      processor = OpticsProcessor()
    ))

  }

}