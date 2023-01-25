package arrow.fx.resilience

public actual fun timeInMillis(): Long {
  val x: Number = js("new Date().getTime()")
  return x.toLong()
}
