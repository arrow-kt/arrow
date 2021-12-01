//[arrow-meta](../../../index.md)/[arrow.meta.ast](../index.md)/[Property](index.md)

# Property

[jvm]\
data class [Property](index.md)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), type: [TypeName](../-type-name/index.md), mutable: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), kdoc: [Code](../-code/index.md)?, initializer: [Code](../-code/index.md)?, delegated: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), getter: [Func](../-func/index.md)?, setter: [Func](../-func/index.md)?, receiverType: [TypeName](../-type-name/index.md)?, jvmPropertySignature: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?, jvmFieldSignature: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?, annotations: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Annotation](../-annotation/index.md)&gt;, modifiers: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Modifier](../-modifier/index.md)&gt;) : [Tree](../-tree/index.md)

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [jvm]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [annotations](annotations.md) | [jvm]<br>val [annotations](annotations.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Annotation](../-annotation/index.md)&gt; |
| [delegated](delegated.md) | [jvm]<br>val [delegated](delegated.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false |
| [getter](getter.md) | [jvm]<br>val [getter](getter.md): [Func](../-func/index.md)? = null |
| [initializer](initializer.md) | [jvm]<br>val [initializer](initializer.md): [Code](../-code/index.md)? = null |
| [jvmFieldSignature](jvm-field-signature.md) | [jvm]<br>val [jvmFieldSignature](jvm-field-signature.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? = null |
| [jvmPropertySignature](jvm-property-signature.md) | [jvm]<br>val [jvmPropertySignature](jvm-property-signature.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? = null |
| [kdoc](kdoc.md) | [jvm]<br>val [kdoc](kdoc.md): [Code](../-code/index.md)? = null |
| [modifiers](modifiers.md) | [jvm]<br>val [modifiers](modifiers.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Modifier](../-modifier/index.md)&gt; |
| [mutable](mutable.md) | [jvm]<br>val [mutable](mutable.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false |
| [name](name.md) | [jvm]<br>val [name](name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [receiverType](receiver-type.md) | [jvm]<br>val [receiverType](receiver-type.md): [TypeName](../-type-name/index.md)? = null |
| [setter](setter.md) | [jvm]<br>val [setter](setter.md): [Func](../-func/index.md)? = null |
| [type](type.md) | [jvm]<br>val [type](type.md): [TypeName](../-type-name/index.md) |
