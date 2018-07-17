package arrow.ap.objects.dsl

import arrow.optics.PTraversal
import arrow.optics.optics

@optics
fun <A, B> pOptic(): PTraversal<List<A>, List<B>, A, B> = TODO()