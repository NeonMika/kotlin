// PLATFORM_DEPENDANT_METADATA
// ALLOW_AST_ACCESS
// IGNORE_BACKEND: JS_IR
// Reason: java.io.Serializable shouldn't be accessible in JS
package test

import java.io.Serializable

@Target(AnnotationTarget.TYPE)
annotation class A

interface Foo<T : @A Number> : @A Serializable {
    fun <E, F : @A E> bar()
}
