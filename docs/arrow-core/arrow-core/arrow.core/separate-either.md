//[arrow-core](../../index.md)/[arrow.core](index.md)/[separateEither](separate-either.md)

# separateEither

[common]\
fun &lt;[A](separate-either.md), [B](separate-either.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[Either](-either/index.md)&lt;[A](separate-either.md), [B](separate-either.md)&gt;&gt;.[separateEither](separate-either.md)(): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](separate-either.md)&gt;, [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](separate-either.md)&gt;&gt;

Separate the inner [Either](-either/index.md) values into the [Either.Left](-either/-left/index.md) and [Either.Right](-either/-right/index.md).

#### Receiver

Iterable of Validated

#### Return

a tuple containing List with [Either.Left](-either/-left/index.md) and another List with its [Either.Right](-either/-right/index.md) values.

[common]\
fun &lt;[A](separate-either.md), [B](separate-either.md)&gt; [Option](-option/index.md)&lt;[Either](-either/index.md)&lt;[A](separate-either.md), [B](separate-either.md)&gt;&gt;.[separateEither](separate-either.md)(): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Option](-option/index.md)&lt;[A](separate-either.md)&gt;, [Option](-option/index.md)&lt;[B](separate-either.md)&gt;&gt;

Separate the inner [Either](-either/index.md) value into the [Either.Left](-either/-left/index.md) and [Either.Right](-either/-right/index.md).

#### Receiver

Option of Either

#### Return

a tuple containing Option of [Either.Left](-either/-left/index.md) and another Option of its [Either.Right](-either/-right/index.md) value.

[common]\
fun &lt;[A](separate-either.md), [B](separate-either.md)&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[Either](-either/index.md)&lt;[A](separate-either.md), [B](separate-either.md)&gt;&gt;.[separateEither](separate-either.md)(): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](separate-either.md)&gt;, [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[B](separate-either.md)&gt;&gt;

Separate the inner [Either](-either/index.md) values into the [Either.Left](-either/-left/index.md) and [Either.Right](-either/-right/index.md).

#### Receiver

Iterable of Validated

#### Return

a tuple containing Sequence with [Either.Left](-either/-left/index.md) and another Sequence with its [Either.Right](-either/-right/index.md) values.
