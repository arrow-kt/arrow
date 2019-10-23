package arrow.meta.plugin.testing

import org.junit.Test

class ExampleTest : CompilationTest {

  //
  // TODO: waiting for the arrow-annotations release which contains higherkind annotation
  //    classpaths = listOf(classpathOf("arrow-annotations:x.x.x"))
  //

  @Test
  fun `accepts dependencies and the generated meta file to check the compilation result`() {
    """
    | import arrow.higherkind
    | 
    | //metadebug
    | 
    | @higherkind
    | class Id2<out A>(val value: A)
    | 
    | val x: Id2Of<Int> = Id2(1)
    | 
    """ withDependencies listOf("arrow-annotations:rr-meta-prototype-integration-SNAPSHOT") compilesTo """
      | import arrow.higherkind
      | 
      | //meta: <date>
      | 
      | @arrow.synthetic class ForId2 private constructor() { companion object }
      | @arrow.synthetic typealias Id2Of<A> = arrow.Kind<ForId2, A>
      | @arrow.synthetic typealias Id2KindedJ<A> = arrow.HkJ<ForId2, A>
      | @arrow.synthetic fun <A> Id2Of<A>.fix(): Id2<A> =
      | this as Id2<A>
      | @arrow.synthetic @higherkind /* empty? */class Id2 <out A> public constructor (val value: A) : Id2Of<A> {}
      | 
      | val x: Id2Of<Int> = Id2(1)
      | 
      """
  }

  @Test
  fun `emits error diagnostic when compilation fails`() {
    """
    | classsss Error
    | 
    """ emitErrorDiagnostic "Expecting a top level declaration"
  }
}
