//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[onCancel](on-cancel.md)

# onCancel

[common]\
inline suspend fun &lt;[A](on-cancel.md)&gt; [onCancel](on-cancel.md)(fa: suspend () -&gt; [A](on-cancel.md), crossinline onCancel: suspend () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [A](on-cancel.md)

Registers an [onCancel](on-cancel.md) handler after [fa](on-cancel.md). [onCancel](on-cancel.md) is guaranteed to be called in case of cancellation, otherwise it's ignored.

This function is useful for wiring cancellation tokens between fibers, building inter-op with other effect systems or testing.

## See also

common

| | |
|---|---|
| [guarantee](guarantee.md) | for registering a handler that is guaranteed to always run. |
| [guaranteeCase](guarantee-case.md) | for registering a handler that executes for any [ExitCase](-exit-case/index.md). |

## Parameters

common

| | |
|---|---|
| fa | program that you want to register handler on |
| onCancel | handler to run when [fa](on-cancel.md) gets cancelled. |
