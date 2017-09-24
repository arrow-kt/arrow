package kategory

fun <A> identity(a: A): A = a

fun <A, B, Z> ((A, B) -> Z).curry(): (A) -> (B) -> Z = { p1: A -> { p2: B -> this(p1, p2) } }

