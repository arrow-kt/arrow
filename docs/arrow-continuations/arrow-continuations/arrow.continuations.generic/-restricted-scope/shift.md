//[arrow-continuations](../../../index.md)/[arrow.continuations.generic](../index.md)/[RestrictedScope](index.md)/[shift](shift.md)

# shift

[common]\
abstract suspend fun &lt;[A](shift.md)&gt; [shift](shift.md)(f: suspend [RestrictedScope](index.md)&lt;[R](index.md)&gt;.([DelimitedContinuation](../-delimited-continuation/index.md)&lt;[A](shift.md), [R](index.md)&gt;) -&gt; [R](index.md)): [A](shift.md)

Capture the continuation and pass it to [f](shift.md).

[common]\
open suspend override fun &lt;[A](shift.md)&gt; [shift](shift.md)(r: [R](index.md)): [A](shift.md)

Exit the [DelimitedScope](../-delimited-scope/index.md) with [R](index.md)
