//[arrow-continuations](../../index.md)/[arrow.continuations.generic](index.md)/[loop](loop.md)

# loop

[common]\
inline fun &lt;[V](loop.md)&gt; [AtomicRef](-atomic-ref/index.md)&lt;[V](loop.md)&gt;.[loop](loop.md)(action: ([V](loop.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html)

Infinite loop that reads this atomic variable and performs the specified [action](loop.md) on its value.
