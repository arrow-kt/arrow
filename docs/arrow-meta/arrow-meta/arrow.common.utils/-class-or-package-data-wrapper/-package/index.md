//[arrow-meta](../../../../index.md)/[arrow.common.utils](../../index.md)/[ClassOrPackageDataWrapper](../index.md)/[Package](index.md)

# Package

[jvm]\
class [Package](index.md)(nameResolver: NameResolver, packageProto: ProtoBuf.Package, package: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) : [ClassOrPackageDataWrapper](../index.md)

## Functions

| Name | Summary |
|---|---|
| [getTypeParameter](get-type-parameter.md) | [jvm]<br>open override fun [getTypeParameter](get-type-parameter.md)(typeParameterIndex: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): ProtoBuf.TypeParameter? |

## Properties

| Name | Summary |
|---|---|
| [constructorList](constructor-list.md) | [jvm]<br>open override val [constructorList](constructor-list.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;ProtoBuf.Constructor&gt; |
| [functionList](function-list.md) | [jvm]<br>open override val [functionList](function-list.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;ProtoBuf.Function&gt; |
| [nameResolver](name-resolver.md) | [jvm]<br>open override val [nameResolver](name-resolver.md): NameResolver |
| [package](package.md) | [jvm]<br>open override val [package](package.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [packageProto](package-proto.md) | [jvm]<br>val [packageProto](package-proto.md): ProtoBuf.Package |
| [propertyList](property-list.md) | [jvm]<br>open override val [propertyList](property-list.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;ProtoBuf.Property&gt; |
| [typeParameters](type-parameters.md) | [jvm]<br>open override val [typeParameters](type-parameters.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;ProtoBuf.TypeParameter&gt; |
