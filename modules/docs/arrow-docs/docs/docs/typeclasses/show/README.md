---
layout: docs
title: Show
permalink: /docs/typeclasses/show/
---

## Show

The `Show` typeclass abstracts the ability to obtain a `String` representation of any object.

It can be considered the typeclass equivalent of Java's `Object#toString`.

```kotlin:ank
import arrow.*
import arrow.instances.*

Int.show().run { 1.show() }
```

### Main Combinators

#### F#show

Given an instance of `F` it returns the `String` representation of this instance.

`fun F.show(): String`

### Laws

Arrow provides [`ShowLaws`]({{ '/docs/typeclasses/laws#showlaws' | relative_url }}) in the form of test cases for internal verification of lawful instances and third party apps creating their own `Show` instances.

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

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }}) to provide your own `Show` instances for custom datatypes.


### Data Types

The following data types in Arrow provide instances that adhere to the `Show` type class.

- [Either]({{ '/docs/datatypes/either' | relative_url }})
- [Id]({{ '/docs/datatypes/id' | relative_url }})
- [Ior]({{ '/docs/datatypes/ior' | relative_url }})
- [NonEmptyList]({{ '/docs/datatypes/nonemptylist' | relative_url }})
- [Option]({{ '/docs/datatypes/option' | relative_url }})
- [SequenceK]({{ '/docs/datatypes/sequencek' | relative_url }})
- [SetK]({{ '/docs/datatypes/setk' | relative_url }})
- [Try]({{ '/docs/datatypes/try' | relative_url }})
- [Validated]({{ '/docs/datatypes/validated' | relative_url }})
