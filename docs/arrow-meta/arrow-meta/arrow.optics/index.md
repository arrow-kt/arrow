//[arrow-meta](../../index.md)/[arrow.optics](index.md)

# Package arrow.optics

## Types

| Name | Summary |
|---|---|
| [AnnotatedElement](-annotated-element/index.md) | [jvm]<br>data class [AnnotatedElement](-annotated-element/index.md)(type: [TypeElement](https://docs.oracle.com/javase/8/docs/api/javax/lang/model/element/TypeElement.html), classData: [ClassOrPackageDataWrapper.Class](../arrow.common.utils/-class-or-package-data-wrapper/-class/index.md), targets: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Target](-target/index.md)&gt;) |
| [DataClassDsl](index.md#-1612631170%2FClasslikes%2F-35121544) | [jvm]<br>typealias [DataClassDsl](index.md#-1612631170%2FClasslikes%2F-35121544) = [Target.DataClassDsl](-target/-data-class-dsl/index.md) |
| [Focus](-focus/index.md) | [jvm]<br>sealed class [Focus](-focus/index.md) |
| [IsoTarget](index.md#971942047%2FClasslikes%2F-35121544) | [jvm]<br>typealias [IsoTarget](index.md#971942047%2FClasslikes%2F-35121544) = [Target.Iso](-target/-iso/index.md) |
| [LensTarget](index.md#247387454%2FClasslikes%2F-35121544) | [jvm]<br>typealias [LensTarget](index.md#247387454%2FClasslikes%2F-35121544) = [Target.Lens](-target/-lens/index.md) |
| [NonNullFocus](index.md#752967881%2FClasslikes%2F-35121544) | [jvm]<br>typealias [NonNullFocus](index.md#752967881%2FClasslikes%2F-35121544) = [Focus.NonNull](-focus/-non-null/index.md) |
| [NullableFocus](index.md#882531902%2FClasslikes%2F-35121544) | [jvm]<br>typealias [NullableFocus](index.md#882531902%2FClasslikes%2F-35121544) = [Focus.Nullable](-focus/-nullable/index.md) |
| [OpticsProcessor](-optics-processor/index.md) | [jvm]<br>@AutoService(value = [[Processor::class](https://docs.oracle.com/javase/8/docs/api/javax/annotation/processing/Processor.html)])<br>class [OpticsProcessor](-optics-processor/index.md) : [AbstractProcessor](../arrow.common.utils/-abstract-processor/index.md) |
| [OptionalTarget](index.md#-2045194084%2FClasslikes%2F-35121544) | [jvm]<br>typealias [OptionalTarget](index.md#-2045194084%2FClasslikes%2F-35121544) = [Target.Optional](-target/-optional/index.md) |
| [OptionFocus](index.md#34522450%2FClasslikes%2F-35121544) | [jvm]<br>typealias [OptionFocus](index.md#34522450%2FClasslikes%2F-35121544) = [Focus.Option](-focus/-option/index.md) |
| [PrismTarget](index.md#473742691%2FClasslikes%2F-35121544) | [jvm]<br>typealias [PrismTarget](index.md#473742691%2FClasslikes%2F-35121544) = [Target.Prism](-target/-prism/index.md) |
| [SealedClassDsl](index.md#-1473107188%2FClasslikes%2F-35121544) | [jvm]<br>typealias [SealedClassDsl](index.md#-1473107188%2FClasslikes%2F-35121544) = [Target.SealedClassDsl](-target/-sealed-class-dsl/index.md) |
| [Snippet](-snippet/index.md) | [jvm]<br>data class [Snippet](-snippet/index.md)(package: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), imports: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)&gt;, content: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) |
| [Target](-target/index.md) | [jvm]<br>sealed class [Target](-target/index.md) |

## Functions

| Name | Summary |
|---|---|
| [asFileText](as-file-text.md) | [jvm]<br>fun [Snippet](-snippet/index.md).[asFileText](as-file-text.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [generateIsos](generate-isos.md) | [jvm]<br>fun [generateIsos](generate-isos.md)(ele: [AnnotatedElement](-annotated-element/index.md), target: [IsoTarget](index.md#971942047%2FClasslikes%2F-35121544)): [Snippet](-snippet/index.md) |
| [generateLensDsl](generate-lens-dsl.md) | [jvm]<br>fun [generateLensDsl](generate-lens-dsl.md)(ele: [AnnotatedElement](-annotated-element/index.md), optic: [DataClassDsl](index.md#-1612631170%2FClasslikes%2F-35121544)): [Snippet](-snippet/index.md) |
| [generateLenses](generate-lenses.md) | [jvm]<br>fun [generateLenses](generate-lenses.md)(ele: [AnnotatedElement](-annotated-element/index.md), target: [LensTarget](index.md#247387454%2FClasslikes%2F-35121544)): [Snippet](-snippet/index.md) |
| [generateOptionalDsl](generate-optional-dsl.md) | [jvm]<br>fun [generateOptionalDsl](generate-optional-dsl.md)(ele: [AnnotatedElement](-annotated-element/index.md), optic: [DataClassDsl](index.md#-1612631170%2FClasslikes%2F-35121544)): [Snippet](-snippet/index.md) |
| [generateOptionals](generate-optionals.md) | [jvm]<br>fun [generateOptionals](generate-optionals.md)(ele: [AnnotatedElement](-annotated-element/index.md), target: [OptionalTarget](index.md#-2045194084%2FClasslikes%2F-35121544)): [Snippet](-snippet/index.md) |
| [generatePrismDsl](generate-prism-dsl.md) | [jvm]<br>fun [generatePrismDsl](generate-prism-dsl.md)(ele: [AnnotatedElement](-annotated-element/index.md), isoOptic: [SealedClassDsl](index.md#-1473107188%2FClasslikes%2F-35121544)): [Snippet](-snippet/index.md) |
| [generatePrisms](generate-prisms.md) | [jvm]<br>fun [generatePrisms](generate-prisms.md)(ele: [AnnotatedElement](-annotated-element/index.md), target: [PrismTarget](index.md#473742691%2FClasslikes%2F-35121544)): [Snippet](-snippet/index.md) |
| [lensParamName](lens-param-name.md) | [jvm]<br>fun [Focus](-focus/index.md).[lensParamName](lens-param-name.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

## Properties

| Name | Summary |
|---|---|
| [dslErrorMessage](dsl-error-message.md) | [jvm]<br>val [Element](https://docs.oracle.com/javase/8/docs/api/javax/lang/model/element/Element.html).[dslErrorMessage](dsl-error-message.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [Every](-every.md) | [jvm]<br>const val [Every](-every.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [Fold](-fold.md) | [jvm]<br>const val [Fold](-fold.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [Getter](-getter.md) | [jvm]<br>const val [Getter](-getter.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [Iso](-iso.md) | [jvm]<br>const val [Iso](-iso.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [isoErrorMessage](iso-error-message.md) | [jvm]<br>val [Element](https://docs.oracle.com/javase/8/docs/api/javax/lang/model/element/Element.html).[isoErrorMessage](iso-error-message.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [isoTooBigErrorMessage](iso-too-big-error-message.md) | [jvm]<br>val [Element](https://docs.oracle.com/javase/8/docs/api/javax/lang/model/element/Element.html).[isoTooBigErrorMessage](iso-too-big-error-message.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [Lens](-lens.md) | [jvm]<br>const val [Lens](-lens.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [lensErrorMessage](lens-error-message.md) | [jvm]<br>val [Element](https://docs.oracle.com/javase/8/docs/api/javax/lang/model/element/Element.html).[lensErrorMessage](lens-error-message.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [opticsAnnotationClass](optics-annotation-class.md) | [jvm]<br>val [opticsAnnotationClass](optics-annotation-class.md): [Class](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)&lt;[optics](../../../arrow-annotations/arrow-annotations/arrow.optics/optics/index.md)&gt; |
| [opticsAnnotationKClass](optics-annotation-k-class.md) | [jvm]<br>val [opticsAnnotationKClass](optics-annotation-k-class.md): [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;[optics](../../../arrow-annotations/arrow-annotations/arrow.optics/optics/index.md)&gt; |
| [Optional](-optional.md) | [jvm]<br>const val [Optional](-optional.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [optionalErrorMessage](optional-error-message.md) | [jvm]<br>val [Element](https://docs.oracle.com/javase/8/docs/api/javax/lang/model/element/Element.html).[optionalErrorMessage](optional-error-message.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [otherClassTypeErrorMessage](other-class-type-error-message.md) | [jvm]<br>val [Element](https://docs.oracle.com/javase/8/docs/api/javax/lang/model/element/Element.html).[otherClassTypeErrorMessage](other-class-type-error-message.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [Pair](-pair.md) | [jvm]<br>const val [Pair](-pair.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [Prism](-prism.md) | [jvm]<br>const val [Prism](-prism.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [prismErrorMessage](prism-error-message.md) | [jvm]<br>val [Element](https://docs.oracle.com/javase/8/docs/api/javax/lang/model/element/Element.html).[prismErrorMessage](prism-error-message.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [Setter](-setter.md) | [jvm]<br>const val [Setter](-setter.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [targetNames](target-names.md) | [jvm]<br>val [Target](-target/index.md).[targetNames](target-names.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)&gt; |
| [Traversal](-traversal.md) | [jvm]<br>const val [Traversal](-traversal.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [Triple](-triple.md) | [jvm]<br>const val [Triple](-triple.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [Tuple](-tuple.md) | [jvm]<br>const val [Tuple](-tuple.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
