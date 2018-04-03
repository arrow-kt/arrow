package docs

import arrow.product

@product
data class Account(val balance: Int, val available: Int)

data class Speed(val kmh: Int)

@product
data class Car(val mod: Int, val speed: Speed)