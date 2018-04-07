package com.example.domain

import arrow.lenses
import arrow.optionals
import arrow.syntax

@syntax
data class Street(val number: Int, val name: String)

@syntax
data class Address(val city: String, val street: Street)

@syntax
data class Company(val name: String, val address: Address)

@syntax
@optionals
@lenses
data class Employee(val name: String, val company: Company?)