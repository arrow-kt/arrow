//[arrow-meta](../../../index.md)/[arrow.meta.encoder](../index.md)/[MetaApi](index.md)/[prependExtraDummyArg](prepend-extra-dummy-arg.md)

# prependExtraDummyArg

[jvm]\
abstract fun [Func](../../arrow.meta.ast/-func/index.md).[prependExtraDummyArg](prepend-extra-dummy-arg.md)(): [Func](../../arrow.meta.ast/-func/index.md)

Prepends (...argN: Unit = Unit) at the beginning of the parameter lists of this function. This is frequently done to work around JVM overload clashes specially when extending kinded values which show the same JVM signature after erasure
