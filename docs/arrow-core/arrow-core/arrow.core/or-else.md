//[arrow-core](../../index.md)/[arrow.core](index.md)/[orElse](or-else.md)

# orElse

[common]\
inline fun &lt;[A](or-else.md)&gt; [Option](-option/index.md)&lt;[A](or-else.md)&gt;.[orElse](or-else.md)(alternative: () -&gt; [Option](-option/index.md)&lt;[A](or-else.md)&gt;): [Option](-option/index.md)&lt;[A](or-else.md)&gt;

Returns this option's if the option is nonempty, otherwise returns another option provided lazily by default.

## Parameters

common

| | |
|---|---|
| alternative | the default option if this is empty. |

[common]\
inline fun &lt;[E](or-else.md), [A](or-else.md)&gt; [Validated](-validated/index.md)&lt;[E](or-else.md), [A](or-else.md)&gt;.[orElse](or-else.md)(default: () -&gt; [Validated](-validated/index.md)&lt;[E](or-else.md), [A](or-else.md)&gt;): [Validated](-validated/index.md)&lt;[E](or-else.md), [A](or-else.md)&gt;

Return this if it is Valid, or else fall back to the given default. The functionality is similar to that of [findValid](find-valid.md) except for failure accumulation, where here only the error on the right is preserved and the error on the left is ignored.
