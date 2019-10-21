package arrow.meta.plugins.typeclasses

import arrow.meta.plugin.testing.Check.ExpectedGeneratedSourceCode
import arrow.meta.plugin.testing.Check.ExpectedGeneratedClasses
import arrow.meta.plugin.testing.CompilationData
import arrow.meta.plugin.testing.CompilationStatus
import arrow.meta.plugin.testing.assertThis
import org.junit.Test

class TypeClassesTest {

  @Test
  fun `simple_case`() {
    assertThis(CompilationData(
      dependencies = listOf(
        "arrow-annotations:rr-meta-prototype-integration-SNAPSHOT",
        "arrow-core-data:0.10.1"
      ),
      sourceFilename = "Example.kt",
      sourceCode = """
        | import arrow.Kind
        | import arrow.given
        | import arrow.core.Some
        | import arrow.core.Option
        |
        | //metadebug
        |
        | interface Mappable<F> {
        |   fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B>
        | }
        |
        | object Test {
        |   fun <F> Kind<F, Int>.addOne(M: Mappable<F> = given): Kind<F, Int> =
        |     map { it + 1 }
        | }
        |
        | fun foo() {
        |   Test.run {
        |     val result: Option<Int> = Some(1).addOne()
        |     println(result)
        |   }
        | }
        |""".trimMargin(),
      checks = listOf(
        ExpectedGeneratedSourceCode(code = """
          | import arrow.Kind
          | import arrow.given
          | import arrow.core.Some
          | import arrow.core.Option
          | 
          | //meta: <date>
          | 
          | interface Mappable<F> {
          |   fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B>
          | }
          | 
          | object Test {
          |   fun <F> Kind<F, Int>.addOne(M: Mappable<F> = given): Kind<F, Int> =
          |      M.run { map { it + 1 } }
          | }  
          | 
          | fun foo() {
          |   Test.run {
          |     val result: Option<Int> = Some(1).addOne()
          |     println(result)
          |   }
          | }
          |""".trimMargin()),
        ExpectedGeneratedClasses(filenamesWithoutExt = listOf(
          "ExampleKt", "Mappable\$DefaultImpls", "Mappable", "Test\$\$addOne\$lambda-1\$lambda-0\$0", "Test"
        ))
      ),
      expectedStatus = CompilationStatus.OK
    ))
  }
}
