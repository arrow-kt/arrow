---
layout: docs
title: MonadFilter
permalink: /docs/typeclasses/monadfilter/
---

## MonadFilter

`MonadFilter` is a type class that abstract away the option of interrupting computation if a given predicate is not satisfied. 

All instances of `MonadFilter` provide syntax over their respective data types to comprehend monadically over their computation:

##continueWith

Binding over `MonadFilter` instances with `bindingFilter` brings into scope the `continueIf` guard that requires a `Boolean` predicate as value. If the predicate is `true` the computation will continue and if the predicate returns `false` the computation is short-circuited returning monad filter instance `empty()` value. 

In the example below we demonstrate monadic comprehension over the `MonadFilter` instances for both `Option` and `ListKW` since both data types can provide a safe `empty` value. 

When the guard is satisfied the computation continues

```kotlin:ank
Option.monadFilter().bindingFilter {
 val a = Option(1).bind()
 val b = Option(1).bind()
 val c = a + b
 continueIf(c > 0)
 yields(c)
}
```

```kotlin:ank
ListKW.monadFilter().bindingFilter {
 val a = listOf(1).k().bind()
 val b = listOf(1).k().bind()
 val c = a + b
 continueIf(c > 0)
 yields(c)
}
```    

When the guard returns false the computation is interrupted and the `empty()` value is returned 

```kotlin:ank
Option.monadFilter().bindingFilter {
 val a = Option(1).bind()
 val b = Option(1).bind()
 val c = a + b
 continueIf(c < 0)
 yields(c)
}
```

```kotlin:ank
ListKW.monadFilter().bindingFilter {
 val a = listOf(1).k().bind()
 val b = listOf(1).k().bind()
 val c = a + b
 continueIf(c < 0)
 yields(c)
}
```    

##bindWithFilter

Binding over `MonadFilter` instances with `bindingFilter` brings into scope the `bindWithFilter` guard that requires a `Boolean` predicate as value getting matched on the monad capturing inner value. If the predicate is `true` the computation will continue and if the predicate returns `false` the computation is short-circuited returning the monad filter instance `empty()` value. 

In the example below we demonstrate monadic comprehension over the `MonadFilter` instances for both `Option` and `ListKW` since both data types can provide a safe `empty` value. 

When the guard is satisfied the computation continues

```kotlin:ank
Option.monadFilter().bindingFilter {
 val a = Option(1).bind()
 val b = Option(1).bindWithFilter { it == a } //continues
 val c = a + b
 yields(c)
}
```

```kotlin:ank
ListKW.monadFilter().bindingFilter {
 val a = listOf(1).k().bind()
 val b = listOf(1).k().bindWithFilter { it == a } //continues
 val c = a + b
 yields(c)
}
```

When a predicate fails the computation short circuited yielding the monad's empty value

```kotlin:ank
Option.monadFilter().bindingFilter {
 val a = Option(0).bind()
 val b = Option(1).bindWithFilter { it == a } //short circuits because a is 0
 val c = a + b
 yields(c)
}
```   

```kotlin:ank
ListKW.monadFilter().bindingFilter {
 val a = listOf(0).k().bind()
 val b = listOf(1).k().bindWithFilter { it == a } //short circuits because a is 0
 val c = a + b
 yields(c)
}
```