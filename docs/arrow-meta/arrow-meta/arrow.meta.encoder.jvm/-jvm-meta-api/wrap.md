//[arrow-meta](../../../index.md)/[arrow.meta.encoder.jvm](../index.md)/[JvmMetaApi](index.md)/[wrap](wrap.md)

# wrap

[jvm]\
open fun [TypeName.TypeVariable](../../arrow.meta.ast/-type-name/-type-variable/index.md).[wrap](wrap.md)(wrapped: [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[TypeName](../../arrow.meta.ast/-type-name/index.md), [TypeName.ParameterizedType](../../arrow.meta.ast/-type-name/-parameterized-type/index.md)&gt;): [TypeName.TypeVariable](../../arrow.meta.ast/-type-name/-type-variable/index.md)

open fun [TypeName.WildcardType](../../arrow.meta.ast/-type-name/-wildcard-type/index.md).[wrap](wrap.md)(wrapped: [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[TypeName](../../arrow.meta.ast/-type-name/index.md), [TypeName.ParameterizedType](../../arrow.meta.ast/-type-name/-parameterized-type/index.md)&gt;): [TypeName.WildcardType](../../arrow.meta.ast/-type-name/-wildcard-type/index.md)

open fun [TypeName.ParameterizedType](../../arrow.meta.ast/-type-name/-parameterized-type/index.md).[wrap](wrap.md)(wrapped: [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[TypeName](../../arrow.meta.ast/-type-name/index.md), [TypeName.ParameterizedType](../../arrow.meta.ast/-type-name/-parameterized-type/index.md)&gt;): [TypeName.ParameterizedType](../../arrow.meta.ast/-type-name/-parameterized-type/index.md)

open fun [TypeName.Classy](../../arrow.meta.ast/-type-name/-classy/index.md).[wrap](wrap.md)(wrapped: [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[TypeName](../../arrow.meta.ast/-type-name/index.md), [TypeName.ParameterizedType](../../arrow.meta.ast/-type-name/-parameterized-type/index.md)&gt;): [TypeName.Classy](../../arrow.meta.ast/-type-name/-classy/index.md)

open fun [TypeName.FunctionLiteral](../../arrow.meta.ast/-type-name/-function-literal/index.md).[wrap](wrap.md)(wrapped: [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[TypeName](../../arrow.meta.ast/-type-name/index.md), [TypeName.ParameterizedType](../../arrow.meta.ast/-type-name/-parameterized-type/index.md)&gt;): [TypeName.FunctionLiteral](../../arrow.meta.ast/-type-name/-function-literal/index.md)

open fun [TypeName](../../arrow.meta.ast/-type-name/index.md).[wrap](wrap.md)(wrapped: [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[TypeName](../../arrow.meta.ast/-type-name/index.md), [TypeName.ParameterizedType](../../arrow.meta.ast/-type-name/-parameterized-type/index.md)&gt;): [TypeName](../../arrow.meta.ast/-type-name/index.md)

Applies replacement on a type recursively changing it's wrapper type for it's wrapped type and MetaApi.getDownKind as needed ex: Kind -> Set<A>

[jvm]\
open fun [Func](../../arrow.meta.ast/-func/index.md).[wrap](wrap.md)(wrappedType: [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[TypeName](../../arrow.meta.ast/-type-name/index.md), [TypeName.ParameterizedType](../../arrow.meta.ast/-type-name/-parameterized-type/index.md)&gt;? = null): [Func](../../arrow.meta.ast/-func/index.md)

Applies replacement on all types of this function recursively changing wrapper types for their wrapped type over all three receiver, parameters and return type. and MetaApi.getDownKind as needed
