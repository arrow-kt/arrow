---
layout: docs
title: MonadRun
permalink: /docs/effects/monadrun/
---

## MonadRun

### Laws

Kategory provides [`MonadRunLaws`]({{ '/docs/typeclasses/laws#monadrunlaws' | relative_url }}) in the form of test cases for internal verification of lawful instances and third party apps creating their own `MonadRunLaws` instances.

### Data types

The following datatypes in Kategory provide instances that adhere to the `AsyncContext` typeclass.

- [IO]({{ '/docs/effects/io' | relative_url }})
