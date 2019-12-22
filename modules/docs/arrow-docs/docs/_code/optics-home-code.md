---
library: optics
---
{: data-executable="true"}
```kotlin
package example
import arrow.core.*
import arrow.optics.*
data class Street(val number: Int, val name: String) {
  companion object {
    val name: Lens<Street, String> = Lens(Street::name)
    { street, name -> street.copy(name = name) }
  }
}
data class Address(val city: String, val street: Street) {
  companion object {
    val street: Lens<Address, Street> = Lens(Address::street)
     { address, street -> address.copy(street = street) }
  }
}
data class Company(val name: String, val address: Address) {
  companion object {
    val address: Lens<Company, Address> = Lens(Company::address)
     { company, address -> company.copy(address = address) }
  }
}
data class Employee(val name: String, val company: Company?) {
  companion object {
    val company: Optional<Employee, Company> = Optional(
      getOption = { it.company.toOption() },
      set = { source, focus -> source.copy(company = focus) }
    )
  }
}
fun main() {
  //sampleStart


  val john =
  Employee("John Doe",
          Company("Arrow",
                  Address("Functional city",
                          Street(42, "lambda street"))))

  val modify = Employee.company.address.street.name
      .modify(john, String::toUpperCase)

  println(modify)
//sampleEnd
}
val <A> Optional<A, Company>.address: Optional<A, Address>
  get() = this compose Company.address
val <A> Optional<A, Address>.street: Optional<A, Street>
  get() = this compose Address.street
val <A> Optional<A, Street>.name: Optional<A, String>
  get() = this compose Street.name
```
