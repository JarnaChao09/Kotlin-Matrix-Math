package symbolic

interface Simplify<X: Fun> {
    fun simplify(): X
}