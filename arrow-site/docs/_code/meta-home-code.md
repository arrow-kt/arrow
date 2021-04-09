---
library: meta
---
```kotlin
import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.quotes.Transform
import arrow.meta.quotes.namedFunction

//sampleStart

val Meta.helloWorld: CliPlugin get() =
  "Hello World" {
    meta(
      namedFunction(this, { element.name == "helloWorld" }) { (c, _) -> // <-- namedFunction(...) {...}
        Transform.replace(
          replacing = c,
          newDeclaration = """|fun helloWorld(): Unit =
                              |  println("Hello ΛRROW Meta!")
                              |""".function(descriptor).syntheticScope
        )
      }
    )
  }
//sampleEnd
```
