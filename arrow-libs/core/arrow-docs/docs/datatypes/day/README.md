---
layout: docs-core
title: Day
permalink: /arrow/ui/day/
---

## Day

When building user interfaces, it is common to have two screens side by side evolving their states independently. In order to implement this behavior, we can use `Day`.

`Day` is a [`comonadic`]({{ '/arrow/typeclasses/comonad' | relative_url }}) data structure that holds two `Comonads` and a rendering function for both states.
