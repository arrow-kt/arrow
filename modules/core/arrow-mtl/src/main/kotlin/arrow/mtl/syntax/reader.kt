package arrow.mtl.syntax

import arrow.data.ReaderApi
import arrow.data.ReaderPartialOf
import arrow.mtl.MonadReader

inline fun <reified D> ReaderApi.monadReader(): MonadReader<ReaderPartialOf<D>, D> = arrow.mtl.monadReader()
