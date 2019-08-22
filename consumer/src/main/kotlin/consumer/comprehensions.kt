package consumer

import arrow.not

object Comprehensions {
  fun fx(): Option<Int> =
    Option.fx {
      val x = !Option.Some(1)
      val y = !Option.Some(1)
      x + y
    }

  fun chainedBinds(): Option<Int> =
    Option.Some(!Option.Some(!Option.Some(!Option.Some(1))))

  fun simpleBind(): Option<Int> {
    val x = !Option.Some(1)
    return Option.Some(x)
  }

  //TODO rewrite bindings order, scoping is wrong
//  fun existentFlatMap(): Option<Int> =
//    Option.Some(1).map { x ->
//      !Option.Some(x)
//    }
}