package arrow.meta.plugins.comprehensions

import arrow.meta.plugin.testing.CompilationTest
import org.junit.Test

class ComprehensionsTest : CompilationTest {

  companion object {
    const val IO_CLASS_4_TESTS = """
      | import kotlin.reflect.KProperty
      |
      | //metadebug
      |
      | class IO<A>(val value: A) {
      |
      |   operator fun getValue(value: Any?, property: KProperty<*>): A = TODO()
      |
      |   fun <B> flatMap(f: (A) -> IO<B>): IO<B> =
      |     f(value)
      |
      |   companion object {
      |     fun <A> fx(f: IO.Companion.() -> A): IO<A> = TODO()
      |     fun <A> just(a: A): IO<A> = IO(a)
      |   }
      | }
      """
  }

  @Test
  fun `simple_case`() {
    """
    $IO_CLASS_4_TESTS
    |
    | fun test(): IO<Int> =
    |   IO.fx {
    |     val a: Int by IO(1)
    |     val b: Int by IO(2)
    |     a + b
    |   }
    |   
    |""" compilesTo """
      $IO_CLASS_4_TESTS
      |
      | fun test(): IO<Int> =
      |   IO(1).flatMap { a : Int ->
      |     IO(2).flatMap { b : Int ->
      |       IO.just(a + b)
      |     }
      |   }
      |   
      |""" andExpression "test().value" evalTo "3"
  }

  @Test
  fun `simple_case_with_type_inference`() {
    """
    $IO_CLASS_4_TESTS
    |
    | fun test(): IO<Int> =
    |   IO.fx {
    |     val a by IO(1)
    |     val b by IO(2)
    |     a + b
    |   }
    |   
    |""" compilesTo """
      $IO_CLASS_4_TESTS
      |
      | fun test(): IO<Int> =
      |   IO(1).flatMap { a  ->
      |     IO(2).flatMap { b  ->
      |       IO.just(a + b)
      |     }
      |   }
      |   
      |""" andExpression "test().value" evalTo "3"
  }

  @Test
  fun `nested_case_with_type_inference`() {
    """
    $IO_CLASS_4_TESTS
    |
    | fun test(): IO<Int> =
    |   IO.fx {
    |     val a by IO.fx {
    |       val a by IO(1)
    |       val b by IO(2)
    |       a + b
    |     }
    |     val b by IO.fx {
    |       val a by IO(3)
    |       val b by IO(4)
    |       a + b
    |     }
    |     a + b
    |   }
    |   
    |""" compilesTo """
      $IO_CLASS_4_TESTS
      |
      | fun test(): IO<Int> = 
      |   IO(1).flatMap { a ->
      |     IO(2).flatMap { b ->
      |       IO.just(a + b)
      |     }
      |   }.flatMap { a -> 
      |     IO(3).flatMap { a -> 
      |       IO(4).flatMap { b -> 
      |         IO.just(a + b)  
      |       }
      |     }.flatMap { b ->
      |       IO.just(a + b)
      |     }
      |   }
      |   
      |""" andExpression "test().value" evalTo "10"
  }

  @Test
  fun `mixed_properties_and_expressions`() {
    """
    $IO_CLASS_4_TESTS
    |
    | fun test(): IO<Int> =
    |   IO.fx {
    |     val a by IO(1)
    |     val t = a + 1
    |     val b by IO(2)
    |     val y = a + b
    |     val f by IO(3)
    |     val n = a + 1
    |     val g by IO(4)
    |     y + f + g + t + n
    |   }
    |   
    |""" compilesTo """
      $IO_CLASS_4_TESTS
      |
      | fun test(): IO<Int> =
      |   IO(1).flatMap { a -> 
      |     val t = a + 1
      |     IO(2).flatMap { b -> 
      |       val y = a + b
      |       IO(3).flatMap { f -> 
      |         val n = a + 1
      |         IO(4).flatMap { g -> 
      |           IO.just(y + f + g + t + n)  
      |         }
      |       }
      |     } 
      |   }
      |   
      |""" andExpression "test().value" evalTo "14"
  }

  @Test
  fun `just`() {
    """
    $IO_CLASS_4_TESTS
    |
    | fun test(): IO<Int> =
    |   IO.fx { 1 + 1 }
    |
    |""" compilesTo """
      $IO_CLASS_4_TESTS
      |
      | fun test(): IO<Int> =
      |   IO.just(1 + 1)
      |   
      |""" andExpression "test().value" evalTo "2"
  }

  @Test
  fun `unresolved_reference_error`() {
    """
    $IO_CLASS_4_TESTS
    |
    | fun test(): IO<Int> =
    |   IO.fx { a + 1 }
    |
    |""" emitErrorDiagnostic "Unresolved reference: a"
  }

//  @Test
//  fun `Does not break other delegations`() {
//    """
//    $IO_CLASS_4_TESTS
//    |
//    | fun test(): IO<Int> =
//    |   IO.fx {
//    |     val a by IO(1)
//    |     val b: Int by lazy { 2 }
//    |     a + b
//    |   }
//    |
//    |""" compilesTo """
//      $IO_CLASS_4_TESTS
//      |
//      | fun test(): IO<Int> =
//      |   IO(1).flatMap { a ->
//      |     lazy { 2 }.flatMap { b : Int ->
//      |       IO.just(a + b)
//      |      }
//      |   }
//      |
//      |"""
//  }
}
