package symbolic

interface Differentiable<V, T: Differentiable<V, T>> {
    fun diff(by: V): T
}