---
layout: docs
title: NonEmptyList
permalink: /docs/datatypes/nonemptylist/
---

## NonEmptyList

NonEmptyList or Nel, is a list that, as the name suggests, is guaranteed to have at least one element.

Let's explore it more in depth with a custom validation example:

### A Validation example

Suppose that we have some data to validate, let's say an email. We could implement a (naive) validation function like the following:

```kotlin:ank:silent
import kategory.*

typealias EmailValidationResult = Validated<String, Unit>

fun validateEmail(email: String): EmailValidationResult {
    fun isEmailValid(email: String): EmailValidationResult = when {
        !email.contains('@') -> "Email doesn't contain @".invalid()
        email.indexOf('@') == 0 -> "Email doesn't contain local-part".invalid()
        else -> email.split('@').filter { !it.isBlank() }.let<List<String>, Validated<String, Unit>> { emailParts ->
            when {
                emailParts.size < 2 -> "Email doesn't contain domain".invalid()
                emailParts.size > 2 -> "Email cannot contain more than one @".invalid()
                emailParts[1].contains('.')
                        && emailParts[1].indexOf('.') == 0 -> "Email domain should have a name before dot".invalid()
                else -> Unit.valid()
            }
        }
    }

    return isEmailValid(email)
}
```

This function will validate each email and let us act on each of the Valid/Invalid cases. But what if we want to validate a bunch of them?

```kotlin:ank
    val emails = listOf(
            "hello@bye.com",
            "@nolocalpart.com",
            "nodomain@",
            "onlypartdomain@bye",
            "onlyotherpartdomain@.com",
            "no.at.com"
    )
            
    emails.map { validateEmail(it) }
            .reduce { acc, validated -> acc.combine(validated) }
```

As you can see we just obtain an Invalid case with all the errors combined, but they're not readable at all.

### Validation with Nel

NonEmptyList is highly connected with Validation, as it provides a nice and simple way of accumulating values for both the Valid or Invalid case. Let's see how it combines with it following the previous example:

```kotlin:ank:silent
import kategory.*

typealias EmailValidation = Nel<String>
typealias EmailValidationResult = Validated<String, Unit>

fun validateEmail(email: String): EmailValidation {
    //(...)

    return isEmailValid(email)
            .fold({
                "$email is not valid, reason: $it".nel()
            }, {
                "$email is a valid email".nel()
            })
}
```

Now trying to validate the same list of emails:

```kotlin:ank
    emails.map { validateEmail(it) }
            .reduce(EmailValidation::plus)
```

This is fine as now we have a proper message including the email, but we still can do better, let's say we want an output of one email per line, we could simply use Nel's `all` function and join with a line return:

```kotlin:ank
    emails.map { validateEmail(it) }
            .reduce(EmailValidation::plus)
            .all
            .joinToString(separator = "\n")
```

## Instances

```kotlin:ank
import kategory.debug.*

showInstances<NonEmptyListHK, Unit>()
```