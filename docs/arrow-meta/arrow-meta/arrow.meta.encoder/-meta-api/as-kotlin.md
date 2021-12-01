//[arrow-meta](../../../index.md)/[arrow.meta.encoder](../index.md)/[MetaApi](index.md)/[asKotlin](as-kotlin.md)

# asKotlin

[jvm]\
abstract fun [TypeName](../../arrow.meta.ast/-type-name/index.md).[asKotlin](as-kotlin.md)(): [TypeName](../../arrow.meta.ast/-type-name/index.md)

Normalizes potentially rogue types coming from Java introspection into their Kotlin counterpart. ex: java.lang.Integer -&gt; Kotlin.Int It's implementation is partial and does not cover all corner cases.

[jvm]\
abstract fun [TypeName.TypeVariable](../../arrow.meta.ast/-type-name/-type-variable/index.md).[asKotlin](as-kotlin.md)(): [TypeName.TypeVariable](../../arrow.meta.ast/-type-name/-type-variable/index.md)

abstract fun [TypeName.ParameterizedType](../../arrow.meta.ast/-type-name/-parameterized-type/index.md).[asKotlin](as-kotlin.md)(): [TypeName.ParameterizedType](../../arrow.meta.ast/-type-name/-parameterized-type/index.md)

abstract fun [TypeName.FunctionLiteral](../../arrow.meta.ast/-type-name/-function-literal/index.md).[asKotlin](as-kotlin.md)(): [TypeName.FunctionLiteral](../../arrow.meta.ast/-type-name/-function-literal/index.md)

abstract fun [TypeName.WildcardType](../../arrow.meta.ast/-type-name/-wildcard-type/index.md).[asKotlin](as-kotlin.md)(): [TypeName.WildcardType](../../arrow.meta.ast/-type-name/-wildcard-type/index.md)

abstract fun [TypeName.Classy](../../arrow.meta.ast/-type-name/-classy/index.md).[asKotlin](as-kotlin.md)(): [TypeName.Classy](../../arrow.meta.ast/-type-name/-classy/index.md)
