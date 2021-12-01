//[arrow-meta](../../../index.md)/[arrow.meta.encoder](../index.md)/[MetaApi](index.md)/[downKind](down-kind.md)

# downKind

[jvm]\
abstract fun [Parameter](../../arrow.meta.ast/-parameter/index.md).[downKind](down-kind.md)(): [Parameter](../../arrow.meta.ast/-parameter/index.md)

Performs a type application transforming the type of this parameter in kinded position into it's concrete counterpart: ex: arg: Kind&lt;ForOption, A&gt; ->arg: Option&lt;A&gt;

[jvm]\
abstract val [TypeName](../../arrow.meta.ast/-type-name/index.md).[downKind](down-kind.md): [TypeName](../../arrow.meta.ast/-type-name/index.md)

Performs a type application transforming a type in kinded position into it's concrete counterpart: ex: Kind&lt;ForOption, A&gt; -&gt; Option&lt;A&gt;

[jvm]\
abstract val [TypeName.TypeVariable](../../arrow.meta.ast/-type-name/-type-variable/index.md).[downKind](down-kind.md): [TypeName](../../arrow.meta.ast/-type-name/index.md)

abstract val [TypeName.FunctionLiteral](../../arrow.meta.ast/-type-name/-function-literal/index.md).[downKind](down-kind.md): [TypeName](../../arrow.meta.ast/-type-name/index.md)

abstract val [TypeName.ParameterizedType](../../arrow.meta.ast/-type-name/-parameterized-type/index.md).[downKind](down-kind.md): [TypeName](../../arrow.meta.ast/-type-name/index.md)

abstract val [TypeName.WildcardType](../../arrow.meta.ast/-type-name/-wildcard-type/index.md).[downKind](down-kind.md): [TypeName](../../arrow.meta.ast/-type-name/index.md)

abstract val [TypeName.Classy](../../arrow.meta.ast/-type-name/-classy/index.md).[downKind](down-kind.md): [TypeName](../../arrow.meta.ast/-type-name/index.md)
