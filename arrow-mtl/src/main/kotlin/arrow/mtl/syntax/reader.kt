package arrow.mtl.syntax

import arrow.*
import arrow.data.ReaderApi
import arrow.data.ReaderKindPartial

inline fun <reified D> ReaderApi.monadReader(): MonadReader<ReaderKindPartial<D>, D> = arrow.monadReader<IdHK, D>()