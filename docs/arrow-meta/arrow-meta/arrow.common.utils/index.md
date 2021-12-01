//[arrow-meta](../../index.md)/[arrow.common.utils](index.md)

# Package arrow.common.utils

## Types

| Name | Summary |
|---|---|
| [AbstractProcessor](-abstract-processor/index.md) | [jvm]<br>abstract class [AbstractProcessor](-abstract-processor/index.md) : KotlinAbstractProcessor, [ProcessorUtils](-processor-utils/index.md), [KotlinMetatadataEncoder](../arrow.meta.encoder.jvm/-kotlin-metatadata-encoder/index.md) |
| [ClassOrPackageDataWrapper](-class-or-package-data-wrapper/index.md) | [jvm]<br>sealed class [ClassOrPackageDataWrapper](-class-or-package-data-wrapper/index.md) |
| [KnownException](-known-exception/index.md) | [jvm]<br>class [KnownException](-known-exception/index.md)(message: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), element: [Element](https://docs.oracle.com/javase/8/docs/api/javax/lang/model/element/Element.html)?) : [RuntimeException](https://docs.oracle.com/javase/8/docs/api/java/lang/RuntimeException.html) |
| [ProcessorUtils](-processor-utils/index.md) | [jvm]<br>interface [ProcessorUtils](-processor-utils/index.md) : KotlinMetadataUtils |

## Functions

| Name | Summary |
|---|---|
| [asClassOrPackageDataWrapper](as-class-or-package-data-wrapper.md) | [jvm]<br>fun ClassData.[asClassOrPackageDataWrapper](as-class-or-package-data-wrapper.md)(package: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [ClassOrPackageDataWrapper.Class](-class-or-package-data-wrapper/-class/index.md)<br>fun PackageData.[asClassOrPackageDataWrapper](as-class-or-package-data-wrapper.md)(package: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [ClassOrPackageDataWrapper.Package](-class-or-package-data-wrapper/-package/index.md) |
| [extractFullName](extract-full-name.md) | [jvm]<br>fun ProtoBuf.Type.[extractFullName](extract-full-name.md)(classData: [ClassOrPackageDataWrapper](-class-or-package-data-wrapper/index.md), outputTypeAlias: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [getParameter](get-parameter.md) | [jvm]<br>fun [ClassOrPackageDataWrapper](-class-or-package-data-wrapper/index.md).[getParameter](get-parameter.md)(function: ProtoBuf.Function, parameterElement: [VariableElement](https://docs.oracle.com/javase/8/docs/api/javax/lang/model/element/VariableElement.html)): ProtoBuf.ValueParameter |
| [getPropertyOrNull](get-property-or-null.md) | [jvm]<br>fun [ClassOrPackageDataWrapper](-class-or-package-data-wrapper/index.md).[getPropertyOrNull](get-property-or-null.md)(methodElement: [ExecutableElement](https://docs.oracle.com/javase/8/docs/api/javax/lang/model/element/ExecutableElement.html)): ProtoBuf.Property? |
| [knownError](known-error.md) | [jvm]<br>fun [knownError](known-error.md)(message: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), element: [Element](https://docs.oracle.com/javase/8/docs/api/javax/lang/model/element/Element.html)? = null): [Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html) |
| [recurseFilesUpwards](recurse-files-upwards.md) | [jvm]<br>fun [recurseFilesUpwards](recurse-files-upwards.md)(fileNames: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)&gt;): [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)<br>fun [recurseFilesUpwards](recurse-files-upwards.md)(fileNames: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)&gt;, currentDirectory: [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html)): [File](https://docs.oracle.com/javase/8/docs/api/java/io/File.html) |
| [removeBackticks](remove-backticks.md) | [jvm]<br>fun [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html).[removeBackticks](remove-backticks.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [toCamelCase](to-camel-case.md) | [jvm]<br>fun [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html).[toCamelCase](to-camel-case.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [typeConstraints](type-constraints.md) | [jvm]<br>fun [ClassOrPackageDataWrapper](-class-or-package-data-wrapper/index.md).[typeConstraints](type-constraints.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [writeSafe](write-safe.md) | [jvm]<br>fun [Filer](https://docs.oracle.com/javase/8/docs/api/javax/annotation/processing/Filer.html).[writeSafe](write-safe.md)(pkg: [CharSequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char-sequence/index.html), name: [CharSequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char-sequence/index.html), fileString: [CharSequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char-sequence/index.html), logger: (message: [CharSequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char-sequence/index.html)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)? = null, vararg originatingElements: [Element](https://docs.oracle.com/javase/8/docs/api/javax/lang/model/element/Element.html)?)<br>Writes this to filer. |
| [writeSafeTo](write-safe-to.md) | [jvm]<br>fun FileSpec.[writeSafeTo](write-safe-to.md)(filer: [Filer](https://docs.oracle.com/javase/8/docs/api/javax/annotation/processing/Filer.html), logger: (message: [CharSequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char-sequence/index.html)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)? = null)<br>Writes this to filer. |

## Properties

| Name | Summary |
|---|---|
| [fullName](full-name.md) | [jvm]<br>val [ClassOrPackageDataWrapper.Class](-class-or-package-data-wrapper/-class/index.md).[fullName](full-name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [isCompanionOrObject](is-companion-or-object.md) | [jvm]<br>val ProtoBuf.Class.Kind.[isCompanionOrObject](is-companion-or-object.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [isSealed](is-sealed.md) | [jvm]<br>val ProtoBuf.Class.[isSealed](is-sealed.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [simpleName](simple-name.md) | [jvm]<br>val [ClassOrPackageDataWrapper.Class](-class-or-package-data-wrapper/-class/index.md).[simpleName](simple-name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
