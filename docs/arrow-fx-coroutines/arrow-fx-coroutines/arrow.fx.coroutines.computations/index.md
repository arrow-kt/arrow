//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines.computations](index.md)

# Package arrow.fx.coroutines.computations

## Types

| Name | Summary |
|---|---|
| [ResourceEffect](-resource-effect/index.md) | [common]<br>interface [ResourceEffect](-resource-effect/index.md)<br>Computation block for the [Resource](../arrow.fx.coroutines/-resource/index.md) type. The [Resource](../arrow.fx.coroutines/-resource/index.md) allows us to describe resources as immutable values, and compose them together in simple ways. This way you can split the logic of what a Resource is and how it should be closed from how you use them. |

## Functions

| Name | Summary |
|---|---|
| [resource](resource.md) | [common]<br>fun &lt;[A](resource.md)&gt; [resource](resource.md)(f: suspend [ResourceEffect](-resource-effect/index.md).() -&gt; [A](resource.md)): [Resource](../arrow.fx.coroutines/-resource/index.md)&lt;[A](resource.md)&gt; |
