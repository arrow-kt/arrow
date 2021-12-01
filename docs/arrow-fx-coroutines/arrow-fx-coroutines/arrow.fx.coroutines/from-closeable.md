//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[fromCloseable](from-closeable.md)

# fromCloseable

[jvm]\
fun &lt;[A](from-closeable.md) : [Closeable](https://docs.oracle.com/javase/8/docs/api/java/io/Closeable.html)&gt; [Resource.Companion](-resource/-companion/index.md#-1559173624%2FExtensions%2F1399459356).[fromCloseable](from-closeable.md)(f: suspend () -&gt; [A](from-closeable.md)): [Resource](../../../arrow-fx-coroutines/arrow-fx-coroutines/arrow.fx.coroutines/-resource/index.md)&lt;[A](from-closeable.md)&gt;

Creates a [Resource](../../../arrow-fx-coroutines/arrow-fx-coroutines/arrow.fx.coroutines/-resource/index.md) from an [Closeable](https://docs.oracle.com/javase/8/docs/api/java/io/Closeable.html), which uses [Closeable.close](https://docs.oracle.com/javase/8/docs/api/java/io/Closeable.html#close--) for releasing.

import arrow.fx.coroutines.*\
import java.io.FileInputStream\
\
suspend fun copyFile(src: String, dest: String): Unit =\
  Resource.fromCloseable { FileInputStream(src) }\
    .zip(Resource.fromCloseable { FileInputStream(dest) })\
    .use { (a: FileInputStream, b: FileInputStream) -&gt;\
       /** read from [a] and write to [b]. **/\
       // Both resources will be closed accordingly to their #close methods\
    }<!--- KNIT example-resourceextensions-02.kt -->
