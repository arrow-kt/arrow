//[arrow-meta](../../../index.md)/[arrow.meta.encoder](../index.md)/[MetaApi](index.md)/[addExtraDummyArg](add-extra-dummy-arg.md)

# addExtraDummyArg

[jvm]\
abstract fun [Func](../../arrow.meta.ast/-func/index.md).[addExtraDummyArg](add-extra-dummy-arg.md)(): [Func](../../arrow.meta.ast/-func/index.md)

Appends (...argN: Unit = Unit) at the end of the parameter lists of this function. This is frequently done to work around JVM overload clashes specially when extending kinded values which show the same JVM signature after erasure
