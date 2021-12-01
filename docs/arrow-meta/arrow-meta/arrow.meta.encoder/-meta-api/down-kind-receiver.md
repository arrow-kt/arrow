//[arrow-meta](../../../index.md)/[arrow.meta.encoder](../index.md)/[MetaApi](index.md)/[downKindReceiver](down-kind-receiver.md)

# downKindReceiver

[jvm]\
abstract fun [Func](../../arrow.meta.ast/-func/index.md).[downKindReceiver](down-kind-receiver.md)(): [Func](../../arrow.meta.ast/-func/index.md)

Performs a type application transforming the receiver type in this function in kinded position into it's concrete counterpart: ex: Kind&lt;ForOption, A&gt;.someFun(): A ->Option.someFun(): A
