package arrow.generic

import arrow.product
import arrow.generic.coproduct3.coproductOf
import arrow.generic.coproduct3.fold
import arrow.generic.coproduct3.select
import arrow.generic.coproduct3.Coproduct3

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