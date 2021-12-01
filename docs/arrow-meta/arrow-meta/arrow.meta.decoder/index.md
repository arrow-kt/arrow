//[arrow-meta](../../index.md)/[arrow.meta.decoder](index.md)

# Package arrow.meta.decoder

## Types

| Name | Summary |
|---|---|
| [MetaDecoder](-meta-decoder/index.md) | [jvm]<br>interface [MetaDecoder](-meta-decoder/index.md)&lt;in [A](-meta-decoder/index.md) : [Tree](../arrow.meta.ast/-tree/index.md)&gt;<br>Provides ways to go from a [Tree](../arrow.meta.ast/-tree/index.md) to [Code](../arrow.meta.ast/-code/index.md) for the purposes of code gen and reporting |
| [TypeDecoder](-type-decoder/index.md) | [jvm]<br>interface [TypeDecoder](-type-decoder/index.md) : [MetaDecoder](-meta-decoder/index.md)&lt;[Type](../arrow.meta.ast/-type/index.md)&gt; <br>Type decoder that leverages Kotlin Poet to organize imports and output formatted code |
