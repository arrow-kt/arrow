//[arrow-meta](../../../index.md)/[arrow.meta.encoder.jvm](../index.md)/[KotlinMetatadataEncoder](index.md)

# KotlinMetatadataEncoder

[jvm]\
interface [KotlinMetatadataEncoder](index.md)

## Functions

| Name | Summary |
|---|---|
| [asModifier](as-modifier.md) | [jvm]<br>open fun ProtoBuf.Modality.[asModifier](as-modifier.md)(): [Modifier](../../arrow.meta.ast/-modifier/index.md)<br>open fun ProtoBuf.Visibility.[asModifier](as-modifier.md)(): [Modifier](../../arrow.meta.ast/-modifier/index.md)? |
| [asTypeName](as-type-name.md) | [jvm]<br>open fun ProtoBuf.Type.[asTypeName](as-type-name.md)(meta: [ClassOrPackageDataWrapper.Class](../../arrow.common.utils/-class-or-package-data-wrapper/-class/index.md)): [TypeName](../../arrow.meta.ast/-type-name/index.md) |
| [extractFullName](extract-full-name.md) | [jvm]<br>open fun ProtoBuf.Type.[extractFullName](extract-full-name.md)(classData: [ClassOrPackageDataWrapper](../../arrow.common.utils/-class-or-package-data-wrapper/index.md), outputTypeAlias: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [modifiersFromFlags](modifiers-from-flags.md) | [jvm]<br>open fun [modifiersFromFlags](modifiers-from-flags.md)(flags: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Modifier](../../arrow.meta.ast/-modifier/index.md)&gt; |
| [nameOf](name-of.md) | [jvm]<br>open fun [ClassOrPackageDataWrapper.Class](../../arrow.common.utils/-class-or-package-data-wrapper/-class/index.md).[nameOf](name-of.md)(id: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [supertypes](supertypes.md) | [jvm]<br>open fun [supertypes](supertypes.md)(current: [ClassOrPackageDataWrapper.Class](../../arrow.common.utils/-class-or-package-data-wrapper/-class/index.md), typeTable: TypeTable, processorUtils: [ProcessorUtils](../../arrow.common.utils/-processor-utils/index.md), acc: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[ClassOrPackageDataWrapper](../../arrow.common.utils/-class-or-package-data-wrapper/index.md)&gt;): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[ClassOrPackageDataWrapper](../../arrow.common.utils/-class-or-package-data-wrapper/index.md)&gt; |
| [toMeta](to-meta.md) | [jvm]<br>open fun ProtoBuf.Modality.[toMeta](to-meta.md)(): [Modifier](../../arrow.meta.ast/-modifier/index.md)<br>open fun ProtoBuf.TypeParameter.Variance.[toMeta](to-meta.md)(): [Modifier](../../arrow.meta.ast/-modifier/index.md)<br>open fun ProtoBuf.TypeParameter.[toMeta](to-meta.md)(owner: [ClassOrPackageDataWrapper.Class](../../arrow.common.utils/-class-or-package-data-wrapper/-class/index.md)): [TypeName.TypeVariable](../../arrow.meta.ast/-type-name/-type-variable/index.md)<br>open fun ProtoBuf.ValueParameter.[toMeta](to-meta.md)(owner: [ClassOrPackageDataWrapper.Class](../../arrow.common.utils/-class-or-package-data-wrapper/-class/index.md)): [Parameter](../../arrow.meta.ast/-parameter/index.md)<br>open fun ProtoBuf.Function.[toMeta](to-meta.md)(owner: [ClassOrPackageDataWrapper.Class](../../arrow.common.utils/-class-or-package-data-wrapper/-class/index.md), executableElement: [ExecutableElement](https://docs.oracle.com/javase/8/docs/api/javax/lang/model/element/ExecutableElement.html)): [Func](../../arrow.meta.ast/-func/index.md) |

## Inheritors

| Name |
|---|
| [AbstractProcessor](../../arrow.common.utils/-abstract-processor/index.md) |
| [TypeElementEncoder](../-type-element-encoder/index.md) |
