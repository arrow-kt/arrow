---
library: core
---
{: data-executable="true"}
```kotlin:ank
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.extensions.fx

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
fun main() {
//sampleStart


fun prepareLunch(): Either<CookingException, Salad> =
  Either.fx<CookingException, Salad> {
    val lettuce = !takeFoodFromRefrigerator()
    val knife = !getKnife()
    val lunch = !prepare(knife, lettuce)
    lunch
  }

println(prepareLunch())
//sampleEnd
}
```
