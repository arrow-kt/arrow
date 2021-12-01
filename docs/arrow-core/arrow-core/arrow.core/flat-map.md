//[arrow-core](../../index.md)/[arrow.core](index.md)/[flatMap](flat-map.md)

# flatMap

[common]\
inline fun &lt;[A](flat-map.md), [B](flat-map.md), [C](flat-map.md)&gt; [Either](-either/index.md)&lt;[A](flat-map.md), [B](flat-map.md)&gt;.[flatMap](flat-map.md)(f: ([B](flat-map.md)) -&gt; [Either](-either/index.md)&lt;[A](flat-map.md), [C](flat-map.md)&gt;): [Either](-either/index.md)&lt;[A](flat-map.md), [C](flat-map.md)&gt;

Binds the given function across [Right](-either/-right/index.md).

## Parameters

common

| | |
|---|---|
| f | The function to bind across [Right](-either/-right/index.md). |

[common]\
inline fun &lt;[A](flat-map.md), [B](flat-map.md), [D](flat-map.md)&gt; [Ior](-ior/index.md)&lt;[A](flat-map.md), [B](flat-map.md)&gt;.[flatMap](flat-map.md)(SG: [Semigroup](../arrow.typeclasses/-semigroup/index.md)&lt;[A](flat-map.md)&gt;, f: ([B](flat-map.md)) -&gt; [Ior](-ior/index.md)&lt;[A](flat-map.md), [D](flat-map.md)&gt;): [Ior](-ior/index.md)&lt;[A](flat-map.md), [D](flat-map.md)&gt;

Binds the given function across [Ior.Right](-ior/-right/index.md).

## Parameters

common

| | |
|---|---|
| f | The function to bind across [Ior.Right](-ior/-right/index.md). |

[common]\
inline fun &lt;[A](flat-map.md), [B](flat-map.md)&gt; [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[A](flat-map.md)&gt;.[flatMap](flat-map.md)(transform: ([A](flat-map.md)) -&gt; [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[B](flat-map.md)&gt;): [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[B](flat-map.md)&gt;

Compose a [transform](flat-map.md) operation on the success value [A](flat-map.md) into [B](flat-map.md) whilst flattening [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html).

## See also

common

| | |
|---|---|
| [mapCatching](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/index.html) | if you want run a function that catches and maps with (A) -&gt; B |

[common]\
fun &lt;[K](flat-map.md), [A](flat-map.md), [B](flat-map.md)&gt; [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](flat-map.md), [A](flat-map.md)&gt;.[flatMap](flat-map.md)(f: ([Map.Entry](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/-entry/index.html)&lt;[K](flat-map.md), [A](flat-map.md)&gt;) -&gt; [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](flat-map.md), [B](flat-map.md)&gt;): [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](flat-map.md), [B](flat-map.md)&gt;
