package katz.data

/*class Kleisli[F[_], Z, A](run: Z => F[A]) {
    def flatMap[B](f: A => Kleisli[F, Z, B])(implicit F: FlatMap[F]): Kleisli[F, Z, B] =
    Kleisli(z => F.flatMap(run(z))(a => f(a).run(z)))

    def map[B](f: A => B)(implicit F: Functor[F]): Kleisli[F, Z, B] =
    Kleisli(z => F.map(run(z))(f))

    def local[ZZ](f: ZZ => Z): Kleisli[F, ZZ, A] = Kleisli(f.andThen(run))
}*/
