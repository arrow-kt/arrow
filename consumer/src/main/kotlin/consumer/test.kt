package consumer

object test {
  @JvmStatic
  fun main(args : Array<String>) {
    val x: OptionOf<Int> = Option.None
    val y: Option<Int> = x
    println("run!: $y")
  }
}