package arrow.core

const val OrderingDeprecation = "Ordering is deprecated together with Order. Use compareTo instead of Order instead."

@Deprecated(OrderingDeprecation)
sealed class Ordering {
  override fun equals(other: Any?): Boolean =
    this === other // ref equality is fine because objects should be singletons

  override fun toString(): String =
    when (this) {
      LT -> "LT"
      GT -> "GT"
      EQ -> "EQ"
    }

  override fun hashCode(): Int =
    when (this) {
      LT -> -1
      GT -> 1
      EQ -> 0
    }

  fun toInt(): Int =
    when (this) {
      LT -> -1
      GT -> 1
      EQ -> 0
    }

  operator fun plus(b: Ordering): Ordering =
    when (this) {
      LT -> LT
      EQ -> b
      GT -> GT
    }

  companion object {
    fun fromInt(i: Int): Ordering = when (i) {
      0 -> EQ
      else -> if (i < 0) LT else GT
    }
  }
}

object LT : Ordering()
object GT : Ordering()
object EQ : Ordering()
