---
layout: docs-optics
title: Every
permalink: /optics/every/
---

## Every

`Every` is an optic that can see into a structure and get, set, or modify 0 to N foci.
It is useful when you want to focus into a structure that has 0 to N elements, such as collections etc.

`Every` is a composition of both `Traversal` and `Fold`, which means it's a generalization of both `map` and `foldMap`.

A structure `S` that has a focus `A`:
 - to which we can apply a function `(A) -> B` to `S` and get `T`. For example, `S == List<Int>` to which we apply `(Int) -> String` and we get `T == List<String>`
 - to which we can apply a function `(A) -> R` with `Monoid<R>` to `S` and get `R`. For example, `S == List<Int>` to which we apply `(Int) -> String` with `Monoid<String>` and we get `R == String`

An `Every` can simply be created by providing the `map` & `foldMap` function.

```kotlin:ank:playground
import arrow.optics.*
import arrow.core.foldMap
import arrow.typeclasses.Monoid

fun main(): Unit {
  //startSample
  val every: Every<List<Int>, Int> =
      object : Every<List<Int>, Int> {
        override fun modify(source: List<Int>, map: (focus: Int) -> Int): List<Int> =
          source.map(map)

        override fun <R> foldMap(M: Monoid<R>, source: List<Int>, map: (focus: Int) -> R): R =
          source.foldMap(M, map)
      }
  
  val source = listOf(1, 2, 3, 4)
  val target = every.modify(source, Int::inc)
  //endSample
  println(target)
} 
```

Or by using any of the pre-defined of `Every` on its `Companion` object.

```kotlin:ank:playground
Every.list<Int>().modify(listOf(1, 2, 3, 4), Int::inc)
```

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

val everyEmployee = Every.list<Employee>()

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

employeesStreetName.getAll(employee)
```

## Composition

Composing `Every` can be used for accessing and modifying foci in nested structures.

`Traversal` can be composed with all optics, and results in the following optics:

|   | Iso | Lens | Prism |Optional | Getter | Setter | Fold | Traversal |
| --- | --- | --- | --- |--- | --- | --- | --- | --- |
| Every | Every | Every | Every | Every | Every | Every | Every | Every |

### Polymorphic Every

When dealing with polymorphic types, we can also have polymorphic `Every`s that allow us to morph the type of the foci.
Previously, we used a `Every<List<Int>, Int>`; it was able to morph the `Int` values in the constructed type `List<Int>`.
With a `PEvery<List<Int>, List<String>, Int, String>`, we can morph an `Int` to a `String`, and thus, also morph the type from `List<Int>` to `List<String>`.
