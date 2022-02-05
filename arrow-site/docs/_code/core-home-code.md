---
library: core
---
<!--- INCLUDE
import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.computations.either

object Lettuce
object Knife
object Salad

sealed class CookingException {
  object LettuceIsRotten: CookingException()
  object KnifeNeedsSharpening: CookingException()
  data class InsufficientAmount(val quantityInGrams : Int): CookingException()
}

typealias NastyLettuce = CookingException.LettuceIsRotten
typealias KnifeIsDull = CookingException.KnifeNeedsSharpening
typealias InsufficientAmountOfLettuce = CookingException.InsufficientAmount

fun takeFoodFromRefrigerator(): Either<NastyLettuce, Lettuce> = Right(Lettuce)
fun getKnife(): Either<KnifeIsDull, Knife> = Right(Knife)
fun prepare(tool: Knife, ingredient: Lettuce): Either<InsufficientAmountOfLettuce, Salad> = Left(InsufficientAmountOfLettuce(5))
-->
```kotlin
suspend fun prepareLunch(): Either<CookingException, Salad> =
  // with the 'either' computation block
  // we follow a "fail fast" strategy
  either<CookingException, Salad> {
    val lettuce = takeFoodFromRefrigerator().bind()
    val knife = getKnife().bind()
    val lunch = prepare(knife, lettuce).bind()
    lunch
  }
```
