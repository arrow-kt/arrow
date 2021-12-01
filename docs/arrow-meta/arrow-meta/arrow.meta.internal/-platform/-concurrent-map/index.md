//[arrow-meta](../../../../index.md)/[arrow.meta.internal](../../index.md)/[Platform](../index.md)/[ConcurrentMap](index.md)

# ConcurrentMap

[jvm]\
interface [ConcurrentMap](index.md)&lt;[K](index.md), [V](index.md)&gt; : [MutableMap](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-map/index.html)&lt;[K](index.md), [V](index.md)&gt;

## Functions

| Name | Summary |
|---|---|
| [clear](index.md#1264776610%2FFunctions%2F-35121544) | [jvm]<br>abstract fun [clear](index.md#1264776610%2FFunctions%2F-35121544)() |
| [compute](index.md#-2125908806%2FFunctions%2F-35121544) | [jvm]<br>open fun [compute](index.md#-2125908806%2FFunctions%2F-35121544)(p0: [K](index.md), p1: [BiFunction](https://docs.oracle.com/javase/8/docs/api/java/util/function/BiFunction.html)&lt;in [K](index.md), in [V](index.md)?, out [V](index.md)?&gt;): [V](index.md)? |
| [computeIfAbsent](index.md#-2012194187%2FFunctions%2F-35121544) | [jvm]<br>open fun [computeIfAbsent](index.md#-2012194187%2FFunctions%2F-35121544)(p0: [K](index.md), p1: [Function](https://docs.oracle.com/javase/8/docs/api/java/util/function/Function.html)&lt;in [K](index.md), out [V](index.md)&gt;): [V](index.md) |
| [computeIfPresent](index.md#1357972273%2FFunctions%2F-35121544) | [jvm]<br>open fun [computeIfPresent](index.md#1357972273%2FFunctions%2F-35121544)(p0: [K](index.md), p1: [BiFunction](https://docs.oracle.com/javase/8/docs/api/java/util/function/BiFunction.html)&lt;in [K](index.md), in [V](index.md), out [V](index.md)?&gt;): [V](index.md)? |
| [containsKey](index.md#189495335%2FFunctions%2F-35121544) | [jvm]<br>abstract fun [containsKey](index.md#189495335%2FFunctions%2F-35121544)(key: [K](index.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [containsValue](index.md#-337993863%2FFunctions%2F-35121544) | [jvm]<br>abstract fun [containsValue](index.md#-337993863%2FFunctions%2F-35121544)(value: [V](index.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [forEach](index.md#1890068580%2FFunctions%2F-35121544) | [jvm]<br>open fun [forEach](index.md#1890068580%2FFunctions%2F-35121544)(p0: [BiConsumer](https://docs.oracle.com/javase/8/docs/api/java/util/function/BiConsumer.html)&lt;in [K](index.md), in [V](index.md)&gt;) |
| [get](index.md#1589144509%2FFunctions%2F-35121544) | [jvm]<br>abstract operator fun [get](index.md#1589144509%2FFunctions%2F-35121544)(key: [K](index.md)): [V](index.md)? |
| [getOrDefault](index.md#1493482850%2FFunctions%2F-35121544) | [jvm]<br>open fun [getOrDefault](index.md#1493482850%2FFunctions%2F-35121544)(key: [K](index.md), defaultValue: [V](index.md)): [V](index.md) |
| [isEmpty](index.md#-1708477740%2FFunctions%2F-35121544) | [jvm]<br>abstract fun [isEmpty](index.md#-1708477740%2FFunctions%2F-35121544)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [merge](index.md#1519727293%2FFunctions%2F-35121544) | [jvm]<br>open fun [merge](index.md#1519727293%2FFunctions%2F-35121544)(p0: [K](index.md), p1: [V](index.md), p2: [BiFunction](https://docs.oracle.com/javase/8/docs/api/java/util/function/BiFunction.html)&lt;in [V](index.md), in [V](index.md), out [V](index.md)?&gt;): [V](index.md)? |
| [put](index.md#1076499965%2FFunctions%2F-35121544) | [jvm]<br>abstract fun [put](index.md#1076499965%2FFunctions%2F-35121544)(key: [K](index.md), value: [V](index.md)): [V](index.md)? |
| [putAll](index.md#-1770992861%2FFunctions%2F-35121544) | [jvm]<br>abstract fun [putAll](index.md#-1770992861%2FFunctions%2F-35121544)(from: [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;out [K](index.md), [V](index.md)&gt;) |
| [putIfAbsent](index.md#-255529517%2FFunctions%2F-35121544) | [jvm]<br>open fun [putIfAbsent](index.md#-255529517%2FFunctions%2F-35121544)(p0: [K](index.md), p1: [V](index.md)): [V](index.md)? |
| [putSafely](put-safely.md) | [jvm]<br>abstract fun [putSafely](put-safely.md)(k: [K](index.md), v: [V](index.md)): [V](index.md) |
| [remove](index.md#-121413961%2FFunctions%2F-35121544) | [jvm]<br>abstract fun [remove](index.md#-121413961%2FFunctions%2F-35121544)(key: [K](index.md)): [V](index.md)?<br>open fun [remove](index.md#351754838%2FFunctions%2F-35121544)(key: [K](index.md), value: [V](index.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [replace](index.md#1894614946%2FFunctions%2F-35121544) | [jvm]<br>open fun [replace](index.md#1894614946%2FFunctions%2F-35121544)(p0: [K](index.md), p1: [V](index.md)): [V](index.md)?<br>open fun [replace](index.md#-1618274495%2FFunctions%2F-35121544)(p0: [K](index.md), p1: [V](index.md), p2: [V](index.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [replaceAll](index.md#-616367665%2FFunctions%2F-35121544) | [jvm]<br>open fun [replaceAll](index.md#-616367665%2FFunctions%2F-35121544)(p0: [BiFunction](https://docs.oracle.com/javase/8/docs/api/java/util/function/BiFunction.html)&lt;in [K](index.md), in [V](index.md), out [V](index.md)&gt;) |

## Properties

| Name | Summary |
|---|---|
| [entries](index.md#313986111%2FProperties%2F-35121544) | [jvm]<br>abstract override val [entries](index.md#313986111%2FProperties%2F-35121544): [MutableSet](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-set/index.html)&lt;[MutableMap.MutableEntry](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-map/-mutable-entry/index.html)&lt;[K](index.md), [V](index.md)&gt;&gt; |
| [keys](index.md#-1153773961%2FProperties%2F-35121544) | [jvm]<br>abstract override val [keys](index.md#-1153773961%2FProperties%2F-35121544): [MutableSet](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-set/index.html)&lt;[K](index.md)&gt; |
| [size](index.md#-157521630%2FProperties%2F-35121544) | [jvm]<br>abstract val [size](index.md#-157521630%2FProperties%2F-35121544): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [values](index.md#211311497%2FProperties%2F-35121544) | [jvm]<br>abstract override val [values](index.md#211311497%2FProperties%2F-35121544): [MutableCollection](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-collection/index.html)&lt;[V](index.md)&gt; |
