//[arrow-meta](../../../index.md)/[arrow.meta.decoder](../index.md)/[MetaDecoder](index.md)

# MetaDecoder

[jvm]\
interface [MetaDecoder](index.md)&lt;in [A](index.md) : [Tree](../../arrow.meta.ast/-tree/index.md)&gt;

Provides ways to go from a [Tree](../../arrow.meta.ast/-tree/index.md) to [Code](../../arrow.meta.ast/-code/index.md) for the purposes of code gen and reporting

## Functions

| Name | Summary |
|---|---|
| [decode](decode.md) | [jvm]<br>abstract fun [decode](decode.md)(tree: [A](index.md)): [Code](../../arrow.meta.ast/-code/index.md) |

## Inheritors

| Name |
|---|
| [TypeDecoder](../-type-decoder/index.md) |
