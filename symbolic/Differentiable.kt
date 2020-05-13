package symbolic

interface Differentiable<T: Differentiable<T>> {
    fun diff(): T
}