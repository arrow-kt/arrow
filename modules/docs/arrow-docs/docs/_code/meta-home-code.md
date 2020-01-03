---
library: meta
---
```kotlin
val Meta.comprehensions: Plugin
  get() =
    "comprehensions" {
      meta(
        quote(KtDotQualifiedExpression::containsFxBlock) { fxExpression ->
          Transform.replace(
            replacing = fxExpression,
            newDeclaration = toFlatMap(fxExpression).expression
          )
        }
      )
    }
//sampleStart


+ service1().flatMap { result1 ->
+   service2(result1).flatMap { result2 ->
+     service3(result2).map { result3 ->
+        Result(result3)
+     }
+   }
+ }
- val result1 by service1()
- val result2 by service2(result1)
- val result3 by service3(result2)
- Result(result3)
//sampleEnd
```
