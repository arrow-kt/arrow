//[arrow-meta](../../../index.md)/[arrow.meta.ast](../index.md)/[TypeName](index.md)

# TypeName

[jvm]\
sealed class [TypeName](index.md) : [Tree](../-tree/index.md)

## Types

| Name | Summary |
|---|---|
| [Classy](-classy/index.md) | [jvm]<br>data class [Classy](-classy/index.md)(simpleName: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), fqName: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), pckg: [PackageName](../-package-name/index.md), nullable: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), annotations: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Annotation](../-annotation/index.md)&gt;) : [TypeName](index.md) |
| [Companion](-companion/index.md) | [jvm]<br>object [Companion](-companion/index.md) |
| [FunctionLiteral](-function-literal/index.md) | [jvm]<br>data class [FunctionLiteral](-function-literal/index.md)(modifiers: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Modifier](../-modifier/index.md)&gt;, receiverType: [TypeName](index.md)?, parameters: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[TypeName](index.md)&gt;, returnType: [TypeName](index.md)) : [TypeName](index.md) |
| [ParameterizedType](-parameterized-type/index.md) | [jvm]<br>data class [ParameterizedType](-parameterized-type/index.md)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), enclosingType: [TypeName](index.md)?, rawType: [TypeName.Classy](-classy/index.md), typeArguments: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[TypeName](index.md)&gt;, nullable: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), annotations: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Annotation](../-annotation/index.md)&gt;) : [TypeName](index.md) |
| [TypeVariable](-type-variable/index.md) | [jvm]<br>data class [TypeVariable](-type-variable/index.md)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), bounds: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[TypeName](index.md)&gt;, variance: [Modifier](../-modifier/index.md)?, reified: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), nullable: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), annotations: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Annotation](../-annotation/index.md)&gt;) : [TypeName](index.md) |
| [WildcardType](-wildcard-type/index.md) | [jvm]<br>data class [WildcardType](-wildcard-type/index.md)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), upperBounds: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[TypeName](index.md)&gt;, lowerBounds: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[TypeName](index.md)&gt;, nullable: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), annotations: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Annotation](../-annotation/index.md)&gt;) : [TypeName](index.md) |

## Properties

| Name | Summary |
|---|---|
| [rawName](raw-name.md) | [jvm]<br>abstract val [rawName](raw-name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [simpleName](simple-name.md) | [jvm]<br>abstract val [simpleName](simple-name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

## Inheritors

| Name |
|---|
| [TypeName](-type-variable/index.md) |
| [TypeName](-wildcard-type/index.md) |
| [TypeName](-function-literal/index.md) |
| [TypeName](-parameterized-type/index.md) |
| [TypeName](-classy/index.md) |
