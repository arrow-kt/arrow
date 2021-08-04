package arrow.fx.coroutines

/*
 * For JS:
 *  ```
 *    val x: Number = js("new Date().getTime()")
 *    return x.toLong()
 *  ```
 *
 * For Native:
 *   - `getTimeMillis()`
 *   - https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.system/get-time-millis.html
 */
public actual fun timeInMillis(): Long =
  System.currentTimeMillis()
