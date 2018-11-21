---
layout: docs
title: Show
permalink: /docs/typeclasses/show/
---

## Show

{:.beginner}
beginner

The `Show` typeclass abstracts the ability to obtain a `String` representation of any object.

It can be considered the typeclass equivalent of Java's `Object#toString`.

```kotlin:ank
import arrow.*
import arrow.instances.*

ForInt extensions { 1.show() }
```

### Main Combinators

#### F#show

Given an instance of `F` it returns the `String` representation of this instance.

`fun F.show(): String`

### Laws

Arrow provides `ShowLaws` in the form of test cases for internal verification of lawful instances and third party apps creating their own `Show` instances.

#### Creating your own `Show` instances

Show provides one special instance that can be potentially applicable to most datatypes.
It uses kotlin's `toString` method to get an object's literal representation.
This will work well in many cases, specially for data classes.

```kotlin:ank
import arrow.core.*
import arrow.typeclasses.*

// Option is a data class with a single value
Show.any().run { Option.just(1).show() }
```

```kotlin:ank
// using invoke constructor
class Person(val firstName: String, val lastName: String)
val personShow = Show<Person> { "Hello $firstName $lastName" }
```

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }}) to provide your own `Show` instances for custom datatypes.


### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Show

TypeClass(Show::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.Show)