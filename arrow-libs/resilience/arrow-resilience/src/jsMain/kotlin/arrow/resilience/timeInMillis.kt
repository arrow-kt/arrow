package arrow.resilience

@Deprecated("This is being removed in Arrow Fx Resilience and kotlin.time.TimeSource is the recommended replacement.")
public actual fun timeInMillis(): Long {
  val x: Number = js("new Date().getTime()")
  return x.toLong()
}
