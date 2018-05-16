package arrow.typeclasses

import arrow.Kind

interface Invariant<F> {

  fun <A, B> Kind<F, A>.imap(f: (A) -> B, fi: (B) -> A): Kind<F, B>

  // TODO("What are those!")
  // 6 codegen creates the extension functions for the companion for you, so just add the instances and annotate them with @instance

/*  object Invariant {
    implicit val catsInvariantMonoid: Invariant[Monoid] = new Invariant[Monoid] {

      def imap[A, B](fa: Monoid[A])(f: A => B)(g: B => A): Monoid[B] = new Monoid[B] {
        val empty = f(fa.empty)
        def combine(x: B, y: B): B = f(fa.combine(g(x), g(y)))
        override def combineAllOption(bs: TraversableOnce[B]): Option[B] =
        fa.combineAllOption(bs.map(g)).map(f)
      }

    }

    implicit val catsInvariantBand: Invariant[Band] = new Invariant[Band] {

      def imap[A, B](fa: Band[A])(f: A => B)(g: B => A): Band[B] = new Band[B] {
        def combine(x: B, y: B): B = f(fa.combine(g(x), g(y)))
        override def combineAllOption(bs: TraversableOnce[B]): Option[B] =
        fa.combineAllOption(bs.map(g)).map(f)
      }
    }

    implicit val catsInvariantSemilattice: Invariant[Semilattice] = new Invariant[Semilattice] {

      def imap[A, B](fa: Semilattice[A])(f: A => B)(g: B => A): Semilattice[B] = new Semilattice[B] {
        def combine(x: B, y: B): B = f(fa.combine(g(x), g(y)))
        override def combineAllOption(bs: TraversableOnce[B]): Option[B] =
        fa.combineAllOption(bs.map(g)).map(f)
      }

    }



    implicit val catsInvariantCommutativeMonoid: Invariant[CommutativeMonoid] = new Invariant[CommutativeMonoid] {

      def imap[A, B](fa: CommutativeMonoid[A])(f: A => B)(g: B => A): CommutativeMonoid[B] = new CommutativeMonoid[B] {
        val empty = f(fa.empty)
        def combine(x: B, y: B): B = f(fa.combine(g(x), g(y)))
        override def combineAllOption(bs: TraversableOnce[B]): Option[B] =
        fa.combineAllOption(bs.map(g)).map(f)
      }

    }

    implicit val catsInvariantBoundedSemilattice: Invariant[BoundedSemilattice] = new Invariant[BoundedSemilattice] {

      def imap[A, B](fa: BoundedSemilattice[A])(f: A => B)(g: B => A): BoundedSemilattice[B] = new BoundedSemilattice[B] {
        val empty = f(fa.empty)
        def combine(x: B, y: B): B = f(fa.combine(g(x), g(y)))
        override def combineAllOption(bs: TraversableOnce[B]): Option[B] =
        fa.combineAllOption(bs.map(g)).map(f)
      }

    }


    implicit val catsInvariantGroup: Invariant[Group] = new Invariant[Group] {

      def imap[A, B](fa: Group[A])(f: A => B)(g: B => A): Group[B] = new Group[B] {
        val empty = f(fa.empty)
        def combine(x: B, y: B): B = f(fa.combine(g(x), g(y)))
        def inverse(b: B): B = f(fa.inverse(g(b)))
        override def combineAllOption(bs: TraversableOnce[B]): Option[B] =
        fa.combineAllOption(bs.map(g)).map(f)
      }

    }

    implicit val catsInvariantCommutativeGroup: Invariant[CommutativeGroup] = new Invariant[CommutativeGroup] {

      def imap[A, B](fa: CommutativeGroup[A])(f: A => B)(g: B => A): CommutativeGroup[B] = new CommutativeGroup[B] {
        val empty = f(fa.empty)
        def combine(x: B, y: B): B = f(fa.combine(g(x), g(y)))
        def inverse(b: B): B = f(fa.inverse(g(b)))
        override def combineAllOption(bs: TraversableOnce[B]): Option[B] =
        fa.combineAllOption(bs.map(g)).map(f)
      }

    }*/
}
