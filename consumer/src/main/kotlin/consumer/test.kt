package consumer

object test {
  @JvmStatic
  fun main(args : Array<String>) {
    val x: OptionOf<Int> = Option.None
    /**
     * `x` is implicitly casted since the KindAwareTypeChecker
     * establishes an iso between OptionOf<Int> <-> Option<Int>
     */
    val y: Option<Int> = x
    println("run!: $y")
  }
}