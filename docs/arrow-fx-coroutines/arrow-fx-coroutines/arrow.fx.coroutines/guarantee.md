//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[guarantee](guarantee.md)

# guarantee

[common]\
inline suspend fun &lt;[A](guarantee.md)&gt; [guarantee](guarantee.md)(fa: suspend () -&gt; [A](guarantee.md), crossinline finalizer: suspend () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [A](guarantee.md)

Guarantees execution of a given [finalizer](guarantee.md) after [fa](guarantee.md) regardless of success, error or cancellation.

As best practice, it's not a good idea to release resources via [guarantee](guarantee.md). since [guarantee](guarantee.md) doesn't properly model acquiring, using and releasing resources. It only models scheduling of a finalizer after a given suspending program, so you should prefer [Resource](-resource/index.md) or [bracket](bracket.md) which captures acquiring, using and releasing into 3 separate steps to ensure resource safety.

## See also

common

| | |
|---|---|
| [guaranteeCase](guarantee-case.md) | for registering a handler that tracks the [ExitCase](-exit-case/index.md) of [fa](guarantee.md). |

## Parameters

common

| | |
|---|---|
| fa | program that you want to register handler on |
| finalizer | handler to run after [fa](guarantee.md). |
