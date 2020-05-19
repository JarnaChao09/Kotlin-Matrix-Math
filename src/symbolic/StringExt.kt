package symbolic

val String.asVar: Variable
    get() = Variable(this)

val String.parse: Fun
    get() = TODO()