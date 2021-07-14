package arrow.fx.coroutines

public actual fun timeInMillis(): Long {
  val x: Number = js("new Date().getTime()")
  return x.toLong()
}
