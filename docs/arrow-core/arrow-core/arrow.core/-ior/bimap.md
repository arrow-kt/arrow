//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Ior](index.md)/[bimap](bimap.md)

# bimap

[common]\
inline fun &lt;[C](bimap.md), [D](bimap.md)&gt; [bimap](bimap.md)(fa: ([A](index.md)) -&gt; [C](bimap.md), fb: ([B](index.md)) -&gt; [D](bimap.md)): [Ior](index.md)&lt;[C](bimap.md), [D](bimap.md)&gt;

Apply fa if this is a [Left](-left/index.md) or [Both](-both/index.md) to A and apply fb if this is [Right](-right/index.md) or [Both](-both/index.md) to B

Example:

&lt;!--- KNIT example-ior-11.kt --&gt;\
Ior.Right(12).bimap ({ "flower" }, { 12 }) // Result: Right(12)\
Ior.Left(12).bimap({ "flower" }, { 12 })  // Result: Left("flower")\
Ior.Both(12, "power").bimap ({ a, b -&gt; "flower $b" },{ a * 2})  // Result: Both("flower power", 24)<!--- KNIT example-ior-12.kt -->
