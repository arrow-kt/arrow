package arrow.ap.tests

import arrow.optics.*

class DSLTest : APTest("arrow.ap.objects.dsl") {

  init {

    testProcessor(AnnotationProcessor(
      name = "DSL cannot be generated for instance functions",
      sourceFiles = listOf("DSLInstanceMethod.java"),
      errorMessage = """
        |Cannot generate Optics DSL for method()
        |                                 ^
        |  arrow.optics.OpticsTarget.DSL is an invalid @${opticsAnnotationClass.simpleName} argument for method().
        |  It is only valid for data classes and sealed classes or top level functions without parameters.
      """.trimMargin(),
      processor = OpticsProcessor()
    ))

    testProcessor(AnnotationProcessor(
      name = "DSL cannot be generated for instance functions",
      sourceFiles = listOf("DSLWithParamsKt.java"),
      errorMessage = """
        |Cannot generate Optics DSL for <A>index(int)
        |                                  ^
        |  Top level Function annotated with @${opticsAnnotationClass.simpleName} cannot have any parameters.
        """.trimMargin(),
      processor = OpticsProcessor()
    ))

    testProcessor(AnnotationProcessor(
      name = "DSL target must return optic",
      sourceFiles = listOf("DSLNoOpticKt.java"),
      errorMessage = """
        |Cannot generate Optics DSL for notOptic()
        |                                  ^
        |  Top level Function annotated with @${opticsAnnotationClass.simpleName} must return monomorphic optic. Candidates are $validOptics.
        """.trimMargin(),
      processor = OpticsProcessor()
    ))

    testProcessor(AnnotationProcessor(
      name = "DSL target must return monomorphic optic",
      sourceFiles = listOf("DSLPOpticKt.java"),
      errorMessage = """
        |Cannot generate Optics DSL for <A,B>pOptic()
        |                                  ^
        |  Top level Function annotated with @${opticsAnnotationClass.simpleName} must return monomorphic optic. Candidates are $validOptics.
        """.trimMargin(),
      processor = OpticsProcessor()
    ))

    testProcessor(AnnotationProcessor(
      name = "DSL can be generated for top-level functions",
      sourceFiles = listOf("DSLKt.java"),
      destFile = "DSL.kt",
      processor = OpticsProcessor()
    ))

  }

}