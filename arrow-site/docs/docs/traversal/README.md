---
layout: docs-optics
title: Traversal
permalink: /optics/traversal/
---

## Traversal

A `Traversal` is an optic that can see into a structure and set, or modify 0 to N foci.
And thus a `Traversal` is useful when you want to focus into a structure that has 0 to N elements, such as collections etc. 

It is a generalization of `map`.
A structure `S` that has a focus `A` to which we can apply a function `(A) -> B` to `S` and get `T`.
For example, `S == List<Int>` to which we apply `(Int) -> String` and we get `T == List<String>`

A `Traversal` can simply be created by providing the `map` function.

```kotlin:ank:playground
import arrow.optics.*

fun main(): Unit {
  //startSample
  val traversal: PTraversal<List<Int>, List<String>, Int, String> =
    PTraversal { s, f -> s.map(f) }
  
  val source = listOf(1, 2, 3, 4)
  val target = traversal.modify(source, Int::toString)
  //endSample
  println(target)
} 
```

Or by using any of the constructors of `Traversal`.

Arrow optics also provides a number of predefined `Traversal` optics.

## Usage

```kotlin
data class Street(val number: Int, val name: String)
data class Address(val city: String, val street: Street)
data class Company(val name: String, val address: Address)
data class Employee(val name: String, val company: Company)
data class Employees(val employees: List<Employee>)
```
```kotlin:ank
val john = Employee("John Doe", Company("Arrow", Address("Functional city", Street(23, "lambda street"))))
val jane = Employee("John Doe", Company("Arrow", Address("Functional city", Street(23, "lambda street"))))
val employees = Employees(listOf(john, jane))
```

Without lenses, we could use the `copy` method provided on a `data class` for dealing with immutable structures.

```kotlin:ank
employees.employees.map { employee ->
  employee.copy(
          company = employee.company.copy(
                  address = employee.company.address.copy(
                          street = employee.company.address.street.copy(
                                  name = employee.company.address.street.name.capitalize()
                          )
                  )
          )
  )
}
```

Imagine how complex this would look if we would also introduce `sealed class` into our domain.
This is hard to read, does not scale very well, and it draws attention away from the simple operation we wanted to do `name.capitalize()`.

What we actually wanted to do here is the following: focus into _every_ employee, `and then` focus into the employee's company, `and then` focus into the company's address, `and then` focus into the street address, and finally, modify the street name by capitalizing it.

```kotlin
val employees: Lens<Employees, List<Employee>> = Lens(
  get = { it.company },
  set = { employee, company -> employee.copy(company = company) }
)

val everyEmployee = Traversal.list<Employee>()

val employeeCompany: Lens<Employee, Company> = Lens(
        get = { it.company },
        set = { employee, company -> employee.copy(company = company) }
)

val companyAddress: Lens<Company, Address> = Lens(
        get = { it.address },
        set = { company, address -> company.copy(address = address) }
)

val addressStrees: Lens<Address, Street> = Lens(
        get = { it.street },
        set = { address, street -> address.copy(street = street) }
)

val streetName: Lens<Street, String> = Lens(
        get = { it.name },
        set = { street, name -> street.copy(name = name) }
)

val employeesStreetName: Lens<Employee, String> = employees compose everyEmployee compose employeeCompany compose companyAddress compose addressStrees compose streetName

employeesStreetName.modify(employee, String::capitalize)
```

## Composition

Composing `Traversal` can be used for accessing and modifying foci in nested structures.

`Traversal` can be composed with all optics, and results in the following optics:

|   | Iso | Lens | Prism |Optional | Getter | Setter | Fold | Traversal |
| --- | --- | --- | --- |--- | --- | --- | --- | --- |
| Traversal | Traversal | Traversal | Traversal | Traversal | Fold | Setter | Fold | Traversal |

### Polymorphic Traversal

When dealing with polymorphic types, we can also have polymorphic `Traversal`s that allow us to morph the type of the foci.
Previously, we used a `Traversal<List<Int>, Int>`; it was able to morph the `Int` values in the constructed type `List<Int>`.
With a `PTraversal<List<Int>, List<String>, Int, String>`, we can morph an `Int` to a `String`, and thus, also morph the type from `List<Int>` to `List<String>`.

### Laws

Arrow provides [`TraversalLaws`][traversal_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own traversal.

[traversal_laws_source]: https://github.com/arrow-kt/arrow/blob/main/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/TraversalLaws.kt
