//[arrow-core](../../index.md)/[arrow.core](index.md)/[memoize](memoize.md)

# memoize

[common]\
fun &lt;[R](memoize.md)&gt; () -&gt; [R](memoize.md).[memoize](memoize.md)(): () -&gt; [R](memoize.md)

Memoizes the given **pure** function so that invocations with the same arguments will only execute the function once.

import arrow.core.memoize\
fun someWorkIntensiveFunction(someParam: Int): String = "$someParam"\
\
fun main() {\
  //sampleStart\
  val memoizedF = ::someWorkIntensiveFunction.memoize()\
\
  // The first invocation will store the argument and result in a cache inside the `memoizedF` reference.\
  val value1 = memoizedF(42)\
  // This second invocation won't really call the `someWorkIntensiveFunction` function\
  //but retrieve the result from the previous invocation instead.\
  val value2 = memoizedF(42)\
\
  //sampleEnd\
  println("$value1 $value2")\
}<!--- KNIT example-memoization-01.kt -->

Note that calling this function with the same parameters in parallel might cause the function to be executed twice.

[common]\
fun &lt;[P1](memoize.md), [R](memoize.md)&gt; ([P1](memoize.md)) -&gt; [R](memoize.md).[memoize](memoize.md)(): ([P1](memoize.md)) -&gt; [R](memoize.md)

fun &lt;[P1](memoize.md), [P2](memoize.md), [R](memoize.md)&gt; ([P1](memoize.md), [P2](memoize.md)) -&gt; [R](memoize.md).[memoize](memoize.md)(): ([P1](memoize.md), [P2](memoize.md)) -&gt; [R](memoize.md)

fun &lt;[P1](memoize.md), [P2](memoize.md), [P3](memoize.md), [R](memoize.md)&gt; ([P1](memoize.md), [P2](memoize.md), [P3](memoize.md)) -&gt; [R](memoize.md).[memoize](memoize.md)(): ([P1](memoize.md), [P2](memoize.md), [P3](memoize.md)) -&gt; [R](memoize.md)

fun &lt;[P1](memoize.md), [P2](memoize.md), [P3](memoize.md), [P4](memoize.md), [R](memoize.md)&gt; ([P1](memoize.md), [P2](memoize.md), [P3](memoize.md), [P4](memoize.md)) -&gt; [R](memoize.md).[memoize](memoize.md)(): ([P1](memoize.md), [P2](memoize.md), [P3](memoize.md), [P4](memoize.md)) -&gt; [R](memoize.md)

fun &lt;[P1](memoize.md), [P2](memoize.md), [P3](memoize.md), [P4](memoize.md), [P5](memoize.md), [R](memoize.md)&gt; ([P1](memoize.md), [P2](memoize.md), [P3](memoize.md), [P4](memoize.md), [P5](memoize.md)) -&gt; [R](memoize.md).[memoize](memoize.md)(): ([P1](memoize.md), [P2](memoize.md), [P3](memoize.md), [P4](memoize.md), [P5](memoize.md)) -&gt; [R](memoize.md)

## See also

common

| | |
|---|---|
| [memoize](memoize.md) |  |
