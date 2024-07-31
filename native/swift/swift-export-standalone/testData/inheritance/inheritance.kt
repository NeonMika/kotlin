// KIND: STANDALONE
// MODULE: main
// FILE: main.kt

open class Foo

private class Bar : Foo()

fun getFoo(): Foo = Bar()
var foo: Foo = Bar()
