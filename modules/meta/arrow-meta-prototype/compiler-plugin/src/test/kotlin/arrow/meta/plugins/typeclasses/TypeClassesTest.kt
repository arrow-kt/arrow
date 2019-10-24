package arrow.meta.plugins.typeclasses

import arrow.meta.plugin.testing.CompilationTest
import org.junit.Test

class TypeClassesTest : CompilationTest {

  @Test
  fun `simple_case`() {
    """
    | import arrow.Kind
    | import arrow.given
    | import arrow.core.Some
    | import arrow.core.Option
    | import arrow.extension
    | import arrow.core.ForOption
    | import arrow.core.fix
    | import arrow.core.None
    |
    | //metadebug
    |
    | @extension
    | object OptionMappable : Mappable<ForOption> {
    |   override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Kind<ForOption, B> =
    |     when (val o: Option<A> = this.fix()) {
    |       is Some -> Some(f(o.t))
    |       None -> None
    |     }
    | } 
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
    | fun foo(): Option<Int> {
    |   Test.run {
    |     return Some(1).addOne()
    |   }
    | }
    |""" withDependencies listOf("arrow-annotations:rr-meta-prototype-integration-SNAPSHOT", "arrow-core-data:0.10.1") compilesTo """
      |    
      | import arrow.Kind
      | import arrow.given
      | import arrow.core.Some
      | import arrow.core.Option
      | import arrow.extension
      | import arrow.core.ForOption
      | import arrow.core.fix
      | import arrow.core.None
      | 
      | //meta: <date>
      | 
      | @extension
      | object OptionMappable : Mappable<ForOption> {
      |   override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Kind<ForOption, B> =
      |     when(val o: Option<A> = this.fix()) {
      |       is Some -> Some(f(o.t))
      |       None -> None
      |     }
      | }
      | 
      | interface Mappable<F> {
      |   fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B>
      | }
      | 
      | object Test {
      |   fun <F> Kind<F, Int>.addOne(M: Mappable<F> = given): Kind<F, Int> =
      |     M.run { map { it + 1 } }
      | }
      | 
      | fun foo(): Option<Int> {
      |   Test.run {
      |     return Some(1).addOne()
      |   }
      | }
      |""" andExpression "foo()" evalTo "Some(2)"
  }
}
