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
      name = "Lens generation requires companion object declaration",
      sourceFiles = listOf("LensWithoutCompanion.java"),
      errorMessage = "@optics annotated class arrow.ap.objects.lens.LensWithoutCompanion needs to declare companion object.",
      processor = OpticsProcessor()
    ))

    testProcessor(AnnotationProcessor(
      name = "Lenses will be generated for data class",
      sourceFiles = listOf("Lens.java"),
      destFile = "Lens.kt",
      processor = OpticsProcessor()
    ))

    testProcessor(AnnotationProcessor(
      name = "Lenses will be generated for secondary constructor arguments",
      sourceFiles = listOf("LensSecondaryConstructor.java"),
      destFile = "LensSecondaryConstructor.kt",
      processor = OpticsProcessor()
    ))

  }

}