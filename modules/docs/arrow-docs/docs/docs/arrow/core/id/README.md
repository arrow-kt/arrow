---
layout: docs
title: Id
permalink: /docs/arrow/core/id/
redirect_from:
  - /docs/datatypes/id/
video: DBvVd1pfLMo
---

## Id

{:.beginner}
beginner

[Перевод на русский](/docs/arrow/core/id/ru/)

The identity monad can be seen as the ambient monad that encodes the effect of having no effect.
It is ambient in the sense that plain pure values are values of `Id`.

```kotlin:ank
import arrow.*
import arrow.core.*

Id("hello")
```

Using this type declaration, we can treat our Id type constructor as a `Monad` and as a `Comonad`.
The `just` method, which has type `A -> Id<A>` just becomes the identity function. The `map` method
from `Functor` just becomes function application

```kotlin:ank
val id: Id<Int> = Id.just(3)
id.map{it + 3}
```

### Supported type classes

```kotlin:ank:replace
import arrow.reflect.*
import arrow.core.*

DataType(Id::class).tcMarkdownList()
```
