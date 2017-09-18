---
layout: docs
title: Id
permalink: /docs/datatypes/id/
---

## Id

The identity monad can be seen as the ambient monad that encodes the effect of having no effect. 
It is ambient in the sense that plain pure values are values of `Id`.

```kotlin:ank
import kategory.*

Id("hello")
```

Using this type declaration, we can treat our Id type constructor as a `Monad` and as a `Comonad`. 
The `pure` method, which has type `A -> Id<A>` just becomes the identity function. The `map` method 
from `Functor` just becomes function application

```kotlin:ank
val id: Id<Int> = Id.pure(3)
id.map{it + 3}
```


