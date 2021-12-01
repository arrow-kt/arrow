//[arrow-meta](../../../../index.md)/[arrow.meta.ast](../../index.md)/[TypeName](../index.md)/[ParameterizedType](index.md)

# ParameterizedType

[jvm]\
data class [ParameterizedType](index.md)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), enclosingType: [TypeName](../index.md)?, rawType: [TypeName.Classy](../-classy/index.md), typeArguments: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[TypeName](../index.md)&gt;, nullable: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), annotations: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Annotation](../../-annotation/index.md)&gt;) : [TypeName](../index.md)

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [jvm]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [annotations](annotations.md) | [jvm]<br>val [annotations](annotations.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Annotation](../../-annotation/index.md)&gt; |
| [enclosingType](enclosing-type.md) | [jvm]<br>val [enclosingType](enclosing-type.md): [TypeName](../index.md)? = null |
| [name](name.md) | [jvm]<br>val [name](name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [nullable](nullable.md) | [jvm]<br>val [nullable](nullable.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false |
| [rawName](raw-name.md) | [jvm]<br>open override val [rawName](raw-name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [rawType](raw-type.md) | [jvm]<br>val [rawType](raw-type.md): [TypeName.Classy](../-classy/index.md) |
| [simpleName](simple-name.md) | [jvm]<br>open override val [simpleName](simple-name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [typeArguments](type-arguments.md) | [jvm]<br>val [typeArguments](type-arguments.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[TypeName](../index.md)&gt; |
