//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[bracketCase](bracket-case.md)

# bracketCase

[common]\
inline suspend fun &lt;[A](bracket-case.md), [B](bracket-case.md)&gt; [bracketCase](bracket-case.md)(crossinline acquire: suspend () -&gt; [A](bracket-case.md), use: suspend ([A](bracket-case.md)) -&gt; [B](bracket-case.md), crossinline release: suspend ([A](bracket-case.md), [ExitCase](-exit-case/index.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [B](bracket-case.md)

A way to safely acquire a resource and release in the face of errors and cancellation. It uses [ExitCase](-exit-case/index.md) to distinguish between different exit cases when releasing the acquired resource.

[bracketCase](bracket-case.md) exists out of three stages:

<ol><li>acquisition</li><li>consumption</li><li>releasing</li><li>Resource acquisition is **NON CANCELLABLE**. If resource acquisition fails, meaning no resource was actually successfully acquired then we short-circuit the effect. As the resource was not acquired, it is not possible to [use](bracket-case.md) or [release](bracket-case.md) it. If it is successful we pass the result to stage 2 [use](bracket-case.md).</li><li>Resource consumption is like any other suspend effect. The key difference here is that it's wired in such a way that [release](bracket-case.md)**will always** be called either on [ExitCase.Cancelled](-exit-case/-cancelled/index.md), [ExitCase.Failure](-exit-case/-failure/index.md) or [ExitCase.Completed](-exit-case/-completed/index.md). If it failed, then the resulting [suspend](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/index.html) from [bracketCase](bracket-case.md) will be the error; otherwise the result of [use](bracket-case.md) will be returned.</li><li>Resource releasing is **NON CANCELLABLE**, otherwise it could result in leaks. In the case it throws an exception, the resulting [suspend](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/index.html) will be either such error, or a composed error if one occurred in the [use](bracket-case.md) stage.</li></ol>

## Parameters

common

| | |
|---|---|
| acquire | is the action to acquire the resource. |
| use | is the action to consume the resource and produce a result. Once the resulting suspend program terminates, either successfully, error or disposed, the [release](bracket-case.md) function will run to clean up the resources. |
| release | is the action to release the allocated resource after [use](bracket-case.md) terminates.<br>import arrow.fx.coroutines.*<br>class File(url: String) {<br>  fun open(): File = this<br>  fun close(): Unit {}<br>}<br>suspend fun File.content(): String =<br>    "This file contains some interesting content!"<br>suspend fun openFile(uri: String): File = File(uri).open()<br>suspend fun closeFile(file: File): Unit = file.close()<br>suspend fun main(): Unit {<br>  //sampleStart<br>  val res = bracketCase(<br>    acquire = { openFile("data.json") },<br>    use = { file -&gt; file.content() },<br>    release = { file, exitCase -&gt;<br>      when (exitCase) {<br>        is ExitCase.Completed -&gt; println("File closed with $exitCase")<br>        is ExitCase.Cancelled -&gt; println("Program cancelled with $exitCase")<br>        is ExitCase.Failure -&gt; println("Program failed with $exitCase")<br>      }<br>      closeFile(file)<br>    }<br>  )<br>  //sampleEnd<br>  println(res)<br>}<!--- KNIT example-bracket-02.kt --> |
