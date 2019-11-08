package arrow

import arrow.core.Option
import arrow.core.toT
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.Ref
import arrow.fx.extensions.io.monad.flatMap
import arrow.fx.extensions.io.monadDefer.monadDefer

interface Profile
interface User

// val ref: RefFactory<ForIO> = Ref.factory(IO.monadDefer())

fun userInfo(u: User): IO<Option<Profile>> =
  Ref(IO.monadDefer(), u).flatMap { ref ->
    ref.tryModify { it toT it.getProfile() }
  }

fun User.getProfile(): Profile =
  object : Profile {}
