---
library: optics
---
```kotlin:ank:playground
package example
import arrow.optics.dsl.*

@optics data class Street(val number: Int, val name: String)
@optics data class Address(val city: String, val street: Street)
@optics data class Company(val name: String, val address: Address)
@optics data class Employee(val name: String, val company: Company?)

val john = Employee("John Doe",
    Company("Kategory",
      Address("Functional city",
        Street(42, "lambda street")
      )
    )
  )

Employee.company.address.street.name.modify(john, String::toUpperCase)
// Result here
```
