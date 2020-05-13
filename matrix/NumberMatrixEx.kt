package matrix

inline fun intMatrix(actions: IntMatrix.Scope.() -> IntMatrix.Scope): IntMatrix =
    IntMatrix(IntMatrix.Scope.Base().actions().matrix)

inline fun doubleMatrix(actions: DoubleMatrix.Scope.() -> DoubleMatrix.Scope): DoubleMatrix =
    DoubleMatrix(DoubleMatrix.Scope.Base().actions().matrix)

fun IntMatrix.map(which: Selector, action: (Int) -> Int): IntMatrix {
    val mat = IntMatrix(this.size)
    when(which) {
        Selector.ALL -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = action(v)
            }
        }
        Selector.DIAGONAL -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (i == j) action(v) else v
            }
        }
        Selector.OFF_DIAGONAL -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (i != j) action(v) else v
            }
        }
        Selector.LOWER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j <= i) action(v) else v
            }
        }
        Selector.STRICT_LOWER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j < i) action(v) else v
            }
        }
        Selector.STRICT_UPPER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j > i) action(v) else v
            }
        }
        Selector.UPPER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j >= i) action(v) else v
            }
        }
    }
    return mat
}

fun IntMatrix.mapIndexed(which: Selector, action: (Int, ri: Int, ci: Int) -> Int): IntMatrix {
    val mat = IntMatrix(this.size)
    when(which) {
        Selector.ALL -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = action(v, i, j)
            }
        }
        Selector.DIAGONAL -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (i == j) action(v, i, j) else v
            }
        }
        Selector.OFF_DIAGONAL -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (i != j) action(v, i, j) else v
            }
        }
        Selector.LOWER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j <= i) action(v, i, j) else v
            }
        }
        Selector.STRICT_LOWER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j < i) action(v, i, j) else v
            }
        }
        Selector.STRICT_UPPER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j > i) action(v, i, j) else v
            }
        }
        Selector.UPPER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j >= i) action(v, i, j) else v
            }
        }
    }
    return mat
}

fun IntMatrix.replace(only: Selector, replacement: Int, check: (Int) -> Boolean): IntMatrix {
    val mat = IntMatrix(this.size)
    when(only) {
        Selector.ALL -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (check(v)) replacement else v
            }
        }
        Selector.DIAGONAL -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (i == j && check(v)) replacement else v
            }
        }
        Selector.OFF_DIAGONAL -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (i != j && check(v)) replacement else v
            }
        }
        Selector.LOWER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j <= i && check(v)) replacement else v
            }
        }
        Selector.STRICT_LOWER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j < i && check(v)) replacement else v
            }
        }
        Selector.STRICT_UPPER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j > i && check(v)) replacement else v
            }
        }
        Selector.UPPER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j >= i && check(v)) replacement else v
            }
        }
    }
    return mat
}

fun IntMatrix.replaceNot(only: Selector, replacement: Int, check: (Int) -> Boolean): IntMatrix {
    val mat = IntMatrix(this.size)
    when(only) {
        Selector.ALL -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (!check(v)) replacement else v
            }
        }
        Selector.DIAGONAL -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (i == j && !check(v)) replacement else v
            }
        }
        Selector.OFF_DIAGONAL -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (i != j && !check(v)) replacement else v
            }
        }
        Selector.LOWER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j <= i && !check(v)) replacement else v
            }
        }
        Selector.STRICT_LOWER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j < i && !check(v)) replacement else v
            }
        }
        Selector.STRICT_UPPER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j > i && !check(v)) replacement else v
            }
        }
        Selector.UPPER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j >= i && !check(v)) replacement else v
            }
        }
    }
    return mat
}

fun DoubleMatrix.map(which: Selector, action: (Double) -> Double): DoubleMatrix {
    val mat = DoubleMatrix(this.size)
    when(which) {
        Selector.ALL -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = action(v)
            }
        }
        Selector.DIAGONAL -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (i == j) action(v) else v
            }
        }
        Selector.OFF_DIAGONAL -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (i != j) action(v) else v
            }
        }
        Selector.LOWER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j <= i) action(v) else v
            }
        }
        Selector.STRICT_LOWER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j < i) action(v) else v
            }
        }
        Selector.STRICT_UPPER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j > i) action(v) else v
            }
        }
        Selector.UPPER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j >= i) action(v) else v
            }
        }
    }
    return mat
}

fun DoubleMatrix.mapIndexed(which: Selector, action: (Double, ri: Int, ci: Int) -> Double): DoubleMatrix {
    val mat = DoubleMatrix(this.size)
    when(which) {
        Selector.ALL -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = action(v, i, j)
            }
        }
        Selector.DIAGONAL -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (i == j) action(v, i, j) else v
            }
        }
        Selector.OFF_DIAGONAL -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (i != j) action(v, i, j) else v
            }
        }
        Selector.LOWER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j <= i) action(v, i, j) else v
            }
        }
        Selector.STRICT_LOWER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j < i) action(v, i, j) else v
            }
        }
        Selector.STRICT_UPPER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j > i) action(v, i, j) else v
            }
        }
        Selector.UPPER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j >= i) action(v, i, j) else v
            }
        }
    }
    return mat
}

fun DoubleMatrix.replace(only: Selector, replacement: Double, check: (Double) -> Boolean): DoubleMatrix {
    val mat = DoubleMatrix(this.size)
    when(only) {
        Selector.ALL -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (check(v)) replacement else v
            }
        }
        Selector.DIAGONAL -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (i == j && check(v)) replacement else v
            }
        }
        Selector.OFF_DIAGONAL -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (i != j && check(v)) replacement else v
            }
        }
        Selector.LOWER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j <= i && check(v)) replacement else v
            }
        }
        Selector.STRICT_LOWER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j < i && check(v)) replacement else v
            }
        }
        Selector.STRICT_UPPER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j > i && check(v)) replacement else v
            }
        }
        Selector.UPPER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j >= i && check(v)) replacement else v
            }
        }
    }
    return mat
}

fun DoubleMatrix.replaceNot(only: Selector, replacement: Double, check: (Double) -> Boolean): DoubleMatrix {
    val mat = DoubleMatrix(this.size)
    when(only) {
        Selector.ALL -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (!check(v)) replacement else v
            }
        }
        Selector.DIAGONAL -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (i == j && !check(v)) replacement else v
            }
        }
        Selector.OFF_DIAGONAL -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (i != j && !check(v)) replacement else v
            }
        }
        Selector.LOWER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j <= i && !check(v)) replacement else v
            }
        }
        Selector.STRICT_LOWER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j < i && !check(v)) replacement else v
            }
        }
        Selector.STRICT_UPPER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j > i && !check(v)) replacement else v
            }
        }
        Selector.UPPER -> {
            for ((v, i, j) in this.withIndices) {
                mat[i, j] = if (j >= i && !check(v)) replacement else v
            }
        }
    }
    return mat
}