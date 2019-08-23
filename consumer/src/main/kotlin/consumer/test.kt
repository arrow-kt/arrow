package consumer

object test {
  @JvmStatic
  fun main(args : Array<String>) {
    println(HigherKinds.implicitArity1Casts())
    println(HigherKinds.implicitArity2Casts())

    println(Typeclasses.resolution1())

    println(Comprehensions.fx())
    println(Comprehensions.chainedBinds())
    println(Comprehensions.simpleBind())
    //println(Comprehensions.existentFlatMap())
  }
}