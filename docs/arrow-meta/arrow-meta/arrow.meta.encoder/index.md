//[arrow-meta](../../index.md)/[arrow.meta.encoder](index.md)

# Package arrow.meta.encoder

## Types

| Name | Summary |
|---|---|
| [MetaApi](-meta-api/index.md) | [jvm]<br>interface [MetaApi](-meta-api/index.md)<br>Arrow-Meta provides a hydrated AST representing Kotlin Code. The current implementation relies on TypeElement, KotlinMetadata library and Kotlin Poet to get all the info it needs so you can access the values without manually fiddling with proto buffers or java reflection. |
| [TypeClassInstance](-type-class-instance/index.md) | [jvm]<br>data class [TypeClassInstance](-type-class-instance/index.md)(instance: [Type](../arrow.meta.ast/-type/index.md), dataType: [Type](../arrow.meta.ast/-type/index.md), typeClass: [Type](../arrow.meta.ast/-type/index.md), instanceTypeElement: [TypeElement](https://docs.oracle.com/javase/8/docs/api/javax/lang/model/element/TypeElement.html), dataTypeTypeElement: [TypeElement](https://docs.oracle.com/javase/8/docs/api/javax/lang/model/element/TypeElement.html), typeClassTypeElement: [TypeElement](https://docs.oracle.com/javase/8/docs/api/javax/lang/model/element/TypeElement.html), projectedCompanion: [TypeName](../arrow.meta.ast/-type-name/index.md)) |

## Properties

| Name | Summary |
|---|---|
| [KotlinReservedKeywords](-kotlin-reserved-keywords.md) | [jvm]<br>val [KotlinReservedKeywords](-kotlin-reserved-keywords.md): [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)&gt; |
