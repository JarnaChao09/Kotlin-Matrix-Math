package symbolic

interface EvalFun<X, Y> {
    fun eval(value: Map<X, Y>): Double
}