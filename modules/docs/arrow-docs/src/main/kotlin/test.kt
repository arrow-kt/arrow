import arrow.effects.suspended.fx.Fx

fun main() {

  Fx.just(1).map { i ->
    val ii = i + 1
    println(ii)

    Fx {
      ii
    }
  }

}