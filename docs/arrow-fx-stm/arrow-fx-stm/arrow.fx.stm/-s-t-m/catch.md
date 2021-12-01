//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[catch](catch.md)

# catch

[common]\
abstract fun &lt;[A](catch.md)&gt; [catch](catch.md)(f: [STM](index.md).() -&gt; [A](catch.md), onError: [STM](index.md).([Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)) -&gt; [A](catch.md)): [A](catch.md)

Run [f](catch.md) and handle any exception thrown with [onError](catch.md).

import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val result = atomically {\
    catch({ throw Throwable() }) { e -&gt; "caught" }\
  }\
  //sampleEnd\
  println("Result $result")\
}<!--- KNIT example-stm-06.kt -->
