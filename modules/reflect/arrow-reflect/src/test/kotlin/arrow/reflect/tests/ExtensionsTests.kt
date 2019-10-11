package arrow.reflect.tests

import arrow.core.Option
import arrow.core.Try
import arrow.core.extensions.TryMonadError
import arrow.reflect.DataType
import arrow.reflect.Extension
import arrow.reflect.TypeClass
import arrow.reflect.TypeClassExtension
import arrow.reflect.extends
import arrow.reflect.extensions
import arrow.reflect.hierarchy
import arrow.reflect.supportedDataTypes
import arrow.reflect.supportedTypeClasses
import arrow.test.UnitSpec
import arrow.typeclasses.Functor
import arrow.typeclasses.Invariant
import arrow.typeclasses.MonadError
import io.kotlintest.shouldBe

object Bogus

class ReflectionTests : UnitSpec() {

  init {

    "The list of extensions for a bogus data type is empty" {
      DataType(Bogus::class).extensions().isEmpty() shouldBe true
    }

    "The list of extensions for a known data type isn't empty" {
      DataType(Option::class).extensions().isEmpty() shouldBe false
    }

    "The list of extensions for a bogus type class is empty" {
      TypeClass(Bogus::class).extensions().isEmpty() shouldBe true
    }

    "The list of extensions for a known type class isn't empty" {
      TypeClass(Functor::class).extensions().isEmpty() shouldBe false
    }

    "The list of type classes for a known data type isn't empty" {
      DataType(Option::class).supportedTypeClasses().isEmpty() shouldBe false
    }

    "The list of type classes for a bogus data type is empty" {
      DataType(Bogus::class).supportedTypeClasses().isEmpty() shouldBe true
    }

    "The list of data types for a known type class isn't empty" {
      TypeClass(Functor::class).supportedDataTypes().isEmpty() shouldBe false
    }

    "The list of data types for a bogus type class is empty" {
      TypeClass(Bogus::class).supportedDataTypes().isEmpty() shouldBe true
    }

    "A known instance is found in the data type extensions list" {
      DataType(Try::class).extensions().contains(TypeClassExtension(
        DataType(Try::class),
        TypeClass(MonadError::class),
        Extension(TryMonadError::class)
      )) shouldBe true
    }

    "We can determine a known type class hierarchy" {
      TypeClass(Functor::class).hierarchy() shouldBe listOf(
        TypeClass(Functor::class).extends(TypeClass(Invariant::class))
      )
    }
  }
}
