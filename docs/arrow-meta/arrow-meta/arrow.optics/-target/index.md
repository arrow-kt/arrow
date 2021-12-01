//[arrow-meta](../../../index.md)/[arrow.optics](../index.md)/[Target](index.md)

# Target

[jvm]\
sealed class [Target](index.md)

## Types

| Name | Summary |
|---|---|
| [DataClassDsl](-data-class-dsl/index.md) | [jvm]<br>data class [DataClassDsl](-data-class-dsl/index.md)(foci: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Focus](../-focus/index.md)&gt;) : [Target](index.md) |
| [Iso](-iso/index.md) | [jvm]<br>data class [Iso](-iso/index.md)(foci: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Focus](../-focus/index.md)&gt;) : [Target](index.md) |
| [Lens](-lens/index.md) | [jvm]<br>data class [Lens](-lens/index.md)(foci: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Focus](../-focus/index.md)&gt;) : [Target](index.md) |
| [Optional](-optional/index.md) | [jvm]<br>data class [Optional](-optional/index.md)(foci: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Focus](../-focus/index.md)&gt;) : [Target](index.md) |
| [Prism](-prism/index.md) | [jvm]<br>data class [Prism](-prism/index.md)(foci: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Focus](../-focus/index.md)&gt;) : [Target](index.md) |
| [SealedClassDsl](-sealed-class-dsl/index.md) | [jvm]<br>data class [SealedClassDsl](-sealed-class-dsl/index.md)(foci: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Focus](../-focus/index.md)&gt;) : [Target](index.md) |

## Properties

| Name | Summary |
|---|---|
| [foci](foci.md) | [jvm]<br>abstract val [foci](foci.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Focus](../-focus/index.md)&gt; |

## Inheritors

| Name |
|---|
| [Target](-iso/index.md) |
| [Target](-prism/index.md) |
| [Target](-lens/index.md) |
| [Target](-optional/index.md) |
| [Target](-sealed-class-dsl/index.md) |
| [Target](-data-class-dsl/index.md) |

## Extensions

| Name | Summary |
|---|---|
| [targetNames](../target-names.md) | [jvm]<br>val [Target](index.md).[targetNames](../target-names.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)&gt; |
