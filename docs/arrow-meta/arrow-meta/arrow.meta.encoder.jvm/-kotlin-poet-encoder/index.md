//[arrow-meta](../../../index.md)/[arrow.meta.encoder.jvm](../index.md)/[KotlinPoetEncoder](index.md)

# KotlinPoetEncoder

[jvm]\
interface [KotlinPoetEncoder](index.md)

## Functions

| Name | Summary |
|---|---|
| [kotlinMetadataUtils](kotlin-metadata-utils.md) | [jvm]<br>abstract fun [kotlinMetadataUtils](kotlin-metadata-utils.md)(): KotlinMetadataUtils |
| [metaApi](meta-api.md) | [jvm]<br>abstract fun [metaApi](meta-api.md)(): [MetaApi](../../arrow.meta.encoder/-meta-api/index.md) |
| [toMeta](to-meta.md) | [jvm]<br>open fun AnnotationSpec.[toMeta](to-meta.md)(): [Annotation](../../arrow.meta.ast/-annotation/index.md)<br>open fun TypeName.[toMeta](to-meta.md)(): [TypeName](../../arrow.meta.ast/-type-name/index.md)<br>open fun TypeVariableName.[toMeta](to-meta.md)(): [TypeName.TypeVariable](../../arrow.meta.ast/-type-name/-type-variable/index.md)<br>open fun FunSpec.[toMeta](to-meta.md)(element: [ExecutableElement](https://docs.oracle.com/javase/8/docs/api/javax/lang/model/element/ExecutableElement.html)): [Func](../../arrow.meta.ast/-func/index.md) |
| [typeNameToMetaImpl](type-name-to-meta-impl.md) | [jvm]<br>open fun [typeNameToMetaImpl](type-name-to-meta-impl.md)(typeName: TypeName): [TypeName](../../arrow.meta.ast/-type-name/index.md) |

## Properties

| Name | Summary |
|---|---|
| [typeNameToMeta](type-name-to-meta.md) | [jvm]<br>abstract val [typeNameToMeta](type-name-to-meta.md): (typeName: TypeName) -&gt; [TypeName](../../arrow.meta.ast/-type-name/index.md) |

## Inheritors

| Name |
|---|
| [TypeElementEncoder](../-type-element-encoder/index.md) |
