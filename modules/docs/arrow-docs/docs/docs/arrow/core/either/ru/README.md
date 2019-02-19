---
layout: docs
title: Either
permalink: /docs/arrow/core/either/ru/
redirect_from:
  - /docs/datatypes/either/ru/
video: q6HpChSq-xc
---

## Either

{:.beginner}
beginner

[English](/docs/arrow/core/either/)

Программируя мы часто пишем функции, которые могут завершиться неудачей.
Например, сетевой запрос может прерваться из-за проблем c соединением, или из сервиса вернется JSON, который мы не готовы спарсить.

Для того, чтобы сообщить о подобных ошибках принято бросать исключения. Однако у этого подхода есть ряд недостатков: 
 - исключения не отслеживаются компилятором, поэтому для того, чтобы посмотреть какие исключения может бросить функция приходится копаться в коде
 - для того, чтобы обработать эти исключения мы обязаны поймать их из вызывающей функции. 

Всё это приводит к чрезмерной сложности, особенно, если требуется описать процедуру бросания исключений композиционно:

```kotlin:ank
import arrow.*
import arrow.core.*

val throwsSomeStuff: (Int) -> Double = {x -> x.toDouble()}
val throwsOtherThings: (Double) -> String = {x -> x.toString()}
val moreThrowing: (String) -> List<String> = {x -> listOf(x)}
val magic = throwsSomeStuff.andThen(throwsOtherThings).andThen(moreThrowing)
magic
```

Допустим, в вышепримеденном коде каждая из функций может бросить исключение. Смотря на сигнатуры этих функций мы не знаем, какие исключения могут быть ими брошены. А учитывая, что функции скомпонованы, исключение может быть брошено на любом этапе исполнения программы; плюс две функции могут бросить исключения одного и того же типа — что значительно усложнит отладку.

Как же нам сообщить об ошибке? Сообщив о её возможности в типе данных, который мы возвращаем.

## Either вместо Validated

Обычно для аккумуляции ошибок используетс тип`Validated`, а `Either` используется для того, чтобы завершить исполнение алгоритма после получения первой ошибки.

Правая сторона `Either` содержит в себе значение, возвращенное фукнцией в случае успешного завершения.

```kotlin:ank
val right: Either<String, Int> = Either.Right(5)
right
```

```kotlin:ank
val left: Either<String, Int> = Either.Left("Что-то пошло не так")
left
```
Т.к. `Either` правосторонний тип данных, для нее можно определить инстанс монады.

Поскольку мы хотим, чтобы вычисление продолжилось только в случае `Right`, мы обозначаем левый тип и оставляем правый свободным.

Поэтому методы map и flatMap правосторонние:

```kotlin:ank
val right: Either<String, Int> = Either.Right(5)
right.flatMap{Either.Right(it + 1)}
```

```kotlin:ank
val left: Either<String, Int> = Either.Left("Что-то пошло не так")
left.flatMap{Either.Right(it + 1)}
```

## Используя Either вместо исключений

В качестве примера рассмотрим серию функций, которая:

* Парсит строковое значение в числовое
* Вычисляет обратную величину
* Конвертирует обратную величину в строковое значение

Если бы мы использовали код, который бросает исключения, мы бы написали что-то вроде:

```kotlin:ank:silent
// Exception Style

fun parse(s: String): Int =
    if (s.matches(Regex("-?[0-9]+"))) s.toInt()
    else throw NumberFormatException("$s не числовое значение.")

fun reciprocal(i: Int): Double =
    if (i == 0) throw IllegalArgumentException("Нельзя вычислить обратную величину от 0.")
    else 1.0 / i

fun stringify(d: Double): String = d.toString()
```

Вместо этого попробуем обозначить возможность неудачного завершения функции в её типе.

```kotlin:ank:silent
// Either Style

fun parse(s: String): Either<NumberFormatException, Int> =
    if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
    else Either.Left(NumberFormatException("$s не числовое значение."))

fun reciprocal(i: Int): Either<IllegalArgumentException, Double> =
    if (i == 0) Either.Left(IllegalArgumentException("Нельзя вычислить обратную величину от 0."))
    else Either.Right(1.0 / i)

fun stringify(d: Double): String = d.toString()

fun magic(s: String): Either<Exception, String> =
    parse(s).flatMap{reciprocal(it)}.map{stringify(it)}

```

Теперь вызов `parse` вернет значения `Left` и `Right`.

```kotlin:ank
parse("Не числовое значение")
```

```kotlin:ank
parse("2")
```

Используя комбинаторы вроде `flatMap` и `map`, мы можем скомпоновать функции вместе.

```kotlin:ank
magic("0")
```

```kotlin:ank
magic("1")
```

```kotlin:ank
magic("Не числовое значение")
```

В следующем примере мы обработаем все значения `Either`, возвращаемые методом `magic` с помощью паттерн-матчинга.
Обратите внимание на обработку `Left` в `when` - компилятор заругается, если не прописать случай `else`, т.к. значет, что тип `Either[Exception, String]` может вернуть что-то кроме `NumberFormatException` или `IllegalArgumentException`. Также заметим, что для доступа к значениям `Left` и `Right` используется [Smart Cast](https://kotlinlang.org/docs/reference/typecasts.html#smart-casts).

```kotlin:ank
val x = magic("2")
val value = when(x) {
    is Either.Left -> when (x.a) {
        is NumberFormatException -> "Не числовое значение!"
        is IllegalArgumentException -> "Нельзя вычислить обратную величину от 0"
        else -> "Неизвестная ошибка"
    }
    is Either.Right -> "Полученная обратная величина: ${x.b}"
}
value
```

Вместо использования исключений как значения ошибки, попробуем перечислить всё, что может пойти не так при исполнении нашей программы.

```kotlin
// Either with ADT Style

sealed class Error {
    object NotANumber : Error()
    object NoZeroReciprocal : Error()
}

fun parse(s: String): Either<Error, Int> =
        if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
        else Either.Left(Error.NotANumber)

fun reciprocal(i: Int): Either<Error, Double> =
        if (i == 0) Either.Left(Error.NoZeroReciprocal)
        else Either.Right(1.0 / i)

fun stringify(d: Double): String = d.toString()

fun magic(s: String): Either<Error, String> =
        parse(s).flatMap{reciprocal(it)}.map{stringify(it)}
```

Все ошибки, которые могут возникнуть, перечислены в классе Error. Теперь, вместо использования класса исключения как значения ошибки, мы будем использовать один из подклассов Error. Это позволит нам не описывать случай `else` при обработке ошибки с помощью паттерн-матчинга. Более того, из-за того, что класс Error — изолированный (sealed), никакой внешний код не может добавить подтип ошибки, который мы забудем обработать.

```kotlin
val x = magic("2")
when(x) {
    is Either.Left -> when (x.a) {
        is Error.NotANumber -> "Не числовое значение!"
        is Error.NoZeroReciprocal -> "Нельзя вычислить обратную величину от 0"
    }
    is Either.Right -> "Полученная обратная величина: ${x.b}"
}
```

## Синтаксис

`Either` также может обрабатывать левые значения с помощью `mapLeft`, который схож с `map`, но применяется только к левым инстансам.

```kotlin:ank
val r : Either<Int, Int> = Either.Right(7)
r.mapLeft {it + 1}
val l: Either<Int, Int> = Either.Left(7)
l.mapLeft {it + 1}
```

`Either<A, B>` можно трансформировать в `Either<B,A>` используя метод `swap()`.

```kotlin:ank
val r: Either<String, Int> = Either.Right(7)
r.swap()
```

Для оборачивания в `Either` различных видов данных можно использовать функции `left()`, `right()`, `contains()`, `getOrElse()` и `getOrHandle()`:

```kotlin:ank
7.right()
```

```kotlin:ank
"привет".left()
```

```kotlin:ank
val x = 7.right()
x.contains(7)
```

```kotlin:ank
val x = "привет".left()
x.getOrElse { 7 }
```

```kotlin:ank
val x = "привет".left()
x.getOrHandle { "$it мир!" }
```

Для создания инстанса `Either` на основе предиката используется метод `Either.cond()`:

```kotlin:ank
Either.cond(true, { 42 }, { "Ошибка" })
```

```kotlin:ank
Either.cond(false, { 42 }, { "Ошибка" })
```

Также существует операция `fold`. Эта операция получит значение из `Either`, или предоставит дефолтное, в случае `Left`

```kotlin:ank
val x : Either<Int, Int> = 7.right()
x.fold({ 1 }, { it + 3 })
```

```kotlin:ank
val y : Either<Int, Int> = 7.left()
y.fold({ 1 }, { it + 3 })
```

Операция `getOrHandle()` позволяет трансформировать значение `Either.Left` в `Either.Right` используя значение `Left`. Это может быть полезным, когда  необходимо привести значение к определенному типу (например, для дальнейшего использования в `fold()`) но не требуется обработка случая `Either.Right`.

Например, если мы хотим смапить `Either<Throwable, Int>` в подходящий код статуса HTTP:

```kotlin:ank
val r: Either<Throwable, Int> = Either.Left(NumberFormatException())
val httpStatusCode = r.getOrHandle {
	when(it) {
		is NumberFormatException -> 400
		else -> 500
	}
} // 400
```

Операция `leftIfNull` трансформирует null-значение от `Either.Right` в заданное значение `Either.Left`. 
Если значение не null, оно будет обёрнуто в `Either.Right` как не nullable (что очень удобно, т.к., позволяет избежать проверки на null в последующей цепочке вызовов).
Если же операция `leftIfNull` была вызвана на `Either.Left`, будет возвращен изначальный `Either.Left`.

Рассмотрим примеры ниже:

```kotlin:ank
Right(12).leftIfNull({ -1 })
```

```kotlin:ank
Right(null).leftIfNull({ -1 })
```

```kotlin:ank
 Left(12).leftIfNull({ -1 }) // 12
```

Еще одна полезная при работе с null значениями операция - `rightIfNotNull`.
Если значение null, то оно будет трансформировано в обозначенное `Either.Left`, а если оно не null, то обёрнуто в `Either.Right`.

Пример:

```kotlin:ank
"значение".rightIfNotNull { "значение для left" }
```

```kotlin:ank
null.rightIfNotNull { "значение для left" }
```


В Arrow есть инстансы `Either` для множества полезных классов типа, которые позволяют использовать и трансформировать значения.
`Option` и `Try` не требуют типизированного параметра с последующими функциями, но они используются для `Either.Left`.

 [`Функтор`]({{ '/docs/arrow/typeclasses/functor/' | relative_url }})

Трансормация внутренних значений

```kotlin:ank
import arrow.core.extensions.either.functor.*

Right(1).map { it + 1 }
```

 [`Аппликатив`]({{ '/docs/arrow/typeclasses/applicative/' | relative_url }})

Вычисление с использованиеми независимымых друг от друга значений.

```kotlin:ank
import arrow.core.extensions.either.applicative.*

tupled(Either.Right(1), Either.Right("a"), Either.Right(2.0))
```

 [`Монада`]({{ '/docs/arrow/typeclasses/monad/' | relative_url }})

Вычисление с использованием зависимых значений, за исключением случаев их отстутствия

```kotlin
import arrow.core.extensions.either.monad.*

binding {
  val a = Either.Right(1).bind()
  val b = Either.Right(1 + a).bind()
  val c = Either.Right(1 + b).bind()
  a + b + c
}
// Right(6)
```

### Поддерживаемые классы типа

```kotlin:ank:replace
import arrow.reflect.*
import arrow.data.*
import arrow.core.*

DataType(Either::class).tcMarkdownList()
```
