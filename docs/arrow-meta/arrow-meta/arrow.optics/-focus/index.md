//[arrow-meta](../../../index.md)/[arrow.optics](../index.md)/[Focus](index.md)

# Focus

[jvm]\
sealed class [Focus](index.md)

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [jvm]<br>object [Companion](-companion/index.md) |
| [NonNull](-non-null/index.md) | [jvm]<br>data class [NonNull](-non-null/index.md)(className: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), paramName: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) : [Focus](index.md) |
| [Nullable](-nullable/index.md) | [jvm]<br>data class [Nullable](-nullable/index.md)(className: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), paramName: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) : [Focus](index.md) |
| [Option](-option/index.md) | [jvm]<br>data class [Option](-option/index.md)(className: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), paramName: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) : [Focus](index.md) |

## Properties

| Name | Summary |
|---|---|
| [className](class-name.md) | [jvm]<br>abstract val [className](class-name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [paramName](param-name.md) | [jvm]<br>abstract val [paramName](param-name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

## Inheritors

| Name |
|---|
| [Focus](-nullable/index.md) |
| [Focus](-option/index.md) |
| [Focus](-non-null/index.md) |

## Extensions

| Name | Summary |
|---|---|
| [lensParamName](../lens-param-name.md) | [jvm]<br>fun [Focus](index.md).[lensParamName](../lens-param-name.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
