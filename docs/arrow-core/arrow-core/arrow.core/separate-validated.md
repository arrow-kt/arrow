//[arrow-core](../../index.md)/[arrow.core](index.md)/[separateValidated](separate-validated.md)

# separateValidated

[common]\
fun &lt;[A](separate-validated.md), [B](separate-validated.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[Validated](-validated/index.md)&lt;[A](separate-validated.md), [B](separate-validated.md)&gt;&gt;.[separateValidated](separate-validated.md)(): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](separate-validated.md)&gt;, [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](separate-validated.md)&gt;&gt;

Separate the inner [Validated](-validated/index.md) values into the [Validated.Invalid](-validated/-invalid/index.md) and [Validated.Valid](-validated/-valid/index.md).

#### Receiver

Iterable of Validated

#### Return

a tuple containing List with [Validated.Invalid](-validated/-invalid/index.md) and another List with its [Validated.Valid](-validated/-valid/index.md) values.

[common]\
fun &lt;[A](separate-validated.md), [B](separate-validated.md)&gt; [Option](-option/index.md)&lt;[Validated](-validated/index.md)&lt;[A](separate-validated.md), [B](separate-validated.md)&gt;&gt;.[separateValidated](separate-validated.md)(): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Option](-option/index.md)&lt;[A](separate-validated.md)&gt;, [Option](-option/index.md)&lt;[B](separate-validated.md)&gt;&gt;

Separate the inner [Validated](-validated/index.md) value into the [Validated.Invalid](-validated/-invalid/index.md) and [Validated.Valid](-validated/-valid/index.md).

#### Receiver

Option of Either

#### Return

a tuple containing Option of [Validated.Invalid](-validated/-invalid/index.md) and another Option of its [Validated.Valid](-validated/-valid/index.md) value.

[common]\
fun &lt;[A](separate-validated.md), [B](separate-validated.md)&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[Validated](-validated/index.md)&lt;[A](separate-validated.md), [B](separate-validated.md)&gt;&gt;.[separateValidated](separate-validated.md)(): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](separate-validated.md)&gt;, [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[B](separate-validated.md)&gt;&gt;

Separate the inner [Validated](-validated/index.md) values into the [Validated.Invalid](-validated/-invalid/index.md) and [Validated.Valid](-validated/-valid/index.md).

#### Receiver

Iterable of Validated

#### Return

a tuple containing Sequence with [Validated.Invalid](-validated/-invalid/index.md) and another Sequence with its [Validated.Valid](-validated/-valid/index.md) values.
