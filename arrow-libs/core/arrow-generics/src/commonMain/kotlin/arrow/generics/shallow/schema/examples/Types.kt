package arrow.generics.shallow.schema.examples

import arrow.generics.examples.Company
import arrow.generics.examples.Person
import arrow.generics.shallow.Choice
import arrow.generics.shallow.End
import arrow.generics.shallow.Field
import arrow.generics.shallow.Schema
import arrow.generics.shallow.Schema1
import arrow.generics.shallow.Schema2
import arrow.generics.shallow.SchemasOf
import arrow.generics.shallow.SumRepr
import arrow.generics.shallow.div
import arrow.generics.shallow.schema

// we have a type level representation
public typealias ClientRepr = Choice<PersonRepr, Choice<CompanyRepr, End>>
public typealias PersonRepr = Field<String, Field<Int, End>>
public typealias CompanyRepr = Field<String, End>

public fun <Rest : SumRepr> Schema<Rest>.companySchema(): Schema1<Company, String, Rest> =
  schema(::Company, Company::name)

public fun <Rest : SumRepr> Schema<Rest>.personSchema(): Schema2<Person, String, Int, Rest> =
  schema(::Person, Person::name / Person::age)

public val clientSchema3: Schema<ClientRepr> =
  SchemasOf.companySchema().personSchema()


