package arrow.generic

import arrow.product

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