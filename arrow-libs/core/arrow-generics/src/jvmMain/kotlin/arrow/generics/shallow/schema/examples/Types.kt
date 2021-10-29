package arrow.generics.shallow.schema.examples

import arrow.generics.examples.* // ktlint-disable no-wildcard-imports
import arrow.generics.shallow.* // ktlint-disable no-wildcard-imports
import arrow.generics.shallow.schema.* // ktlint-disable no-wildcard-imports

// we have a type level representation
public typealias ClientRepr = Choice<PersonRepr, Choice<CompanyRepr, End>>
public typealias PersonRepr = Field<String, Field<Int, End>>
public typealias CompanyRepr = Field<String, End>

// and a schema which ties with the actual run-time
public val clientSchema: Schema<ClientRepr> =
  Or(Person::class, And(Person::name, And(Person::age, Done)),
  Or(Company::class, And(Company::name, Done),
  Done))
