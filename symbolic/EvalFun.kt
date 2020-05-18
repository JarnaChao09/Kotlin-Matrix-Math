package symbolic

interface EvalFun<X, Y, R: Fun> {
    fun partialEval(value: Map<X, Y>): R
    fun evalImpl(value: Map<X, Y>): Double
    @Suppress("UNCHECKED_CAST")
    fun eval(value: Map<X, Y>): R = this.evalImpl(value).const as R
}