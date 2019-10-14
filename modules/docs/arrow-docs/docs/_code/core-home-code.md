---
library: core
---
{: data-executable="true"}
```kotlin
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.extensions.fx

object Nuke
object Target
object Impacted

sealed class NukeException {
  object SystemOffline: NukeException()
  object RotationNeedsOil: NukeException()
  data class MissedByMeters(val meters : Int): NukeException()
}

typealias SystemOffline = NukeException.SystemOffline
typealias RotationNeedsOil = NukeException.RotationNeedsOil
typealias MissedByMeters = NukeException.MissedByMeters

fun arm(): Either<SystemOffline, Nuke> = Right(Nuke)
fun aim(): Either<RotationNeedsOil, Target> = Right(Target)
fun launch(target: Target, nuke: Nuke): Either<MissedByMeters, Impacted> = Left(MissedByMeters(5))
fun main() {
//sampleStart


fun launchNuke(): Either<NukeException, Impacted> =
  Either.fx<NukeException, Impacted> {
    val nuke = !arm()
    val target = !aim()
    val impact = !launch(target, nuke)
    impact
  }

println(launchNuke())
//sampleEnd
}
```
