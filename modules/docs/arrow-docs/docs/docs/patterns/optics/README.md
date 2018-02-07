---
layout: docs
title: Optics
permalink: /docs/patterns/optics/
---

## Optics

TODO

### Generating optics

To avoid boilerplate and introduce familiar property-like syntax optics can be generated using `@syntax` annotation. Optics will be generated in the same package as `data class` and can be used by invoking `setter()` on data class instance.

```
@syntax
data class Street(val number: Int, val name: String)
@syntax
data class Address(val city: String, val street: Street)
@syntax
data class Company(val name: String, val address: Address)
@syntax
data class Employee(val name: String, val company: Company?)


val employee = Employee("John Doe", Company("Kategory", Address("Functional city", Street(42, "lambda street"))))
val newEmployee = employee.setter().company.nullable.address.street.name.modify { it.capitalize() }

//Employee(name=John Doe, company=Company(name=Kategory, address=Address(city=Functional city, street=Street(number=42, name=Lambda street))))
```

