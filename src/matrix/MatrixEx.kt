package matrix

inline fun <reified T: Any> matrix(actions: Matrix.Scope<T>.() -> Matrix.Scope<T>): Matrix<T> =
    Matrix(Matrix.Scope.Base<T>().actions().matrix)

fun <T: Any> Matrix<T>.forEach(which: Selector = Selector.ALL, action: (T) -> Unit) {
    when(which) {
        Selector.ALL -> {
            for (i in this) {
                action(i)
            }
        }
        Selector.DIAGONAL -> {
            for (i in 0 until this.rowLength) {
                action(this[i, i])
            }
        }
        Selector.OFF_DIAGONAL -> {
            for ((v, i, j) in this.withIndices) {
                if (i != j) action(v)
            }
        }
        Selector.LOWER -> {
            for ((v, i, j) in this.withIndices) {
                if (j <= i) action(v)
            }
        }
        Selector.STRICT_LOWER -> {
            for ((v, i, j) in this.withIndices) {
                if (j < i) action(v)
            }
        }
        Selector.STRICT_UPPER -> {
            for ((v, i, j) in this.withIndices) {
                if (j > i) action(v)
            }
        }
        Selector.UPPER -> {
            for ((v, i, j) in this.withIndices) {
                if (j >= i) action(v)
            }
        }
    }
}

fun <T: Any> Matrix<T>.forEachIndexed(which: Selector = Selector.ALL, action: (T, ri: Int, ci: Int) -> Unit) {
    when(which) {
        Selector.ALL -> {
            for ((v, i, j) in this.withIndices) {
                action(v, i, j)
            }
        }
        Selector.DIAGONAL -> {
            for (i in 0 until this.rowLength) {
                action(this[i, i], i, i)
            }
        }
        Selector.OFF_DIAGONAL -> {
            for ((v, i, j) in this.withIndices) {
                if (i != j) action(v, i, j)
            }
        }
        Selector.LOWER -> {
            for ((v, i, j) in this.withIndices) {
                if (j <= i) action(v, i, j)
            }
        }
        Selector.STRICT_LOWER -> {
            for ((v, i, j) in this.withIndices) {
                if (j < i) action(v, i, j)
            }
        }
        Selector.STRICT_UPPER -> {
            for ((v, i, j) in this.withIndices) {
                if (j > i) action(v,i ,j)
            }
        }
        Selector.UPPER -> {
            for ((v, i, j) in this.withIndices) {
                if (j >= i) action(v, i, j)
            }
        }
    }
}

fun <T: Any> Matrix<T>.map(which: Selector = Selector.ALL, action: (T) -> T): Matrix<T> {
    val mat = Matrix.nulls<T>(this.size)
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

fun <T: Any> Matrix<T>.mapIndexed(which: Selector = Selector.ALL, action: (T, ri: Int, ci: Int) -> T): Matrix<T> {
    val mat = Matrix.nulls<T>(this.size)
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

fun <T: Any> Matrix<T>.all(only: Selector = Selector.ALL, check: (T) -> Boolean): Boolean {
    var ret = true
    when(only) {
        Selector.ALL -> {
            for (v in this) {
                ret = ret && check(v)
            }
        }
        Selector.DIAGONAL -> {
            for ((v, i, j) in this.withIndices) {
                ret = ret && (if (i == j) check(v) else true)
            }
        }
        Selector.OFF_DIAGONAL -> {
            for ((v, i, j) in this.withIndices) {
                ret = ret && (if (i != j) check(v) else true)
            }
        }
        Selector.LOWER -> {
            for ((v, i, j) in this.withIndices) {
                ret = ret && (if (j <= i) check(v) else true)
            }
        }
        Selector.STRICT_LOWER -> {
            for ((v, i, j) in this.withIndices) {
                ret = ret && (if (j < i) check(v) else true)
            }
        }
        Selector.STRICT_UPPER -> {
            for ((v, i, j) in this.withIndices) {
                ret = ret && (if (j > i) check(v) else true)
            }
        }
        Selector.UPPER -> {
            for ((v, i, j) in this.withIndices) {
                ret = ret && (if (j >= i) check(v) else true)
            }
        }
    }
    return ret
}

fun <T: Any> Matrix<T>.any(only: Selector = Selector.ALL, check: (T) -> Boolean): Boolean {
    var ret = true
    when(only) {
        Selector.ALL -> {
            for (v in this) {
                ret = ret || check(v)
            }
        }
        Selector.DIAGONAL -> {
            for ((v, i, j) in this.withIndices) {
                ret = ret || (if (i == j) check(v) else true)
            }
        }
        Selector.OFF_DIAGONAL -> {
            for ((v, i, j) in this.withIndices) {
                ret = ret || (if (i != j) check(v) else true)
            }
        }
        Selector.LOWER -> {
            for ((v, i, j) in this.withIndices) {
                ret = ret || (if (j <= i) check(v) else true)
            }
        }
        Selector.STRICT_LOWER -> {
            for ((v, i, j) in this.withIndices) {
                ret = ret || (if (j < i) check(v) else true)
            }
        }
        Selector.STRICT_UPPER -> {
            for ((v, i, j) in this.withIndices) {
                ret = ret || (if (j > i) check(v) else true)
            }
        }
        Selector.UPPER -> {
            for ((v, i, j) in this.withIndices) {
                ret = ret || (if (j >= i) check(v) else true)
            }
        }
    }
    return ret
}

fun <T: Any> Matrix<T>.replace(only: Selector = Selector.ALL, replacement: T, check: (T) -> Boolean): Matrix<T> {
    val mat = Matrix.nulls<T>(this.size)
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

fun <T: Any> Matrix<T>.replaceNot(only: Selector = Selector.ALL, replacement: T, check: (T) -> Boolean): Matrix<T> {
    val mat = Matrix.nulls<T>(this.size)
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