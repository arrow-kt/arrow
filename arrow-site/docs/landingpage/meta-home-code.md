```kotlin
//sampleStart
val Meta.helloWorld: CliPlugin get() =
  "Hello World" {
    meta(
      namedFunction(this, { name == "helloWorld" }) { c ->  // <-- namedFunction(...) {...}
        Transform.replace(
          replacing = c,
          newDeclaration = """|fun helloWorld(): Unit =
                              |  println("Hello ΛRROW Meta!")
                              |""".function
        )
      }
    )
  }
//sampleEnd
```
