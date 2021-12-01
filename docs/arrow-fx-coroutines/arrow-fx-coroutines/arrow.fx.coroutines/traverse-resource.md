//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[traverseResource](traverse-resource.md)

# traverseResource

[common]\
inline fun &lt;[A](traverse-resource.md), [B](traverse-resource.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](traverse-resource.md)&gt;.[traverseResource](traverse-resource.md)(crossinline f: ([A](traverse-resource.md)) -&gt; [Resource](-resource/index.md)&lt;[B](traverse-resource.md)&gt;): [Resource](-resource/index.md)&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](traverse-resource.md)&gt;&gt;

Traverse this [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html) and collects the resulting Resource&lt;B&gt; of [f](traverse-resource.md) into a Resource&lt;List&lt;B&gt;&gt;.

import arrow.fx.coroutines.*\
\
class File(url: String) {\
  suspend fun open(): File = this\
  suspend fun close(): Unit {}\
  override fun toString(): String = "This file contains some interesting content!"\
}\
\
suspend fun openFile(uri: String): File = File(uri).open()\
suspend fun closeFile(file: File): Unit = file.close()\
suspend fun fileToString(file: File): String = file.toString()\
\
suspend fun main(): Unit {\
  //sampleStart\
  val res: List&lt;String&gt; = listOf(\
    "data.json",\
    "user.json",\
    "resource.json"\
  ).traverseResource { uri -&gt;\
    resource {\
     openFile(uri)\
    } release { file -&gt;\
      closeFile(file)\
    }\
  }.use { files -&gt;\
    files.map { fileToString(it) }\
  }\
  //sampleEnd\
  res.forEach(::println)\
}<!--- KNIT example-resource-10.kt -->
