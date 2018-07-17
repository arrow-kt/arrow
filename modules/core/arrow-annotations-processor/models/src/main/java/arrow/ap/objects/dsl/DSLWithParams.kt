package arrow.ap.objects.dsl

import arrow.optics.Optional
import arrow.optics.optics

@optics
fun <A> index(i: Int): Optional<List<A>, A> = TODO()