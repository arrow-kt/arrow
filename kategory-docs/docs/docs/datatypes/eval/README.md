---
layout: docs
title: Eval
permalink: /docs/datatypes/eval/
---

## Eval

Eval is a monad that allows us to control evaluation and chain lazy operations that are not executed until `value()` is invoked. 

Eval includes internally trampolining facilities which allows us to chain computations without fear off blowing the stack.
There are two different factors that play into evaluation: memoization and laziness.

Eval supports memoization, eager and lazy evaluation strategies

### now

`Eval#now` creates an Eval instance from an already constructed value but still defers evaluation when chaining expressions with `map` and `flatMap`

```kotlin:ank
import kategory.*

val eager = Eval.now(1).map { it + 1 }
eager.value()
```

### later

`Eval#later` creates an Eval instance from a function deferring it's evaluation until `.value()` is invoked memoizing the computed value.

```kotlin:ank
val lazyEvaled = Eval.later { println("expensive computation"); 1 }
lazyEvaled.value()
```

`"expensive computation"` is only printed once since the results are memoized and multiple calls to `value()` will just return the cached value.

### always

`Eval#always` creates an Eval instance from a function deferring it's evaluation until `.value()` is invoked recomputing each time `.value()` is invoked.

```kotlin:ank
val alwaysEvaled = Eval.always { println("expensive computation"); 1 }
alwaysEvaled.value()
```

### Stack safety

`Eval` empowers stack safe programs by chaining lazy computations

```kotlin:ank
fun even(n: Int): Eval<Boolean> =
  Eval.always { n == 0 }.flatMap {
    if(it == true) Eval.now(true)
    else odd(n - 1)
  }

fun odd(n: Int): Eval<Boolean> =
  Eval.always { n == 0 }.flatMap {
    if(it == true) Eval.now(false)
    else even(n - 1)
  }

// if not wrapped in eval this type of computation would blow the stack and result in a StackOverflowError
odd(100000).value() 
```

### Available Instances

```kotlin:ank
import kategory.debug.*

showInstances<EvalHK, Unit>()
```
 
# Credits
 
Contents partially adapted from [Cats Eval](https://typelevel.org/cats/datatypes/eval.html)
