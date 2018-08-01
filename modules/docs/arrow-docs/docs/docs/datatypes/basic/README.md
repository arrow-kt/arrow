---
layout: docs
title: Basic Types
permalink: /docs/datatypes/basic/
---

## Basic Types

{:.beginner}
beginner

Arrow provides [typeclass]({{ '/docs/patterns/glossary/' | relative_url }}) instances for several platform types.
These instances are available in the module `arrow-instances`.

### Numbers

- [`Show`]({{ '/docs/typeclasses/show/' | relative_url }})

- [`Eq`]({{ '/docs/typeclasses/eq/' | relative_url }})

- [`Order`]({{ '/docs/typeclasses/order/' | relative_url }})

- [`Semigroup`]({{ '/docs/typeclasses/semigroup/' | relative_url }})

- [`Monoid`]({{ '/docs/typeclasses/monoid/' | relative_url }})

### String

- [`Show`]({{ '/docs/typeclasses/show/' | relative_url }})

- [`Eq`]({{ '/docs/typeclasses/eq/' | relative_url }})

- [`Order`]({{ '/docs/typeclasses/order/' | relative_url }})

- [`Semigroup`]({{ '/docs/typeclasses/semigroup/' | relative_url }})

- [`Monoid`]({{ '/docs/typeclasses/monoid/' | relative_url }})

- [`FilterIndex`]({{ '/docs/optics/filterindex/' | relative_url }})

- [`Index`]({{ '/docs/optics/index/' | relative_url }})

### Boolean

Note that because Boolean doesn't have a companion object you'll find these in `BooleanInstances`.

- [`Show`]({{ '/docs/typeclasses/show/' | relative_url }})

- [`Eq`]({{ '/docs/typeclasses/eq/' | relative_url }})

### Char

- [`Show`]({{ '/docs/typeclasses/show/' | relative_url }})

- [`Eq`]({{ '/docs/typeclasses/eq/' | relative_url }})

- [`Order`]({{ '/docs/typeclasses/order/' | relative_url }})
