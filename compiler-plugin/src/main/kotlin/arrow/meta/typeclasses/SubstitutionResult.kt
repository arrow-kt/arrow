package arrow.meta.typeclasses

data class SubstitutionResult(val canBeReplaced : Boolean, val substitutions : List<TypeSubstitution>)