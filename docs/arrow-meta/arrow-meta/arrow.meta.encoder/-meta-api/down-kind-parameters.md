//[arrow-meta](../../../index.md)/[arrow.meta.encoder](../index.md)/[MetaApi](index.md)/[downKindParameters](down-kind-parameters.md)

# downKindParameters

[jvm]\
abstract fun [Func](../../arrow.meta.ast/-func/index.md).[downKindParameters](down-kind-parameters.md)(): [Func](../../arrow.meta.ast/-func/index.md)

Performs a type application transforming all parameter types in this function in kinded position into it's concrete counterpart: ex: (fa: Kind&lt;ForOption, A&gt;) -&gt; (fa: Option&lt;A&gt;)
