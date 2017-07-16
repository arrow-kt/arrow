package kategory

class IOEq : Eq<HK<IO.F, Int>> {
    override fun eqv(a: HK<IO.F, Int>, b: HK<IO.F, Int>): Boolean =
            a.ev().unsafeRunSync() == b.ev().unsafeRunSync()
}