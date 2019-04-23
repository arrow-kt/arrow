---
layout: docs
title: Option
permalink: /docs/arrow/core/option/ru/
redirect_from:
  - /docs/datatypes/option/ru/

video: 5SFTbphderE
---

## Option

{:.beginner}
beginner

[English](/docs/arrow/core/option)

Если Вы работали с Java в прошлом, то скорее всего встречались с `NullPointerException` (прочие языки бросают ошибку с похожим названием). Обычно это происходит из-за того, что какой-то метод вернул `null`, когда это не ожидалось, и поэтому обработка этого значения так и не попала в код. Значение `null` также часто используется для обозначения отсутствия опционального значения.
Котлин пытается бороться с этой проблемой путем полного избавления от `null`-значений через специальный [null-безопасный синтаксис,  основанный на `?`](https://kotlinlang.org/docs/reference/null-safety.html).

Arrow моделирует отсутствие значений через тип данных `Option`, схожим образом используемый в таких языках, как Scala, Haskell и прочих функциональных языках.

`Option<A>` это контейнер для опционального значения типа `A`. Если значение типа `A` присутствует, `Option<A>` является инстансом `Some<A>`, содержащим в себе значение типа `A`. Если значение отсутствует, `Option<A>` является объектом `None`.

```kotlin:ank
import arrow.*
import arrow.core.*

val someValue: Option<String> = Some("Значение в обёртке")
someValue
```

```kotlin:ank
val emptyValue: Option<String> = None
emptyValue
```

Давайте напишем функцию, которая может вернуть или не вернуть строковое значение. Другими словами — функцию, которая возвращает `Option<String>`:

```kotlin:ank:silent
fun maybeItWillReturnSomething(flag: Boolean): Option<String> =
   if (flag) Some("Найдено значение") else None
```

Используя `getOrElse` мы можем предоставить значение `"Нет значения"`, если опциональный аргумент `None` не существует:

```kotlin:ank:silent
val value1 = maybeItWillReturnSomething(true)
val value2 = maybeItWillReturnSomething(false)
```

```kotlin:ank
value1.getOrElse { "Нет значения" }
```

```kotlin:ank
value2.getOrElse { "Нет значения" }
```

Создание `Option<T>` от `T?` для работы со значениями, которые могут быть `null`:

```kotlin:ank
val myString: String? = "Строка, которая может быть null"
val option: Option<String> = Option.fromNullable(myString)
```

Проверка того, есть ли значение в `Option`:

```kotlin:ank
value1 is None
```

```kotlin:ank
value2 is None
```

`Option` может быть использована в условном выражении `when`:

```kotlin:ank
val someValue: Option<Double> = Some(20.0)
val value = when(someValue) {
   is Some -> someValue.t
   is None -> 0.0
}
value
```

```kotlin:ank
val noValue: Option<Double> = None
val value = when(noValue) {
   is Some -> noValue.t
   is None -> 0.0
}
value
```

Альтернативой сравнения с образцом (паттерн-матчинг) служит выполнение операций вида Functor/Foldable. Это становится возможным благодаря тому, что `Option` может восприниматься как коллекция, или же как foldable структура с одним элементом (или пустая).

Одна из этих операций — `map`. Эта операция позволяет привести внутреннее значение к новому типу, который будет сохранен в `Option`:

```kotlin:ank:silent
val number: Option<Int> = Some(3)
val noNumber: Option<Int> = None
val mappedResult1 = number.map { it * 1.5 }
val mappedResult2 = noNumber.map { it * 1.5 }
```

```kotlin:ank
mappedResult1
```

```kotlin:ank
mappedResult2
```

Другая операция - `fold`. Эта операция извлечет значение из `Option`, или предоставит дефолтное, если значение является `None`.

```kotlin:ank
number.fold({ 1 }, { it * 3 })
```

```kotlin:ank
noNumber.fold({ 1 }, { it * 3 })
```

В Arrow также добавлен синтаксис поднятия любого типа данных в контекст `Option`, если это необходимо. 


```kotlin:ank
1.some()
```

```kotlin:ank
none<String>()
```

```kotlin:ank
val nullableValue: String? = null
nullableValue.toOption()
```

```kotlin:ank
val nullableValue: String? = "Привет"
nullableValue.toOption()
```

Также доступны extension-функции для `Iterable`, что позволяет работать с данными и при этом избежать обработки `null`-значений (`firstOrNull()`).

```kotlin:ank:silent
val myList: List<Int> = listOf(1,2,3,4)
```

```kotlin:ank
myList.firstOrNone { it == 4 }
```

```kotlin:ank
myList.firstOrNone { it == 5 }
```

Пример применения

```
fun foo() {
    val foxMap = mapOf(1 to "The", 2 to "Quick", 3 to "Brown", 4 to "Fox")

    val ugly = foxMap.entries.firstOrNull { it.key == 5 }?.value.let { it?.toCharArray() }.toOption()
    val pretty = foxMap.entries.firstOrNone { it.key == 5 }.map { it.value.toCharArray() }

    //Do something with pretty Option
}
```

В Arrow есть инстансы `Option` для множества удобных классов типа, которые позволяют использовать и трансформировать опциональные значения

[`Функтор`]({{ '/docs/arrow/typeclasses/functor/' | relative_url }})

Трансформация вложенного значения

```kotlin:ank
import arrow.typeclasses.*
import arrow.core.extensions.option.functor.*

Some(1).map { it + 1 }
```

[`Аппликатив`]({{ '/docs/arrow/typeclasses/applicative/' | relative_url }})

Вычисление с использованиеми независимых друг от друга значений

```kotlin:ank
import arrow.core.extensions.option.applicative.*

tupled(Some(1), Some("Hello"), Some(20.0))
```

[`Монада`]({{ '/docs/arrow/typeclasses/monad/' | relative_url }})

Вычисление с использованием зависимых значений, за исключением случаев их отстутствия

```kotlin:ank
import arrow.core.extensions.option.monad.binding

binding {
  val (a) = Some(1)
  val (b) = Some(1 + a)
  val (c) = Some(1 + b)
  a + b + c
}
```

```kotlin:ank
binding {
  val (x) = none<Int>()
  val (y) = Some(1 + x)
  val (z) = Some(1 + y)
  x + y + z
}
```

### Поддерживаемые классы типа

```kotlin:ank:replace
import arrow.reflect.*
import arrow.data.*
import arrow.core.*

DataType(Option::class).tcMarkdownList()
```

## Благодарность

Содержание частично адаптировано из [Scala Exercises Option Tutorial](https://www.scala-exercises.org/std_lib/options)
Изначально основано на Scala Koans.
