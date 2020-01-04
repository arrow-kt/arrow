package arrow.ap.tests

import arrow.generic.ProductProcessor

class ProductTest : APTest("arrow.ap.objects.generic", enforcePackage = false) {

  init {

    testProcessor(AnnotationProcessor(
      name = "Product instances cannot be generated for huge classes",
      sourceFiles = listOf("ProductXXL.java"),
      errorMessage = "arrow.ap.objects.generic.ProductXXL up to 22 constructor parameters is supported",
      processor = ProductProcessor()
    ))

    testProcessor(AnnotationProcessor(
      name = "Product instances generation requires companion object declaration",
      sourceFiles = listOf("ProductWithoutCompanion.java"),
      errorMessage = "@product annotated class arrow.ap.objects.generic.ProductWithoutCompanion needs to declare companion object.",
      processor = ProductProcessor()
    ))

    testProcessor(AnnotationProcessor(
      name = "Semigroup instance will be generated for data class annotated with @product([DerivingTarget.SEMIGROUP])",
      sourceFiles = listOf("Semigroup.java"),
      destFile = "Semigroup.kt",
      processor = ProductProcessor()
    ))

    testProcessor(AnnotationProcessor(
      name = "Semigroup and Monoid instances will be generated for data class annotated with @product([DerivingTarget.MONOID])",
      sourceFiles = listOf("Monoid.java"),
      destFile = "Monoid.kt",
      processor = ProductProcessor()
    ))

    testProcessor(AnnotationProcessor(
      name = "Method `.tupled()` will be generated for data class annotated with @product([DerivingTarget.TUPLED])",
      sourceFiles = listOf("Tupled.java"),
      destFile = "Tupled.kt",
      processor = ProductProcessor()
    ))

    testProcessor(AnnotationProcessor(
      name = "Method `.toHList()` will be generated for data class annotated with @product([DerivingTarget.HLIST])",
      sourceFiles = listOf("HList.java"),
      destFile = "HList.kt",
      processor = ProductProcessor()
    ))

    testProcessor(AnnotationProcessor(
      name = "Applicative instance and method `.tupled()` will be generated for data class annotated with @product([DerivingTarget.APPLICATIVE])",
      sourceFiles = listOf("Applicative.java"),
      destFile = "Applicative.kt",
      processor = ProductProcessor()
    ))

    testProcessor(AnnotationProcessor(
      name = "Eq instance will be generated for data class annotated with @product([DerivingTarget.EQ])",
      sourceFiles = listOf("Eq.java"),
      destFile = "Eq.kt",
      processor = ProductProcessor()
    ))

    testProcessor(AnnotationProcessor(
      name = "Show instance will be generated for data class annotated with @product([DerivingTarget.SHOw])",
      sourceFiles = listOf("Show.java"),
      destFile = "Show.kt",
      processor = ProductProcessor()
    ))
  }
}
