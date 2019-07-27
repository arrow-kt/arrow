package arrow.generic

import arrow.core.Either
import arrow.product
import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.option.functor.functor
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import arrow.fx.rx2.ForObservableK
import arrow.fx.rx2.ObservableK
import arrow.fx.rx2.extensions.observablek.monad.monad
import arrow.fx.rx2.fix
import arrow.mtl.EitherT
import arrow.mtl.extensions.eithert.monad.monad
import arrow.mtl.value

data class Country(val code: String)
data class Address(val id: Int, val country: Option<Country>)
data class Person(val id: Int, val name: String, val address: Option<Address>)
sealed class BizError {
  data class PersonNotFound(val personId: Int) : BizError()
  data class AddressNotFound(val personId: Int) : BizError()
  data class CountryNotFound(val addressId: Int) : BizError()
}

typealias PersonNotFound = BizError.PersonNotFound
typealias AddressNotFound = BizError.AddressNotFound
typealias CountryNotFound = BizError.CountryNotFound

fun getCountryCode(maybePerson: Either<BizError, Person>): Either<BizError, String> =
  maybePerson.flatMap { person ->
    person.address.toEither { AddressNotFound(person.id) }.flatMap { address ->
      address.country.fold({ CountryNotFound(address.id).left() }, { it.code.right() })
    }
  }

val personDB: Map<Int, Person> = mapOf(
  1 to Person(
    id = 1,
    name = "Alfredo Lambda",
    address = Some(
      Address(
        id = 1,
        country = Some(
          Country(
            code = "ES"
          )
        )
      )
    )
  )
)

val adressDB: Map<Int, Address> = mapOf(
  1 to Address(
    id = 1,
    country = Some(
      Country(
        code = "ES"
      )
    )
  )
)

fun findPerson(personId: Int): ObservableK<Either<BizError, Person>> =
  ObservableK.just(
    Option.fromNullable(personDB.get(personId)).toEither { PersonNotFound(personId) }
  ) //mock impl for simplicity

fun findCountry(addressId: Int): ObservableK<Either<BizError, Country>> =
  ObservableK.just(
    Option.fromNullable(adressDB.get(addressId))
      .flatMap { it.country }
      .toEither { CountryNotFound(addressId) }
  ) //mock impl for simplicity



fun getCountryCode(personId: Int): ObservableK<Either<BizError, String>> =
  EitherT.monad<ForObservableK, BizError>(ObservableK.monad()).fx.monad {
    val (person) = EitherT(findPerson(personId))
    val address = !EitherT(ObservableK.just(
      person.address.toEither { AddressNotFound(personId) }
    ))
    val (country) = EitherT(findCountry(address.id))
    country.code
  }.value().fix()

val k = EitherT(Option(3.left())).mapLeft(Option.functor(), {it + 1})
@product
data class Account(val balance: Int, val available: Int) {
  companion object
}

@product
data class Speed(val kmh: Int) {
  companion object
}

@product
data class Car(val speed: Speed) {
  companion object
}

data class Salesperson(val name: String)
data class Dealership(val location: String)

sealed class CommonServerError
object ServerError : CommonServerError()
object UserUnauthorized : CommonServerError()
object OverRequestLimit : CommonServerError()

sealed class RegistrationError
object CarAlreadyRegistered : RegistrationError()
object StolenCar : RegistrationError()

data class SuccessfullyRegistered(val registration: Registration)

data class Registration(val car: Car)

interface Database {
  fun insertRegistration(registration: Registration)
}
