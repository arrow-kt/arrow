//[arrow-meta](../../../index.md)/[arrow.meta.decoder](../index.md)/[TypeDecoder](index.md)

# TypeDecoder

[jvm]\
interface [TypeDecoder](index.md) : [MetaDecoder](../-meta-decoder/index.md)&lt;[Type](../../arrow.meta.ast/-type/index.md)&gt; 

Type decoder that leverages Kotlin Poet to organize imports and output formatted code

## Functions

| Name | Summary |
|---|---|
| [code](code.md) | [jvm]<br>open fun [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[Func](../../arrow.meta.ast/-func/index.md)&gt;.[code](code.md)(dummy: [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) = Unit): [Code](../../arrow.meta.ast/-code/index.md)<br>open fun [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[Parameter](../../arrow.meta.ast/-parameter/index.md)&gt;.[code](code.md)(f: ([Parameter](../../arrow.meta.ast/-parameter/index.md)) -&gt; [Code](../../arrow.meta.ast/-code/index.md) = { Code(it.lyrics().toString()) }): [Code](../../arrow.meta.ast/-code/index.md) |
| [codeNames](code-names.md) | [jvm]<br>open fun [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[Parameter](../../arrow.meta.ast/-parameter/index.md)&gt;.[codeNames](code-names.md)(): [Code](../../arrow.meta.ast/-code/index.md) |
| [decode](decode.md) | [jvm]<br>open override fun [decode](decode.md)(tree: [Type](../../arrow.meta.ast/-type/index.md)): [Code](../../arrow.meta.ast/-code/index.md) |
| [empty](empty.md) | [jvm]<br>open fun CodeBlock.Companion.[empty](empty.md)(): CodeBlock |
| [invoke](invoke.md) | [jvm]<br>open operator fun [Code.Companion](../../arrow.meta.ast/-code/-companion/index.md).[invoke](invoke.md)(f: () -&gt; [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Code](../../arrow.meta.ast/-code/index.md) |
| [joinToCode](join-to-code.md) | [jvm]<br>open fun &lt;[A](join-to-code.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](join-to-code.md)&gt;.[joinToCode](join-to-code.md)(separator: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Code](../../arrow.meta.ast/-code/index.md) |
| [lyrics](lyrics.md) | [jvm]<br>open fun [Annotation](../../arrow.meta.ast/-annotation/index.md).[lyrics](lyrics.md)(): AnnotationSpec<br>open fun [Code](../../arrow.meta.ast/-code/index.md).[lyrics](lyrics.md)(): CodeBlock<br>open fun [Func](../../arrow.meta.ast/-func/index.md).[lyrics](lyrics.md)(): FunSpec<br>open fun [Modifier](../../arrow.meta.ast/-modifier/index.md).[lyrics](lyrics.md)(): KModifier<br>open fun [Parameter](../../arrow.meta.ast/-parameter/index.md).[lyrics](lyrics.md)(): ParameterSpec<br>open fun [Property](../../arrow.meta.ast/-property/index.md).[lyrics](lyrics.md)(): PropertySpec<br>open fun [Type](../../arrow.meta.ast/-type/index.md).[lyrics](lyrics.md)(): TypeSpec<br>open fun [TypeName](../../arrow.meta.ast/-type-name/index.md).[lyrics](lyrics.md)(): TypeName<br>open fun [TypeName.Classy](../../arrow.meta.ast/-type-name/-classy/index.md).[lyrics](lyrics.md)(): ClassName<br>open fun [TypeName.FunctionLiteral](../../arrow.meta.ast/-type-name/-function-literal/index.md).[lyrics](lyrics.md)(): TypeName<br>open fun [TypeName.ParameterizedType](../../arrow.meta.ast/-type-name/-parameterized-type/index.md).[lyrics](lyrics.md)(): TypeName<br>open fun [TypeName.TypeVariable](../../arrow.meta.ast/-type-name/-type-variable/index.md).[lyrics](lyrics.md)(): TypeVariableName<br>open fun [TypeName.WildcardType](../../arrow.meta.ast/-type-name/-wildcard-type/index.md).[lyrics](lyrics.md)(): TypeName<br>open fun [UseSiteTarget](../../arrow.meta.ast/-use-site-target/index.md).[lyrics](lyrics.md)(): AnnotationSpec.UseSiteTarget |
| [unaryPlus](unary-plus.md) | [jvm]<br>open operator fun [TypeName](../../arrow.meta.ast/-type-name/index.md)?.[unaryPlus](unary-plus.md)(): [Code](../../arrow.meta.ast/-code/index.md)<br>open operator fun [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html).[unaryPlus](unary-plus.md)(): [Code](../../arrow.meta.ast/-code/index.md)<br>open operator fun [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[TypeName.TypeVariable](../../arrow.meta.ast/-type-name/-type-variable/index.md)&gt;.[unaryPlus](unary-plus.md)(): [Code](../../arrow.meta.ast/-code/index.md)<br>open operator fun [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)&gt;.[unaryPlus](unary-plus.md)(): [Code](../../arrow.meta.ast/-code/index.md) |

## Inheritors

| Name |
|---|
| [JvmMetaApi](../../arrow.meta.encoder.jvm/-jvm-meta-api/index.md) |
