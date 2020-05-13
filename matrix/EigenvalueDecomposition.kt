package matrix

import kotlin.math.*

class EigenvalueDecomposition(matrix: DoubleMatrix) {
    constructor(matrix1: NumberMatrix<*>): this(matrix1 as DoubleMatrix)

    val size = matrix.rowLength

    var d = DoubleArray(size)

    var e = DoubleArray(size)

    var v: Array<DoubleArray>

    var h: Array<DoubleArray> = Array(size) { DoubleArray(size) }

    var ort: DoubleArray = DoubleArray(size)

    val symmetric = matrix.isSymmetric()

    init {
        if (symmetric) {
            v = matrix.array
            tridiagonalize()
            diagonalize()
        } else {
            v = Array(size) { DoubleArray(size) }
            h = matrix.array
            reduceToHessenberg()
            hessenbergToRealSchur()
        }
    }

    fun eigenvectorMatrix(): DoubleMatrix =
        DoubleMatrix(buildEigenvectors().t)

    fun eigenvalues(): DoubleArray {
        TODO("Not yet implemented")
    }

    fun eigenvalueMatrix(): DoubleMatrix {
        TODO("Not yet implemented")
    }

    private fun buildEigenvectors(): DoubleMatrix {
        TODO("Not yet implemented")
    }

    private fun tridiagonalize() {
        for (j in 0 until size) {
            this.d[j] = this.v[size - 1][j]
        }

        for (i in (size - 1) downTo (0 + 1)) {
            var scale = 0.0
            var h = 0.0
            for (k in 0 until i) {
                scale += abs(this.d[k])
            }
            if (scale == 0.0) {
                this.e[i] = this.d[i - 1]
                for (j in 0 until i) {
                    this.d[j] = this.v[i - 1][j]
                    this.v[i][j] = 0.0
                    this.v[j][i] = 0.0
                }
            } else {
                for (k in 0 until i) {
                    this.d[k] /= scale
                    h += this.d[k] * this.d[k]
                }
                var f = this.d[i - 1]
                var g = sqrt(h)
                if (f > 0.0) {
                    g = -g
                }
                this.e[i] = scale * g
                h -= f * g
                this.d[i - 1] = f - g
                this.d[i - 1] = f - g
                for (j in 0 until i) {
                    this.e[j] = 0.0
                }

                for (j in 0 until i) {
                    f = this.d[j]
                    this.v[j][i] = f
                    g = this.e[j] + this.v[j][j] * f
                    for (k in (j+1) until i) {
                        g += this.v[k][j] * this.d[k]
                        this.e[k] += this.v[k][j] * f
                    }
                    this.e[j] = g
                }
                f = 0.0
                for (j in 0 until i) {
                    this.e[j] /= h
                    f += this.e[j] * this.d[j]
                }
                val hh = f / (h + h)
                for (j in 0 until i) {
                    this.e[j] -= hh * this.d[j]
                }
                for (j in 0 until i) {
                    f = this.d[j]
                    g = this.e[j]
                    for (k in j until i) {
                        this.v[k][j] -= (f * this.e[k] + g * this.d[k])
                    }
                    this.d[j] = this.v[i - 1][j]
                    this.v[i][j] = 0.0
                }
            }
            this.d[i] = h
        }

        for (i in 0 until size - 1) {
            this.v[size - 1][i] = this.v[i][i]
            this.v[i][i] = 1.0
            val h = this.d[i + 1]
            if (h != 0.0) {
                for (k in 0..i) {
                    this.d[k] = this.v[k][i + 1] / h
                }
                for (j in 0..i) {
                    var g = 0.0
                    for (k in 0..i) {
                        g += this.v[k][i + 1] * this.v[k][j]
                    }
                    for (k in 0..i) {
                        this.v[k][j] -= g * this.d[k]
                    }
                }
            }
            for (k in 0..i) {
                this.v[k][i + 1] = 0.0
            }
        }
        for (j in 0 until size) {
            this.d[j] = this.v[size - 1][j]
            this.v[size - 1][j] = 0.0
        }
        this.v[size - 1][size - 1] = 1.0
        this.e[0] = 0.0
    }

    private fun diagonalize() {
        TODO("Not yet implemented")
    }

    private fun reduceToHessenberg() {
        TODO("Not yet implemented")
    }

    private fun hessenbergToRealSchur() {
        TODO("Not yet implemented")
    }
}