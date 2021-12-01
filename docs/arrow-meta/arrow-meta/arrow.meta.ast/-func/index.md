//[arrow-meta](../../../index.md)/[arrow.meta.ast](../index.md)/[Func](index.md)

# Func

[jvm]\
data class [Func](index.md)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), kdoc: [Code](../-code/index.md)?, receiverType: [TypeName](../-type-name/index.md)?, returnType: [TypeName](../-type-name/index.md)?, body: [Code](../-code/index.md)?, annotations: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Annotation](../-annotation/index.md)&gt;, modifiers: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Modifier](../-modifier/index.md)&gt;, typeVariables: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[TypeName.TypeVariable](../-type-name/-type-variable/index.md)&gt;, parameters: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Parameter](../-parameter/index.md)&gt;, jvmMethodSignature: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) : [Tree](../-tree/index.md)

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [jvm]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [annotations](annotations.md) | [jvm]<br>val [annotations](annotations.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Annotation](../-annotation/index.md)&gt; |
| [body](body.md) | [jvm]<br>val [body](body.md): [Code](../-code/index.md)? = null |
| [jvmMethodSignature](jvm-method-signature.md) | [jvm]<br>val [jvmMethodSignature](jvm-method-signature.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [kdoc](kdoc.md) | [jvm]<br>val [kdoc](kdoc.md): [Code](../-code/index.md)? = null |
| [modifiers](modifiers.md) | [jvm]<br>val [modifiers](modifiers.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Modifier](../-modifier/index.md)&gt; |
| [name](name.md) | [jvm]<br>val [name](name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [parameters](parameters.md) | [jvm]<br>val [parameters](parameters.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Parameter](../-parameter/index.md)&gt; |
| [receiverType](receiver-type.md) | [jvm]<br>val [receiverType](receiver-type.md): [TypeName](../-type-name/index.md)? = null |
| [returnType](return-type.md) | [jvm]<br>val [returnType](return-type.md): [TypeName](../-type-name/index.md)? |
| [typeVariables](type-variables.md) | [jvm]<br>val [typeVariables](type-variables.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[TypeName.TypeVariable](../-type-name/-type-variable/index.md)&gt; |
