---
layout: docs-core
title: Tags
permalink: /docs/arrow/core/tags/
redirect_from:
  - /docs/datatypes/tags/
video: q6HpChSq-xc
---

# Header h1
## Header h2
### Header h3
#### Header h4
##### Header h5
###### Header h6

---

### Paragraph

Lorem ipsum dolor sit amet, **consectetur adipiscing** elit. Etiam eu *sem suscipit*, malesuada arcu quis, semper arcu. Nullam sit amet ligula orci. Sed venenatis tellus vehicula metus tincidunt, nec sagittis arcu gravida. Aliquam ~~mattis vitae nibh~~ at aliquam. Duis quis [accumsan](#) massa. Curabitur semper dolor vel sem venenatis, vitae semper urna condimentum. Morbi maximus ultrices orci, sit amet lacinia ex sodales ac. Donec sed ante diam. Sed malesuada dapibus ipsum, quis consectetur nulla interdum et. Aliquam vestibulum est ut erat blandit lacinia. Praesent sodales dui ac hendrerit eleifend. Proin dolor sem, auctor vitae dolor id, accumsan finibus nibh. Phasellus tincidunt vulputate pellentesque. Fusce nisi erat, iaculis sed efficitur a, maximus ut ipsum.

### List

* Item
* Item
- Item
- Item

---

1. Item
2. Item
3. Item
4. Item

---

1. Item
2. Item
   * Text
   * Text  
3. Item
4. Item

---

### Images

![Alt](/img/core/arrow-core-brand-sidebar.svg "Icon")

### Code

Lorem ipsum dolor sit amet, `consectetur adipiscing` elit. Etiam eu *sem suscipit*, malesuada arcu quis, semper arcu. Nullam sit amet ligula

```kotlin:ank:replace
import arrow.reflect.*
import arrow.data.*
import arrow.core.*

DataType(Function1::class).tcMarkdownList()
```

```kotlin:ank
import arrow.*
import arrow.core.*

val throwsSomeStuff: (Int) -> Double = {x -> x.toDouble()}
val throwsOtherThings: (Double) -> String = {x -> x.toString()}
val moreThrowing: (String) -> List<String> = {x -> listOf(x)}
val magic = throwsSomeStuff.andThen(throwsOtherThings).andThen(moreThrowing)
magic
```