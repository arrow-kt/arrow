//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[ExitCase](index.md)

# ExitCase

[common]\
sealed class [ExitCase](index.md)

## Types

| Name | Summary |
|---|---|
| [Cancelled](-cancelled/index.md) | [common]<br>data class [Cancelled](-cancelled/index.md)(exception: CancellationException) : [ExitCase](index.md) |
| [Completed](-completed/index.md) | [common]<br>object [Completed](-completed/index.md) : [ExitCase](index.md) |
| [Failure](-failure/index.md) | [common]<br>data class [Failure](-failure/index.md)(failure: [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)) : [ExitCase](index.md) |

## Inheritors

| Name |
|---|
| [ExitCase](-completed/index.md) |
| [ExitCase](-cancelled/index.md) |
| [ExitCase](-failure/index.md) |
