---
layout: docs-core
title: Bimonad
permalink: /docs/arrow/typeclasses/bimonad/
redirect_from:
  - /docs/typeclasses/bimonad/
---

## Bimonad




A datatype that's both a Monad and a Comonad.

TODO. Meanwhile you can find a short description in the [intro to typeclasses]({{ '/docs/typeclasses/intro/' | relative_url }}).

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Bimonad

TypeClass(Bimonad::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.Bimonad)
