//[arrow-meta](../../index.md)/[arrow.meta.encoder.jvm](index.md)

# Package arrow.meta.encoder.jvm

## Types

| Name | Summary |
|---|---|
| [DownKindReduction](-down-kind-reduction/index.md) | [jvm]<br>data class [DownKindReduction](-down-kind-reduction/index.md)(pckg: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), additionalTypeArgs: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)&gt;) |
| [EncodingError](-encoding-error/index.md) | [jvm]<br>sealed class [EncodingError](-encoding-error/index.md) |
| [JvmMetaApi](-jvm-meta-api/index.md) | [jvm]<br>interface [JvmMetaApi](-jvm-meta-api/index.md) : [MetaApi](../arrow.meta.encoder/-meta-api/index.md), [TypeElementEncoder](-type-element-encoder/index.md), [ProcessorUtils](../arrow.common.utils/-processor-utils/index.md), [TypeDecoder](../arrow.meta.decoder/-type-decoder/index.md)<br>A JVM implementation of the Meta Api meant to be mixed in with kapt annotation processors |
| [KotlinMetatadataEncoder](-kotlin-metatadata-encoder/index.md) | [jvm]<br>interface [KotlinMetatadataEncoder](-kotlin-metatadata-encoder/index.md) |
| [KotlinPoetEncoder](-kotlin-poet-encoder/index.md) | [jvm]<br>interface [KotlinPoetEncoder](-kotlin-poet-encoder/index.md) |
| [TypeElementEncoder](-type-element-encoder/index.md) | [jvm]<br>interface [TypeElementEncoder](-type-element-encoder/index.md) : [KotlinMetatadataEncoder](-kotlin-metatadata-encoder/index.md), [KotlinPoetEncoder](-kotlin-poet-encoder/index.md), [ProcessorUtils](../arrow.common.utils/-processor-utils/index.md) |

## Functions

| Name | Summary |
|---|---|
| [asKotlin](as-kotlin.md) | [jvm]<br>fun [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html).[asKotlin](as-kotlin.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>TODO this is horrible is there a canonical way to obtain a kotlin type given a fqn java type name? |
| [asPlatform](as-platform.md) | [jvm]<br>fun [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html).[asPlatform](as-platform.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [downKParts](down-k-parts.md) | [jvm]<br>fun [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html).[downKParts](down-k-parts.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)&gt; |
| [quote](quote.md) | [jvm]<br>fun [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html).[quote](quote.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
