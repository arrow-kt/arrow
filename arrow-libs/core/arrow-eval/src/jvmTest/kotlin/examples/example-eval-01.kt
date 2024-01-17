// This file was automatically generated from Eval.kt by Knit tool. Do not edit.
package arrow.eval.examples.exampleEval01

import arrow.eval.Eval

fun even(n: Int): Eval<Boolean> =
  Eval.always { n == 0 }.flatMap {
    if(it == true) Eval.now(true)
    else odd(n - 1)
  }

fun odd(n: Int): Eval<Boolean> =
  Eval.always { n == 0 }.flatMap {
    if(it == true) Eval.now(false)
    else even(n - 1)
  }

// if not wrapped in eval this type of computation would blow the stack and result in a StackOverflowError
fun main() {
  println(odd(100000).value())
}
