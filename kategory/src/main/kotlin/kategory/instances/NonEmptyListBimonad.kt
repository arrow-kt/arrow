package kategory

interface NonEmptyListBimonad : Bimonad<NonEmptyList.F>, NonEmptyListMonad, NonEmptyListComonad
