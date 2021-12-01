//[arrow-core](../../../index.md)/[arrow.core.computations](../index.md)/[EitherEffect](index.md)/[bind](bind.md)

# bind

[common]\
open suspend fun &lt;[B](bind.md)&gt; [Either](../../arrow.core/-either/index.md)&lt;[E](index.md), [B](bind.md)&gt;.[bind](bind.md)(): [B](bind.md)

open suspend fun &lt;[B](bind.md)&gt; [Validated](../../arrow.core/-validated/index.md)&lt;[E](index.md), [B](bind.md)&gt;.[bind](bind.md)(): [B](bind.md)

open suspend fun &lt;[B](bind.md)&gt; [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[B](bind.md)&gt;.[bind](bind.md)(transform: ([Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)) -&gt; [E](index.md)): [B](bind.md)
