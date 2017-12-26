package arrow.data

@Deprecated("arrow.data.Either is already right biased. This data type will be removed in future releases")
interface LeftLike : EitherLike {
    override fun isLeft(): Boolean = true
    override fun isRight(): Boolean = false
}