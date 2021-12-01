//[arrow-fx-coroutines-test](../../index.md)/[arrow.fx.coroutines](index.md)

# Package arrow.fx.coroutines

## Types

| Name | Summary |
|---|---|
| [ArrowFxSpec](-arrow-fx-spec/index.md) | [common]<br>abstract class [ArrowFxSpec](-arrow-fx-spec/index.md)(iterations: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), spec: [ArrowFxSpec](-arrow-fx-spec/index.md).() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)) : [UnitSpec](../../../arrow-core-test/arrow-core-test/arrow.core.test/-unit-spec/index.md)<br>Simple overwritten Kotest StringSpec (UnitSpec) to reduce stress on tests. |
| [NamedThreadFactory](-named-thread-factory/index.md) | [jvm]<br>class [NamedThreadFactory](-named-thread-factory/index.md)(mkName: ([Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)) -&gt; [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) : [ThreadFactory](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadFactory.html) |
| [SideEffect](-side-effect/index.md) | [common]<br>data class [SideEffect](-side-effect/index.md)(counter: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)) |

## Functions

| Name | Summary |
|---|---|
| [assertThrowable](assert-throwable.md) | [common]<br>inline fun &lt;[A](assert-throwable.md)&gt; [assertThrowable](assert-throwable.md)(executable: () -&gt; [A](assert-throwable.md)): [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)<br>Example usage: |
| [charRange](char-range.md) | [common]<br>fun Arb.Companion.[charRange](char-range.md)(): Arb&lt;[CharRange](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.ranges/-char-range/index.html)&gt; |
| [either](either.md) | [common]<br>fun &lt;[A](either.md)&gt; [either](either.md)(e: [Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html), [A](either.md)&gt;): Matcher&lt;[Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html), [A](either.md)&gt;&gt;<br>fun &lt;[L](either.md), [R](either.md)&gt; Arb.Companion.[either](either.md)(left: Arb&lt;[L](either.md)&gt;, right: Arb&lt;[R](either.md)&gt;): Arb&lt;[Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[L](either.md), [R](either.md)&gt;&gt; |
| [flow](flow.md) | [common]<br>fun &lt;[A](flow.md)&gt; Arb.Companion.[flow](flow.md)(arbA: Arb&lt;[A](flow.md)&gt;): Arb&lt;Flow&lt;[A](flow.md)&gt;&gt; |
| [function](function.md) | [common]<br>fun &lt;[O](function.md)&gt; Arb.Companion.[function](function.md)(arb: Arb&lt;[O](function.md)&gt;): Arb&lt;() -&gt; [O](function.md)&gt; |
| [functionAToB](function-a-to-b.md) | [common]<br>fun &lt;[A](function-a-to-b.md), [B](function-a-to-b.md)&gt; Arb.Companion.[functionAToB](function-a-to-b.md)(arb: Arb&lt;[B](function-a-to-b.md)&gt;): Arb&lt;([A](function-a-to-b.md)) -&gt; [B](function-a-to-b.md)&gt; |
| [intRange](int-range.md) | [common]<br>fun Arb.Companion.[intRange](int-range.md)(min: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = Int.MIN_VALUE, max: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = Int.MAX_VALUE): Arb&lt;[IntRange](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.ranges/-int-range/index.html)&gt; |
| [leftException](left-exception.md) | [common]<br>fun [leftException](left-exception.md)(e: [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)): Matcher&lt;[Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html), *&gt;&gt; |
| [longRange](long-range.md) | [common]<br>fun Arb.Companion.[longRange](long-range.md)(min: [Long](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) = Long.MIN_VALUE, max: [Long](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) = Long.MAX_VALUE): Arb&lt;[LongRange](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.ranges/-long-range/index.html)&gt; |
| [nullable](nullable.md) | [common]<br>fun &lt;[A](nullable.md)&gt; Arb.Companion.[nullable](nullable.md)(arb: Arb&lt;[A](nullable.md)&gt;): Arb&lt;[A](nullable.md)?&gt; |
| [rethrow](rethrow.md) | [common]<br>fun &lt;[A](rethrow.md)&gt; [Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html), [A](rethrow.md)&gt;.[rethrow](rethrow.md)(): [A](rethrow.md)<br>Useful for testing success & error scenarios with an Either generator |
| [shift](shift.md) | [common]<br>suspend fun [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html).[shift](shift.md)() |
| [suspend](suspend.md) | [common]<br>suspend fun &lt;[A](suspend.md)&gt; [A](suspend.md).[suspend](suspend.md)(): [A](suspend.md)<br>suspend fun [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html).[suspend](suspend.md)(): [Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html) |
| [suspended](suspended.md) | [common]<br>fun &lt;[A](suspended.md)&gt; [A](suspended.md).[suspended](suspended.md)(): suspend () -&gt; [A](suspended.md) |
| [throwable](throwable.md) | [common]<br>fun Arb.Companion.[throwable](throwable.md)(): Arb&lt;[Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)&gt; |
| [toEither](to-either.md) | [common]<br>fun &lt;[A](to-either.md)&gt; [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[A](to-either.md)&gt;.[toEither](to-either.md)(): [Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html), [A](to-either.md)&gt; |
| [unit](unit.md) | [common]<br>fun Arb.Companion.[unit](unit.md)(): Arb&lt;[Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)&gt; |
| [validated](validated.md) | [common]<br>fun &lt;[L](validated.md), [R](validated.md)&gt; Arb.Companion.[validated](validated.md)(left: Arb&lt;[L](validated.md)&gt;, right: Arb&lt;[R](validated.md)&gt;): Arb&lt;[Validated](../../../arrow-core/arrow-core/arrow.core/-validated/index.md)&lt;[L](validated.md), [R](validated.md)&gt;&gt; |
| [validatedNel](validated-nel.md) | [common]<br>fun &lt;[L](validated-nel.md), [R](validated-nel.md)&gt; Arb.Companion.[validatedNel](validated-nel.md)(left: Arb&lt;[L](validated-nel.md)&gt;, right: Arb&lt;[R](validated-nel.md)&gt;): Arb&lt;[ValidatedNel](../../../arrow-core/arrow.core/-validated-nel/index.md)&lt;[L](validated-nel.md), [R](validated-nel.md)&gt;&gt; |

## Properties

| Name | Summary |
|---|---|
| [single](single.md) | [jvm]<br>val [single](single.md): [Resource](../../../arrow-fx-coroutines/arrow-fx-coroutines/arrow.fx.coroutines/-resource/index.md)&lt;[CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html)&gt; |
| [singleThreadName](single-thread-name.md) | [jvm]<br>val [singleThreadName](single-thread-name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [threadName](thread-name.md) | [jvm]<br>val [threadName](thread-name.md): suspend () -&gt; [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
