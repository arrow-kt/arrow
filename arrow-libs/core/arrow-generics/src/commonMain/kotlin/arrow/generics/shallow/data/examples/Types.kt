package arrow.generics.shallow.data.examples

import arrow.generics.examples.Client
import arrow.generics.examples.Company
import arrow.generics.examples.Person
import arrow.generics.examples.Tree
import arrow.generics.shallow.data.And
import arrow.generics.shallow.Choice
import arrow.generics.shallow.End
import arrow.generics.shallow.Field
import arrow.generics.shallow.data.Done
import arrow.generics.shallow.data.Generic
import arrow.generics.shallow.data.That
import arrow.generics.shallow.data.This

// this is the representation type
// this follows the "sum of products" approach
// - we only have a set of nested TyS for the sum part
// - and inside each of them we have nested TyP
public typealias ClientRepr = Choice<PersonRepr, Choice<CompanyRepr, End>>
public typealias PersonRepr = Field<String, Field<Int, End>>
public typealias CompanyRepr = Field<String, End>

// this is the conversion to the representation
// this would be derived automatically by some plugin
public fun Client.toGeneric(): Generic<ClientRepr> = when (this) {
  is Person -> This("Person", And("name", name, And("age", age, Done)))
  is Company -> That(This("Company", And("name", name, Done)))
}

public typealias TreeRepr<A> = Choice<LeafRepr<A>, Choice<NodeRepr<A>, End>>
public typealias LeafRepr<A> = Field<A, End>
public typealias NodeRepr<A> = Field<Tree<A>, Field<Tree<A>, End>>
