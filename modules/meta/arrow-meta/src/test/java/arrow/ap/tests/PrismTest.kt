package arrow.ap.tests

import arrow.optics.OpticsProcessor

class PrismTest : APTest("arrow.ap.objects.prism") {

  init {

    testProcessor(AnnotationProcessor(
      name = "Prisms cannot be generated for data class",
      sourceFiles = listOf("PrismDataClass.java"),
      errorMessage = """
      |Cannot generate arrow.optics.Prism for arrow.ap.objects.prism.PrismDataClass
      |                                          ^
      |  arrow.optics.OpticsTarget.PRISM is an invalid @optics argument for arrow.ap.objects.prism.PrismDataClass.
      |  It is only valid for sealed classes.
      """.trimMargin(),
      processor = OpticsProcessor()
    ))

    testProcessor(AnnotationProcessor(
      name = "Prism generation requires companion object declaration",
      sourceFiles = listOf("PrismWithoutCompanion.java"),
      errorMessage = "@optics annotated class arrow.ap.objects.prism.PrismWithoutCompanion needs to declare companion object.",
      processor = OpticsProcessor()
    ))

    testProcessor(AnnotationProcessor(
      name = "Prisms are generated for sealed class",
      sourceFiles = listOf("Prism.java"),
      destFile = "Prism.kt",
      processor = OpticsProcessor()
    ))

  }

}