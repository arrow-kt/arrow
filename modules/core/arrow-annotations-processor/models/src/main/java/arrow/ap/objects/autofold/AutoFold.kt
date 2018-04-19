package arrow.ap.objects.autofold

import arrow.autofold

@autofold
sealed class AutoFold {
  data class First(val a: Int) : AutoFold()
  data class Second(val b: String) : AutoFold()
}