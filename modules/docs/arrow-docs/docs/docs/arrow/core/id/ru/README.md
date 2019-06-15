---
layout: docs
title: Id
permalink: /docs/arrow/core/id/ru/
redirect_from:
  - /docs/datatypes/id/ru/
video: DBvVd1pfLMo
---

## Id

{:.beginner}
beginner

[English](/docs/apidocs/arrow-core-data/arrow.core/-id/)

Id (монада идентичности) должна рассматриваться, как монада, в которую вкладывается эффект отсутствия эффекта. Любое значение может являться значением `Id`.

```kotlin:ank
import arrow.*
import arrow.core.*

Id("привет")
```

Используя данную декларацию мы можем обращаться с нашим конструктором типа `Id` как с `Монадой` и как с `Комонадой`.
Метод `just`, который имеет тип `A -> Id<A>` просто становится функцией, переводящей аргумент в себя. Метод `map` от функтора просто становиться применением функции.

```kotlin:ank
val id: Id<Int> = Id.just(3)
id.map { it + 3 }
```

### Поддерживаемые классы типа

```kotlin:ank:replace
import arrow.reflect.*
import arrow.core.*

DataType(Id::class).tcMarkdownList()
```
