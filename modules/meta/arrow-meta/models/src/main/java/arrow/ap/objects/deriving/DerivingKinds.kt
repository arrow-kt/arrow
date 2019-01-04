package arrow.ap.objects.deriving

class ForDeriving private constructor() { companion object }
typealias DerivingOf<A> = arrow.Kind<ForDeriving, A>

fun <A> DerivingOf<A>.fix(): Deriving<A> = this as Deriving<A>