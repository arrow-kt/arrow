//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[releaseCase](release-case.md)

# releaseCase

[common]\
infix fun &lt;[A](release-case.md)&gt; [Resource](-resource/index.md)&lt;[A](release-case.md)&gt;.[releaseCase](release-case.md)(release: suspend ([A](release-case.md), [ExitCase](-exit-case/index.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Resource](-resource/index.md)&lt;[A](release-case.md)&gt;

Composes a [releaseCase](../../../arrow-fx-coroutines/arrow.fx.coroutines/index.md) action to a [Resource.use](-resource/use.md) action creating a [Resource](-resource/index.md).
