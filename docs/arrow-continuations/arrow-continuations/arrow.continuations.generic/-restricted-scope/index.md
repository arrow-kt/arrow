//[arrow-continuations](../../../index.md)/[arrow.continuations.generic](../index.md)/[RestrictedScope](index.md)

# RestrictedScope

[common]\
interface [RestrictedScope](index.md)&lt;[R](index.md)&gt; : [DelimitedScope](../-delimited-scope/index.md)&lt;[R](index.md)&gt;

## Functions

| Name | Summary |
|---|---|
| [shift](shift.md) | [common]<br>open suspend override fun &lt;[A](shift.md)&gt; [shift](shift.md)(r: [R](index.md)): [A](shift.md)<br>Exit the [DelimitedScope](../-delimited-scope/index.md) with [R](index.md)<br>[common]<br>abstract suspend fun &lt;[A](shift.md)&gt; [shift](shift.md)(f: suspend [RestrictedScope](index.md)&lt;[R](index.md)&gt;.([DelimitedContinuation](../-delimited-continuation/index.md)&lt;[A](shift.md), [R](index.md)&gt;) -&gt; [R](index.md)): [A](shift.md)<br>Capture the continuation and pass it to [f](shift.md). |
