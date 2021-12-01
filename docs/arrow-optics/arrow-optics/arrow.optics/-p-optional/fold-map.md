//[arrow-optics](../../../index.md)/[arrow.optics](../index.md)/[POptional](index.md)/[foldMap](fold-map.md)

# foldMap

[common]\
open override fun &lt;[R](fold-map.md)&gt; [foldMap](fold-map.md)(M: [Monoid](../../../../arrow-core/arrow-core/arrow.typeclasses/-monoid/index.md)&lt;[R](fold-map.md)&gt;, source: [S](index.md), map: ([A](index.md)) -&gt; [R](fold-map.md)): [R](fold-map.md)

Map each target to a type R and use a Monoid to fold the results
