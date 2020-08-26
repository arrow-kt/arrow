# Dependency configurations

| Configuration | Use | Note |
| ------------- | --- | ---- |
| `api` | compilation | exported to consumers for compilation |
| `implementation` | compilation + runtime | exported to consumers for runtime! | 
| `compileOnly` | just compilation | not exported to consumers | 
| `runtimeOnly` | just runtime | exported to consumers for runtime! | 
| `testImplementation` | test compilation + test runtime |  | 
| `testCompileOnly` | test compilation |  | 
| `testRuntimeOnly` | test runtime |  | 
