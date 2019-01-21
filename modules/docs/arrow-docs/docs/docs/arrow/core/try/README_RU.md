---
layout: docs
title: Try
permalink: /docs/arrow/core/try/ru
redirect_from:
  - /docs/datatypes/try/ru
video: XavztYVMUqI
---

## Try
[English](/docs/arrow/core/try)
{:.beginner}
beginner

В Arrow [есть множество способов для обработки ошибок](http://arrow-kt.io/docs/patterns/error_handling/), что позволяет выбрать оптимальную стратегию для любой ситуации.

Например, существует `Option` для моделирования отсутствия значения, или `Either` для моделирования возвращенного функцией типа, который содержит в себе информацию о том завершилась ли функция исключением или вернула значение.

С другой стороны есть `Try` который представляет собой функцию, которая могла привести к `A` как результату (в случае, если выполнение закончилось успехом) или бросить исключение, если что-то пошло не так.

Таким образом `Try` представлен в виде двух классов: инстанс `Try` в котором операция заверилась успехом, представленный как `Success<A>`; или инстанс `Try` который представляет функцию, завершившуюся неудачей, представленный как `Failure`.

После этого объяснения можно подумать, что речь идет о `Either<Throwable, A>`, и это будет недалеко от правды. `Try` может быть имплементирован как `Either`, но их сферы применения очень разнятся.

Если нам известно, что выполнение функции может завершиться исключением (например, если мы используем код из библиотеки над которым у нас нет контроля, или метод из непосредственно языка программирования), мы можем использовать `Try` как замену `try-catch`.

Нижеприведенный пример представляет себе типичный Java код, в котором ошибки бизнес-логики представлены как исключения.

```kotlin:ank:silent
open class GeneralException: Exception()

class NoConnectionException: GeneralException()

class AuthorizationException: GeneralException()

fun checkPermissions() {
    throw AuthorizationException()
}

fun getLotteryNumbersFromCloud(): List<String> {
    throw NoConnectionException()
}

fun getLotteryNumbers(): List<String> {
    checkPermissions()

    return getLotteryNumbersFromCloud()
}
```

Традиционным способом обработки ошибки будет использования блока `try-catch`:

```kotlin:ank
try {
    getLotteryNumbers()
} catch (e: NoConnectionException) {
    //...
} catch (e: AuthorizationException) {
    //...
}
```

Но также мы можем использовать `Try` для получения результата функции, что позволит намного улучшить читаемость:

```kotlin:ank
import arrow.*
import arrow.core.*

val lotteryTry = Try { getLotteryNumbers() }
lotteryTry
```

Используя `getOrDefault` мы можем присвоить результату функции значение по-умолчанию, которое будет использовано, если выполнение функции завершится неудачей:

```kotlin:ank
lotteryTry.getOrDefault { emptyList() }
```
Если причина исключения имеет значение для определения дефолтного значения которое будет возвращено из функции, можно использовать `getOrElse`:

```kotlin:ank
lotteryTry.getOrElse { ex: Throwable -> emptyList() }
```

`getOrElse` также можно использовать и без обработки причины исключения (например, вместо `getOrDefault`):

```kotlin:ank
lotteryTry.getOrElse { emptyList() }
```

Если требуется проверка только случая успешного выполнения, можно использовать `filter` для конвертации результата успешного выполнения функции в исключение, если не выполняется требование предиката:

```kotlin:ank
lotteryTry.filter {
    it.size < 4
}
```

Мы также можем использовать `recover`, что позволит нам восстановить ход выполнения фукнции после получения ошибки (мы получим информацию о ошибке и вернем новое значение):

```kotlin:ank
lotteryTry.recover { exception ->
    emptyList()
}
```

`recoverWith` может быть использована для восстановления после ошибки с помощью другой функции (всё то же самое, что с `recover`, только возвращается не значение а новый `Try`):

```kotlin:ank
enum class Source {
    CACHE, NETWORK
}

fun getLotteryNumbers(source: Source): List<String> {
    checkPermissions()
    return getLotteryNumbersFromCloud()
}

Try { getLotteryNumbers(Source.NETWORK) }.recoverWith {
    Try { getLotteryNumbers(Source.CACHE) }
}
```

Если требуется написать обработку как для успешного выполнения, так и для ошибки, можно использовать `fold`. Для реализации `fold` требуется предоставить две функции, одна из которых трансформирует неудачное завершение в новое значение, вторая же обрабатывает полученные в случае успеха данные:

```kotlin:ank
lotteryTry.fold(
    { emptyList<String>() },
    { it.filter { it.toIntOrNull() != null } })
```

При использовании `Try` полученный инстанс `Try<Throwable, DomainObject>` принято конвертировать в инстанс `Either<DomainError, DomainObject>`. Этого можно достичь с помощью `toEither`, с последующим вызовом `mapLeft`:

```kotlin
sealed class DomainError(val message: String, val cause: Throwable) {
    class GeneralError(message: String, cause: Throwable) : DomainError(message, cause)
    class NoConnectionError(message: String, cause: Throwable) : DomainError(message, cause)
    class AuthorizationError(message: String, cause: Throwable) : DomainError(message, cause)
}

Try {
    getLotteryNumbersFromCloud()
}.toEither()
    .mapLeft {
        DomainError.NoConnectionError("Failed to fetch lottery numbers from cloud", it)
    }
// Left(a=DomainError$NoConnectionError@3ada9e37)
```

В заключение, в Arrow есть инстансы `Try` для многих полезных классов типа, позволяющие использовать и трансформировать значения:

[`Functor`]({{ '/docs/arrow/typeclasses/functor/' | relative_url }})

Трансформация значения в случае, если функция завершилась успешно:

```kotlin:ank
import arrow.typeclasses.*
import arrow.core.extensions.*
import arrow.core.extensions.`try`.functor.*

Try { "3".toInt() }.map { it + 1 }
```

[`Applicative`]({{ '/docs/arrow/typeclasses/applicative/' | relative_url }})

Вычисление с использованием независимых друг от друга значений:

```kotlin:ank
import arrow.core.extensions.`try`.applicative.tupled
  
tupled(Try { "3".toInt() }, Try { "5".toInt() }, Try { "nope".toInt() })
```

[`Monad`]({{ '/docs/arrow/typeclasses/monad/' | relative_url }})

Вычисление с использованием зависимых друг от друга значений, выполнение которого подразумевает возможность ошибки:

```kotlin:ank
import arrow.core.extensions.`try`.monad.binding

binding {
  val a = Try { "3".toInt() }.bind()
  val b = Try { "4".toInt() }.bind()
  val c = Try { "5".toInt() }.bind()
  a + b + c
}
```

```kotlin:ank
binding {
  val a = Try { "none".toInt() }.bind()
  val b = Try { "4".toInt() }.bind()
  val c = Try { "5".toInt() }.bind()
  a + b + c
}
```

Вычисление с использованием зависимых друг от друга значений, которые автоматически возводятся в контекст `Try`:

```kotlin:ank
import arrow.core.extensions.`try`.monadThrow.bindingCatch

bindingCatch {
  val a = "none".toInt()
  val b = "4".toInt()
  val c = "5".toInt()
  a + b + c
}
```

### Поддерживаемые классы типов

```kotlin:ank:replace
import arrow.reflect.*
import arrow.data.*
import arrow.core.*

DataType(Try::class).tcMarkdownList()
```
