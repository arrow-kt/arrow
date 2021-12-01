//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Either](index.md)/[fold](fold.md)

# fold

[common]\
inline fun &lt;[C](fold.md)&gt; [fold](fold.md)(ifLeft: ([A](index.md)) -&gt; [C](fold.md), ifRight: ([B](index.md)) -&gt; [C](fold.md)): [C](fold.md)

Applies ifLeft if this is a [Left](-left/index.md) or ifRight if this is a [Right](-right/index.md).

Example:

&lt;!--- KNIT example-either-34.kt --&gt;\
val result: Either&lt;Exception, Value&gt; = possiblyFailingOperation()\
result.fold(\
     { log("operation failed with $it") },\
     { log("operation succeeded with $it") }\
)<!--- KNIT example-either-35.kt -->

#### Return

the results of applying the function

## Parameters

common

| | |
|---|---|
| ifLeft | the function to apply if this is a [Left](-left/index.md) |
| ifRight | the function to apply if this is a [Right](-right/index.md) |
