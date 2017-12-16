---
layout: docs
title: MonadSuspend
permalink: /docs/effects/monadsuspend/
---

## MonadRun

### Laws

Kategory provides [`MonadSuspendLaws`]({{ '/docs/typeclasses/laws#monadsuspendlaws' | relative_url }}) in the form of test cases for internal verification of lawful instances and third party apps creating their own `MonadSuspendLaws` instances.

### Data types

The following datatypes in Kategory provide instances that adhere to the `AsyncContext` typeclass.

- [IO]({{ '/docs/effects/io' | relative_url }})
