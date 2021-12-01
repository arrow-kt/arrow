//[arrow-meta](../../../index.md)/[arrow.common.utils](../index.md)/[ClassOrPackageDataWrapper](index.md)

# ClassOrPackageDataWrapper

[jvm]\
sealed class [ClassOrPackageDataWrapper](index.md)

## Types

| Name | Summary |
|---|---|
| [Class](-class/index.md) | [jvm]<br>class [Class](-class/index.md)(nameResolver: NameResolver, classProto: ProtoBuf.Class, package: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) : [ClassOrPackageDataWrapper](index.md) |
| [Package](-package/index.md) | [jvm]<br>class [Package](-package/index.md)(nameResolver: NameResolver, packageProto: ProtoBuf.Package, package: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) : [ClassOrPackageDataWrapper](index.md) |

## Functions

| Name | Summary |
|---|---|
| [getTypeParameter](get-type-parameter.md) | [jvm]<br>abstract fun [getTypeParameter](get-type-parameter.md)(typeParameterIndex: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): ProtoBuf.TypeParameter? |

## Properties

| Name | Summary |
|---|---|
| [constructorList](constructor-list.md) | [jvm]<br>abstract val [constructorList](constructor-list.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;ProtoBuf.Constructor&gt; |
| [functionList](function-list.md) | [jvm]<br>abstract val [functionList](function-list.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;ProtoBuf.Function&gt; |
| [nameResolver](name-resolver.md) | [jvm]<br>abstract val [nameResolver](name-resolver.md): NameResolver |
| [package](package.md) | [jvm]<br>abstract val [package](package.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [propertyList](property-list.md) | [jvm]<br>abstract val [propertyList](property-list.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;ProtoBuf.Property&gt; |
| [typeParameters](type-parameters.md) | [jvm]<br>abstract val [typeParameters](type-parameters.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;ProtoBuf.TypeParameter&gt; |

## Inheritors

| Name |
|---|
| [ClassOrPackageDataWrapper](-package/index.md) |
| [ClassOrPackageDataWrapper](-class/index.md) |

## Extensions

| Name | Summary |
|---|---|
| [getParameter](../get-parameter.md) | [jvm]<br>fun [ClassOrPackageDataWrapper](index.md).[getParameter](../get-parameter.md)(function: ProtoBuf.Function, parameterElement: [VariableElement](https://docs.oracle.com/javase/8/docs/api/javax/lang/model/element/VariableElement.html)): ProtoBuf.ValueParameter |
| [getPropertyOrNull](../get-property-or-null.md) | [jvm]<br>fun [ClassOrPackageDataWrapper](index.md).[getPropertyOrNull](../get-property-or-null.md)(methodElement: [ExecutableElement](https://docs.oracle.com/javase/8/docs/api/javax/lang/model/element/ExecutableElement.html)): ProtoBuf.Property? |
| [typeConstraints](../type-constraints.md) | [jvm]<br>fun [ClassOrPackageDataWrapper](index.md).[typeConstraints](../type-constraints.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
