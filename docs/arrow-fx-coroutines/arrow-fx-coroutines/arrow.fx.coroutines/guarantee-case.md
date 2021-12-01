//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[guaranteeCase](guarantee-case.md)

# guaranteeCase

[common]\
inline suspend fun &lt;[A](guarantee-case.md)&gt; [guaranteeCase](guarantee-case.md)(fa: suspend () -&gt; [A](guarantee-case.md), crossinline finalizer: suspend ([ExitCase](-exit-case/index.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [A](guarantee-case.md)

Guarantees execution of a given [finalizer](guarantee-case.md) after [fa](guarantee-case.md) regardless of success, error or cancellation, allowing for differentiating between exit conditions with the [ExitCase](-exit-case/index.md) argument of the finalizer.

As best practice, it's not a good idea to release resources via [guaranteeCase](guarantee-case.md). since [guaranteeCase](guarantee-case.md) doesn't properly model acquiring, using and releasing resources. It only models scheduling of a finalizer after a given suspending program, so you should prefer [Resource](-resource/index.md) or [bracketCase](bracket-case.md) which captures acquiring, using and releasing into 3 separate steps to ensure resource safety.

## See also

common

| | |
|---|---|
| [guarantee](guarantee.md) | for registering a handler that ignores the [ExitCase](-exit-case/index.md) of [fa](guarantee-case.md). |

## Parameters

common

| | |
|---|---|
| fa | program that you want to register handler on |
| finalizer | handler to run after [fa](guarantee-case.md). |
