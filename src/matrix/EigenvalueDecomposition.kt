package matrix

import complex.Complex
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
            v = matrix.doubleArray
            tridiagonalize()
            diagonalize()
        } else {
            v = Array(size) { DoubleArray(size) }
            h = matrix.doubleArray
            reduceToHessenberg()
            hessenbergToRealSchur()
        }
    }

    fun eigenvectorMatrix(): DoubleMatrix =
        DoubleMatrix(buildEigenvectors().t)

    fun eigenvectorMatrixInv(): DoubleMatrix {
        var r = DoubleMatrix(buildEigenvectors())
        if (!symmetric) {
            r = r.t.inv
        }
        return r
    }

    /**after ComplexArray creation, change return type**/
    fun eigenvalues(): Array<Complex> {
        val values = Array(d.size) { i -> Complex(d[i]) }
        e.forEachIndexed { i, imag -> values[i] = if (imag != 0.0) Complex(values[i], imag) else values[i] }
        return values
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
        for (i in 1 until size) {
            e[i - 1] = e[i]
        }
        e[size - 1] = 0.0

        var f = 0.0
        var tst1 = 0.0
        var eps = 2.2204460492503131e-16

        for (l in 0 until size) {
            tst1 = max(tst1, d[l].absoluteValue + e[l].absoluteValue)
            var m = l
            while (m < size) {
                if (e[m].absoluteValue <= eps*tst1) {
                    break
                }
                m += 1
            }

            if (m > l) {
                var iter = 0
                do {
                    iter += 1

                    var g = d[l]
                    var p = (d[l + 1] - g) / (2.0 * e[l])
                    var r = hypot(p, 1.0)
                    if (p < 0.0) {
                        r = -r
                    }
                    d[l] = e[l] / (p + r)
                    d[l + 1] = e[l] * (p + r)
                    var dl1 = d[l + 1]
                    var h_ = g - d[l]
                    for (i in (l + 2) until size) {
                        d[i] -= h_
                    }
                    f += h_

                    p = d[m]
                    var c = 1.0
                    var c2 = c
                    var c3 = c
                    var el1 = e[l + 1]
                    var s = 0.0
                    var s2 = 0.0
                    for (i in (m - 1) downTo l) {
                        c3 = c2
                        c2 = c
                        s2 = s
                        g = c * e[i]
                        h_ = c * p
                        r = hypot(p, e[i])
                        e[i + 1] = s * r
                        s = e[i] / r
                        c = p / r
                        p = c * d[i] - s * g
                        d[i + 1] = h_ + s * (c * g + s * d[i])

                        for (k in 0 until size) {
                            h_ = v[k][i + 1]
                            v[k][i + 1] = s * v[k][i] + c * h_
                            v[k][i] = c * v[k][i] - s * h_
                        }
                    }
                    p = -s * s2 * c3 * el1 * e[l] / dl1
                    e[l] = s * p
                    d[l] = c * p
                } while (e[l].absoluteValue > eps * tst1)
            }
            d[l] = d[l] + f
            e[l] = 0.0
        }
        for (i in 0..(size - 2)) {
            var k = i
            var p = d[i]
            for (j in (i + 1) until size) {
                if (d[j] < p) {
                    k = j
                    p = d[j]
                }
            }
            if (k != i) {
                d[k] = d[i]
                d[i] = p
                for (j in 0 until size) {
                    p = v[j][i]
                    v[j][i] = v[j][k]
                    v[j][k] = p
                }
            }
        }
    }

    private fun reduceToHessenberg() {
        TODO("Not yet implemented")
    }

    private fun hessenbergToRealSchur() {
        TODO("Not yet implemented")
    }
}