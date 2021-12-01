//[arrow-meta](../../../index.md)/[arrow.meta.encoder](../index.md)/[MetaApi](index.md)/[removeConstrains](remove-constrains.md)

# removeConstrains

[jvm]\
abstract fun [TypeName.WildcardType](../../arrow.meta.ast/-type-name/-wildcard-type/index.md).[removeConstrains](remove-constrains.md)(): [TypeName.WildcardType](../../arrow.meta.ast/-type-name/-wildcard-type/index.md)

Remove undesired constrains such as java.lang.Object which appears in upper bound position in certain type shapes

[jvm]\
abstract fun [TypeName.ParameterizedType](../../arrow.meta.ast/-type-name/-parameterized-type/index.md).[removeConstrains](remove-constrains.md)(): [TypeName.ParameterizedType](../../arrow.meta.ast/-type-name/-parameterized-type/index.md)

abstract fun [TypeName.Classy](../../arrow.meta.ast/-type-name/-classy/index.md).[removeConstrains](remove-constrains.md)(): [TypeName.Classy](../../arrow.meta.ast/-type-name/-classy/index.md)

abstract fun [TypeName.FunctionLiteral](../../arrow.meta.ast/-type-name/-function-literal/index.md).[removeConstrains](remove-constrains.md)(): [TypeName.FunctionLiteral](../../arrow.meta.ast/-type-name/-function-literal/index.md)

abstract fun [TypeName](../../arrow.meta.ast/-type-name/index.md).[removeConstrains](remove-constrains.md)(): [TypeName](../../arrow.meta.ast/-type-name/index.md)

abstract fun [TypeName.TypeVariable](../../arrow.meta.ast/-type-name/-type-variable/index.md).[removeConstrains](remove-constrains.md)(): [TypeName.TypeVariable](../../arrow.meta.ast/-type-name/-type-variable/index.md)

[jvm]\
abstract fun [Func](../../arrow.meta.ast/-func/index.md).[removeConstrains](remove-constrains.md)(keepModifiers: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)&lt;[Modifier](../../arrow.meta.ast/-modifier/index.md)&gt; = emptySet()): [Func](../../arrow.meta.ast/-func/index.md)

Removes all modifiers and annotations from this function and normalizes type variables upper bound constrains to not explicitly include implicit types such as java.lang.Object. Preserves all modifiers [keepModifiers](remove-constrains.md)

[jvm]\
abstract fun [Parameter](../../arrow.meta.ast/-parameter/index.md).[removeConstrains](remove-constrains.md)(): [Parameter](../../arrow.meta.ast/-parameter/index.md)

## See also

jvm

| | |
|---|---|
| [arrow.meta.encoder.MetaApi](remove-constrains.md) |  |
