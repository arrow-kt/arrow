package arrow.generics.examples

public sealed interface Client
public data class Person(val name: String, val age: Int) : Client
public data class Company(val name: String) : Client
