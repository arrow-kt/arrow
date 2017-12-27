package arrow.data

@Deprecated("arrow.data.Either is already right biased. This data type will be removed in future releases")
interface RightLike : EitherLike {
    override fun isLeft(): Boolean = false
    override fun isRight(): Boolean = true
}