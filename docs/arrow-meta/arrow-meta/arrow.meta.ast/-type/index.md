//[arrow-meta](../../../index.md)/[arrow.meta.ast](../index.md)/[Type](index.md)

# Type

[jvm]\
data class [Type](index.md)(packageName: [PackageName](../-package-name/index.md), name: [TypeName](../-type-name/index.md), kind: [Type.Shape](-shape/index.md), kdoc: [Code](../-code/index.md)?, modifiers: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Modifier](../-modifier/index.md)&gt;, primaryConstructor: [Func](../-func/index.md)?, superclass: [TypeName](../-type-name/index.md)?, initializer: [Code](../-code/index.md)?, superInterfaces: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[TypeName](../-type-name/index.md)&gt;, enumConstants: [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), [Type](index.md)&gt;, annotations: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Annotation](../-annotation/index.md)&gt;, typeVariables: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[TypeName.TypeVariable](../-type-name/-type-variable/index.md)&gt;, superclassConstructorParameters: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Code](../-code/index.md)&gt;, properties: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Property](../-property/index.md)&gt;, declaredFunctions: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Func](../-func/index.md)&gt;, allFunctions: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Func](../-func/index.md)&gt;, types: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Type](index.md)&gt;) : [Tree](../-tree/index.md)

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [jvm]<br>object [Companion](-companion/index.md) |
| [Shape](-shape/index.md) | [jvm]<br>sealed class [Shape](-shape/index.md) |

## Properties

| Name | Summary |
|---|---|
| [allFunctions](all-functions.md) | [jvm]<br>val [allFunctions](all-functions.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Func](../-func/index.md)&gt; |
| [annotations](annotations.md) | [jvm]<br>val [annotations](annotations.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Annotation](../-annotation/index.md)&gt; |
| [declaredFunctions](declared-functions.md) | [jvm]<br>val [declaredFunctions](declared-functions.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Func](../-func/index.md)&gt; |
| [enumConstants](enum-constants.md) | [jvm]<br>val [enumConstants](enum-constants.md): [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), [Type](index.md)&gt; |
| [initializer](initializer.md) | [jvm]<br>val [initializer](initializer.md): [Code](../-code/index.md)? = null |
| [kdoc](kdoc.md) | [jvm]<br>val [kdoc](kdoc.md): [Code](../-code/index.md)? = null |
| [kind](kind.md) | [jvm]<br>val [kind](kind.md): [Type.Shape](-shape/index.md) |
| [modifiers](modifiers.md) | [jvm]<br>val [modifiers](modifiers.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Modifier](../-modifier/index.md)&gt; |
| [name](name.md) | [jvm]<br>val [name](name.md): [TypeName](../-type-name/index.md) |
| [packageName](package-name.md) | [jvm]<br>val [packageName](package-name.md): [PackageName](../-package-name/index.md) |
| [primaryConstructor](primary-constructor.md) | [jvm]<br>val [primaryConstructor](primary-constructor.md): [Func](../-func/index.md)? = null |
| [properties](properties.md) | [jvm]<br>val [properties](properties.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Property](../-property/index.md)&gt; |
| [superclass](superclass.md) | [jvm]<br>val [superclass](superclass.md): [TypeName](../-type-name/index.md)? = null |
| [superclassConstructorParameters](superclass-constructor-parameters.md) | [jvm]<br>val [superclassConstructorParameters](superclass-constructor-parameters.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Code](../-code/index.md)&gt; |
| [superInterfaces](super-interfaces.md) | [jvm]<br>val [superInterfaces](super-interfaces.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[TypeName](../-type-name/index.md)&gt; |
| [types](types.md) | [jvm]<br>val [types](types.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Type](index.md)&gt; |
| [typeVariables](type-variables.md) | [jvm]<br>val [typeVariables](type-variables.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[TypeName.TypeVariable](../-type-name/-type-variable/index.md)&gt; |
