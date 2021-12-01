//[arrow-meta](../../../../index.md)/[arrow.meta.ast](../../index.md)/[TypeName](../index.md)/[TypeVariable](index.md)

# TypeVariable

[jvm]\
data class [TypeVariable](index.md)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), bounds: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[TypeName](../index.md)&gt;, variance: [Modifier](../../-modifier/index.md)?, reified: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), nullable: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), annotations: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Annotation](../../-annotation/index.md)&gt;) : [TypeName](../index.md)

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [jvm]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [annotations](annotations.md) | [jvm]<br>val [annotations](annotations.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Annotation](../../-annotation/index.md)&gt; |
| [bounds](bounds.md) | [jvm]<br>val [bounds](bounds.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[TypeName](../index.md)&gt; |
| [name](name.md) | [jvm]<br>val [name](name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [nullable](nullable.md) | [jvm]<br>val [nullable](nullable.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false |
| [rawName](raw-name.md) | [jvm]<br>open override val [rawName](raw-name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [reified](reified.md) | [jvm]<br>val [reified](reified.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false |
| [simpleName](simple-name.md) | [jvm]<br>open override val [simpleName](simple-name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [variance](variance.md) | [jvm]<br>val [variance](variance.md): [Modifier](../../-modifier/index.md)? = null |
