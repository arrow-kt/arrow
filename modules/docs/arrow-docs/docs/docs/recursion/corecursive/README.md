---
layout: docs
title: Corecursive
permalink: /docs/recursion/corecursive/
---

## Corecursive

{:.advanced}
advanced

The corecursive typeclass abstracts out the ability to corecursively unfold a structure.
See [Intro to Recursion Schemes]({{ '/docs/recursion/intro/' | relative_url }}) for
an introduction to how it works.

### Main Combinators

#### Functor<F>#embed

Given a Functor `F`, creates a Algebra which collapses a `Kind<T, F>` into a `F`
(wrapped in an `Eval` for stack safety).

`fun <F> Functor<F>.embed(): Algebra<F, Eval<Kind<T, F>>>`

#### Functor<F>#embedT

The implementation for embed; takes a `Kind<F, Kind<T, F>>` and returns a `Kind<T, F>`
(wrapped in an `Eval` for stack safety).

`fun <F> Functor<F>.embedT(tf: Kind<F, Eval<Kind<T, F>>>): Eval<Kind<T, F>>`

#### Functor<F>#ana

Unfold generalized over any Corecursive `T` for any Functor `F`.

`fun <F, A> Functor<F>.ana(a: A, coalg: Coalgebra<F, A>): Kind<T, F>`

### Laws

Arrow provides `CorecursiveLaws` in the form of test cases for internal verification of
lawful instances and third party apps creating their own `Corecursive` instances.

### Data Types

Arrow provides three datatypes that are instances of `Corecursive`, each modeling a
different way of defining corecursion.

- [Fix]({{ 'docs/recursion/fix' | relative_url }})
- [Mu]({{ 'docs/recursion/mu' | relative_url }})
- [Nu]({{ 'docs/recursion/nu' | relative_url }})
