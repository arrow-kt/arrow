//[arrow-continuations](../../../index.md)/[arrow.continuations.generic](../index.md)/[DelimitedScope](index.md)

# DelimitedScope

[common]\
interface [DelimitedScope](index.md)&lt;[R](index.md)&gt;

Base interface for our scope.

## Functions

| Name | Summary |
|---|---|
| [shift](shift.md) | [common]<br>abstract suspend fun &lt;[A](shift.md)&gt; [shift](shift.md)(r: [R](index.md)): [A](shift.md)<br>Exit the [DelimitedScope](index.md) with [R](index.md) |

## Inheritors

| Name |
|---|
| [RestrictedScope](../-restricted-scope/index.md) |
| [SuspendedScope](../-suspended-scope/index.md) |
