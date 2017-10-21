---
layout: docs
title: Async
permalink: /docs/effects/async/
---

## Async

Being able to run code in a different thread than the current one implies that, even if it's part of a sequence, the code will have to be asynchronous.
Running asynchronous code always requires a callback after completion on error capable of returning to the current thread.
The same way the typeclass `Monad` represents a sequence of events, and `MonadError` a sequence that can fail, the typeclass `Async` represents asynchronous code with a callback.
