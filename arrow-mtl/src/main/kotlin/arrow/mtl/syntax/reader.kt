package arrow.mtl.syntax

import arrow.*

inline fun <reified D> ReaderApi.monadReader(): MonadReader<ReaderKindPartial<D>, D> = arrow.monadReader<IdHK, D>()