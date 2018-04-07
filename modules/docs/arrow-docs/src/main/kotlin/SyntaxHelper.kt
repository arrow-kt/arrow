package com.example.domain

import arrow.optics.optics

@optics
data class Street(val number: Int, val name: String)

@optics
data class Address(val city: String, val street: Street)

@optics
data class Company(val name: String, val address: Address)

@optics
data class Employee(val name: String, val company: Company?)