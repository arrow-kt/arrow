//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Atomic](index.md)/[modifyGet](modify-get.md)

# modifyGet

[common]\
abstract suspend fun &lt;[B](modify-get.md)&gt; [modifyGet](modify-get.md)(f: ([A](index.md)) -&gt; [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md), [B](modify-get.md)&gt;): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md), [B](modify-get.md)&gt;

ModifyGet allows to inspect state [A](index.md), update it and extract a different state [B](modify-get.md). In contrast to [modify](modify.md), it returns a [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html) of the updated state [A](index.md) and the extracted state [B](modify-get.md).

## See also

common

| | |
|---|---|
| [arrow.fx.coroutines.Atomic](modify.md) | for an example |
