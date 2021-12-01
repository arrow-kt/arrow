//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[fromAutoCloseable](from-auto-closeable.md)

# fromAutoCloseable

[jvm]\
fun &lt;[A](from-auto-closeable.md) : [AutoCloseable](https://docs.oracle.com/javase/8/docs/api/java/lang/AutoCloseable.html)&gt; [Resource.Companion](-resource/-companion/index.md#-1559173624%2FExtensions%2F1399459356).[fromAutoCloseable](from-auto-closeable.md)(f: suspend () -&gt; [A](from-auto-closeable.md)): [Resource](../../../arrow-fx-coroutines/arrow-fx-coroutines/arrow.fx.coroutines/-resource/index.md)&lt;[A](from-auto-closeable.md)&gt;

Creates a [Resource](../../../arrow-fx-coroutines/arrow-fx-coroutines/arrow.fx.coroutines/-resource/index.md) from an [AutoCloseable](https://docs.oracle.com/javase/8/docs/api/java/lang/AutoCloseable.html), which uses [AutoCloseable.close](https://docs.oracle.com/javase/8/docs/api/java/lang/AutoCloseable.html#close--) for releasing.

import arrow.fx.coroutines.*\
import java.io.FileInputStream\
\
suspend fun copyFile(src: String, dest: String): Unit =\
  Resource.fromAutoCloseable { FileInputStream(src) }\
    .zip(Resource.fromAutoCloseable { FileInputStream(dest) })\
    .use { (a: FileInputStream, b: FileInputStream) -&gt;\
       /** read from [a] and write to [b]. **/\
       // Both resources will be closed accordingly to their #close methods\
    }<!--- KNIT example-resourceextensions-03.kt -->
