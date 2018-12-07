---
layout: docs
title: Eq
permalink: /docs/arrow/typeclasses/decidable/
redirect_from:
  - /docs/typeclasses/decidable
---

## Divide/Divisible/Decidable

{:.beginner}

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }}) to provide your own `Decidable` instances for custom datatypes.

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Decidable

TypeClass(Decidable::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.Decidable)