---
layout: docs
title: Birecursive
permalink: /docs/recursion/birecursive/
---

## Birecursive

{:.advanced}
advanced

A datatype that's both `Recursive` and `Corecursive`, which enables applying both `fold` and `unfold`
operations to it.

### Data Types

Arrow provides three datatypes that are instances of `Birecursive`, each modeling a
different way of defining birecursion.

- [Fix]({{ 'docs/recursion/fix' | relative_url }})
- [Mu]({{ 'docs/recursion/mu' | relative_url }})
- [Nu]({{ 'docs/recursion/nu' | relative_url }})
