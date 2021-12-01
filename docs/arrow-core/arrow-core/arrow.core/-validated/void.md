//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Validated](index.md)/[void](void.md)

# void

[common]\
fun [void](void.md)(): [Validated](index.md)&lt;[E](index.md), [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)&gt;

Discards the [A](index.md) value inside [Validated](index.md) signaling this container may be pointing to a noop or an effect whose return value is deliberately ignored. The singleton value [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) serves as signal.

import arrow.core.*\
\
fun main(args: Array&lt;String&gt;) {\
  val result =\
  //sampleStart\
  "Hello World".valid().void()\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-validated-17.kt -->
