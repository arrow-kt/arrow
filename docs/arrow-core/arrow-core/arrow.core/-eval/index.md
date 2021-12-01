//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Eval](index.md)

# Eval

[common]\
sealed class [Eval](index.md)&lt;out [A](index.md)&gt;

Eval is a monad which controls evaluation of a value or a computation that produces a value.

Three basic evaluation strategies:

<ul><li>Now:    evaluated immediately</li><li>Later:  evaluated once when value is needed</li><li>Always: evaluated every time value is needed</li></ul>

The Later and Always are both lazy strategies while Now is eager. Later and Always are distinguished from each other only by memoization: once evaluated Later will save the value to be returned immediately if it is needed again. Always will run its computation every time.

methods, which use an internal trampoline to avoid stack overflows. Computation done within .map and .flatMap is always done lazily, even when applied to a Now instance.

It is not generally good style to pattern-match on Eval instances. Rather, use .map and .flatMap to chain computation, and use .value to get the result when needed. It is also not good style to create Eval instances whose computation involves calling .value on another Eval instance -- this can defeat the trampolining and lead to stack overflows.

Example of stack safety:

import arrow.core.Eval\
\
//sampleStart\
fun even(n: Int): Eval&lt;Boolean&gt; =\
  Eval.always { n == 0 }.flatMap {\
    if(it == true) Eval.now(true)\
    else odd(n - 1)\
  }\
\
fun odd(n: Int): Eval&lt;Boolean&gt; =\
  Eval.always { n == 0 }.flatMap {\
    if(it == true) Eval.now(false)\
    else even(n - 1)\
  }\
\
// if not wrapped in eval this type of computation would blow the stack and result in a StackOverflowError\
fun main() {\
  println(odd(100000).value())\
}\
//sampleEnd<!--- KNIT example-eval-01.kt -->

## Types

| Name | Summary |
|---|---|
| [Always](-always/index.md) | [common]<br>data class [Always](-always/index.md)&lt;out [A](-always/index.md)&gt;(f: () -&gt; [A](-always/index.md)) : [Eval](index.md)&lt;[A](-always/index.md)&gt; <br>Construct a lazy Eval<A> instance. |
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |
| [Defer](-defer/index.md) | [common]<br>data class [Defer](-defer/index.md)&lt;out [A](-defer/index.md)&gt;(thunk: () -&gt; [Eval](index.md)&lt;[A](-defer/index.md)&gt;) : [Eval](index.md)&lt;[A](-defer/index.md)&gt; <br>Defer is a type of Eval<A> that is used to defer computations which produce Eval<A>. |
| [FlatMap](-flat-map/index.md) | [common]<br>abstract class [FlatMap](-flat-map/index.md)&lt;out [A](-flat-map/index.md)&gt; : [Eval](index.md)&lt;[A](-flat-map/index.md)&gt; <br>FlatMap is a type of Eval<A> that is used to chain computations involving .map and .flatMap. Along with Eval#flatMap. It implements the trampoline that guarantees stack-safety. |
| [Later](-later/index.md) | [common]<br>data class [Later](-later/index.md)&lt;out [A](-later/index.md)&gt;(f: () -&gt; [A](-later/index.md)) : [Eval](index.md)&lt;[A](-later/index.md)&gt; <br>Construct a lazy Eval<A> instance. |
| [Now](-now/index.md) | [common]<br>data class [Now](-now/index.md)&lt;out [A](-now/index.md)&gt;(value: [A](-now/index.md)) : [Eval](index.md)&lt;[A](-now/index.md)&gt; <br>Construct an eager Eval<A> instance. In some sense it is equivalent to using a val. |

## Functions

| Name | Summary |
|---|---|
| [coflatMap](coflat-map.md) | [common]<br>inline fun &lt;[B](coflat-map.md)&gt; [coflatMap](coflat-map.md)(crossinline f: ([Eval](index.md)&lt;[A](index.md)&gt;) -&gt; [B](coflat-map.md)): [Eval](index.md)&lt;[B](coflat-map.md)&gt; |
| [flatMap](flat-map.md) | [common]<br>fun &lt;[B](flat-map.md)&gt; [flatMap](flat-map.md)(f: ([A](index.md)) -&gt; [Eval](index.md)&lt;[B](flat-map.md)&gt;): [Eval](index.md)&lt;[B](flat-map.md)&gt; |
| [map](map.md) | [common]<br>inline fun &lt;[B](map.md)&gt; [map](map.md)(crossinline f: ([A](index.md)) -&gt; [B](map.md)): [Eval](index.md)&lt;[B](map.md)&gt; |
| [memoize](memoize.md) | [common]<br>abstract fun [memoize](memoize.md)(): [Eval](index.md)&lt;[A](index.md)&gt; |
| [toString](to-string.md) | [common]<br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [value](value.md) | [common]<br>abstract fun [value](value.md)(): [A](index.md) |

## Inheritors

| Name |
|---|
| [Eval](-now/index.md) |
| [Eval](-later/index.md) |
| [Eval](-always/index.md) |
| [Eval](-defer/index.md) |
| [Eval](-flat-map/index.md) |

## Extensions

| Name | Summary |
|---|---|
| [replicate](../replicate.md) | [common]<br>fun &lt;[A](../replicate.md)&gt; [Eval](index.md)&lt;[A](../replicate.md)&gt;.[replicate](../replicate.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Eval](index.md)&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](../replicate.md)&gt;&gt;<br>fun &lt;[A](../replicate.md)&gt; [Eval](index.md)&lt;[A](../replicate.md)&gt;.[replicate](../replicate.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), MA: [Monoid](../../arrow.typeclasses/-monoid/index.md)&lt;[A](../replicate.md)&gt;): [Eval](index.md)&lt;[A](../replicate.md)&gt; |
| [zip](../zip.md) | [common]<br>fun &lt;[A](../zip.md), [B](../zip.md), [Z](../zip.md)&gt; [Eval](index.md)&lt;[A](../zip.md)&gt;.[zip](../zip.md)(b: [Eval](index.md)&lt;[B](../zip.md)&gt;, map: ([A](../zip.md), [B](../zip.md)) -&gt; [Z](../zip.md)): [Eval](index.md)&lt;[Z](../zip.md)&gt;<br>fun &lt;[A](../zip.md), [B](../zip.md)&gt; [Eval](index.md)&lt;[A](../zip.md)&gt;.[zip](../zip.md)(b: [Eval](index.md)&lt;[B](../zip.md)&gt;): [Eval](index.md)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](../zip.md), [B](../zip.md)&gt;&gt;<br>fun &lt;[A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md)&gt; [Eval](index.md)&lt;[A](../zip.md)&gt;.[zip](../zip.md)(b: [Eval](index.md)&lt;[B](../zip.md)&gt;, c: [Eval](index.md)&lt;[C](../zip.md)&gt;, map: ([A](../zip.md), [B](../zip.md), [C](../zip.md)) -&gt; [D](../zip.md)): [Eval](index.md)&lt;[D](../zip.md)&gt;<br>fun &lt;[A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md)&gt; [Eval](index.md)&lt;[A](../zip.md)&gt;.[zip](../zip.md)(b: [Eval](index.md)&lt;[B](../zip.md)&gt;, c: [Eval](index.md)&lt;[C](../zip.md)&gt;, d: [Eval](index.md)&lt;[D](../zip.md)&gt;, map: ([A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md)) -&gt; [E](../zip.md)): [Eval](index.md)&lt;[E](../zip.md)&gt;<br>fun &lt;[A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md), [F](../zip.md)&gt; [Eval](index.md)&lt;[A](../zip.md)&gt;.[zip](../zip.md)(b: [Eval](index.md)&lt;[B](../zip.md)&gt;, c: [Eval](index.md)&lt;[C](../zip.md)&gt;, d: [Eval](index.md)&lt;[D](../zip.md)&gt;, e: [Eval](index.md)&lt;[E](../zip.md)&gt;, map: ([A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md)) -&gt; [F](../zip.md)): [Eval](index.md)&lt;[F](../zip.md)&gt;<br>fun &lt;[A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md), [F](../zip.md), [G](../zip.md)&gt; [Eval](index.md)&lt;[A](../zip.md)&gt;.[zip](../zip.md)(b: [Eval](index.md)&lt;[B](../zip.md)&gt;, c: [Eval](index.md)&lt;[C](../zip.md)&gt;, d: [Eval](index.md)&lt;[D](../zip.md)&gt;, e: [Eval](index.md)&lt;[E](../zip.md)&gt;, f: [Eval](index.md)&lt;[F](../zip.md)&gt;, map: ([A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md), [F](../zip.md)) -&gt; [G](../zip.md)): [Eval](index.md)&lt;[G](../zip.md)&gt;<br>fun &lt;[A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md), [F](../zip.md), [G](../zip.md), [H](../zip.md)&gt; [Eval](index.md)&lt;[A](../zip.md)&gt;.[zip](../zip.md)(b: [Eval](index.md)&lt;[B](../zip.md)&gt;, c: [Eval](index.md)&lt;[C](../zip.md)&gt;, d: [Eval](index.md)&lt;[D](../zip.md)&gt;, e: [Eval](index.md)&lt;[E](../zip.md)&gt;, f: [Eval](index.md)&lt;[F](../zip.md)&gt;, g: [Eval](index.md)&lt;[G](../zip.md)&gt;, map: ([A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md), [F](../zip.md), [G](../zip.md)) -&gt; [H](../zip.md)): [Eval](index.md)&lt;[H](../zip.md)&gt;<br>fun &lt;[A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md), [F](../zip.md), [G](../zip.md), [H](../zip.md), [I](../zip.md)&gt; [Eval](index.md)&lt;[A](../zip.md)&gt;.[zip](../zip.md)(b: [Eval](index.md)&lt;[B](../zip.md)&gt;, c: [Eval](index.md)&lt;[C](../zip.md)&gt;, d: [Eval](index.md)&lt;[D](../zip.md)&gt;, e: [Eval](index.md)&lt;[E](../zip.md)&gt;, f: [Eval](index.md)&lt;[F](../zip.md)&gt;, g: [Eval](index.md)&lt;[G](../zip.md)&gt;, h: [Eval](index.md)&lt;[H](../zip.md)&gt;, map: ([A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md), [F](../zip.md), [G](../zip.md), [H](../zip.md)) -&gt; [I](../zip.md)): [Eval](index.md)&lt;[I](../zip.md)&gt;<br>fun &lt;[A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md), [F](../zip.md), [G](../zip.md), [H](../zip.md), [I](../zip.md), [J](../zip.md)&gt; [Eval](index.md)&lt;[A](../zip.md)&gt;.[zip](../zip.md)(b: [Eval](index.md)&lt;[B](../zip.md)&gt;, c: [Eval](index.md)&lt;[C](../zip.md)&gt;, d: [Eval](index.md)&lt;[D](../zip.md)&gt;, e: [Eval](index.md)&lt;[E](../zip.md)&gt;, f: [Eval](index.md)&lt;[F](../zip.md)&gt;, g: [Eval](index.md)&lt;[G](../zip.md)&gt;, h: [Eval](index.md)&lt;[H](../zip.md)&gt;, i: [Eval](index.md)&lt;[I](../zip.md)&gt;, map: ([A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md), [F](../zip.md), [G](../zip.md), [H](../zip.md), [I](../zip.md)) -&gt; [J](../zip.md)): [Eval](index.md)&lt;[J](../zip.md)&gt;<br>fun &lt;[A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md), [F](../zip.md), [G](../zip.md), [H](../zip.md), [I](../zip.md), [J](../zip.md), [K](../zip.md)&gt; [Eval](index.md)&lt;[A](../zip.md)&gt;.[zip](../zip.md)(b: [Eval](index.md)&lt;[B](../zip.md)&gt;, c: [Eval](index.md)&lt;[C](../zip.md)&gt;, d: [Eval](index.md)&lt;[D](../zip.md)&gt;, e: [Eval](index.md)&lt;[E](../zip.md)&gt;, f: [Eval](index.md)&lt;[F](../zip.md)&gt;, g: [Eval](index.md)&lt;[G](../zip.md)&gt;, h: [Eval](index.md)&lt;[H](../zip.md)&gt;, i: [Eval](index.md)&lt;[I](../zip.md)&gt;, j: [Eval](index.md)&lt;[J](../zip.md)&gt;, map: ([A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [E](../zip.md), [F](../zip.md), [G](../zip.md), [H](../zip.md), [I](../zip.md), [J](../zip.md)) -&gt; [K](../zip.md)): [Eval](index.md)&lt;[K](../zip.md)&gt; |
