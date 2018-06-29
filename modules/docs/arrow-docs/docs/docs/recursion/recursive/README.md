---
layout: docs
title: Recursive
permalink: /docs/recursion/recursive/
---

## Recursive

{:.advanced}
advanced

The recursive typeclass abstracts out the ability to recursively fold a structure.
See [Intro to Recursion Schemes]({{ '/docs/recursion/intro/' | relative_url }}) for
an introduction to how it works.

### Main Combinators

#### Functor<F>#project

Given a Functor `F`, creates a Coalgebra which expands an `F` into a `Kind<T, F>`.

`fun <F> Functor<F>.project(): Coalgebra<F, Kind<T, F>>`

#### Functor<F>#projectT

The implementation for project; takes a `Kind<T, F>` and returns a `Kind<F, Kind<T, F>>`.

`fun <F> Functor<F>.projectT(tf: Kind<T, F>): Kind<F, Kind<T, F>>`

#### Functor<F>#cata

Fold generalized over any Recursive `T` for any Functor `F` (wrapped in an `Eval` for
stack safety).

`fun <F, A> Functor<F>.cata(tf: Kind<T, F>, alg: Algebra<F, Eval<A>>): A`

### Laws

Arrow provides `RecursiveLaws` in the form of test cases for internal verification of 
lawful instances and third party apps creating their own `Recursive` instances.

### Data Types

Arrow provides three datatypes that are instances of `Recursive`, each modeling a
different way of defining recursion.

- [Fix]({{ 'docs/recursion/fix' | relative_url }})
- [Mu]({{ 'docs/recursion/mu' | relative_url }})
- [Nu]({{ 'docs/recursion/nu' | relative_url }})
