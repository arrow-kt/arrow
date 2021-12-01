//[arrow-meta](../../../index.md)/[arrow.meta.encoder](../index.md)/[MetaApi](index.md)/[downKindReturnType](down-kind-return-type.md)

# downKindReturnType

[jvm]\
abstract fun [Func](../../arrow.meta.ast/-func/index.md).[downKindReturnType](down-kind-return-type.md)(): [Func](../../arrow.meta.ast/-func/index.md)

Performs a type application transforming the return type in this function in kinded position into it's concrete counterpart: ex: someFun(): Kind&lt;ForOption, A&gt; ->someFun(): Option&lt;A&gt;
