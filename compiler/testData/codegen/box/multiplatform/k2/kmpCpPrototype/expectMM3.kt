// FIR_IDENTICAL
// LANGUAGE: +MultiPlatformProjects
// TARGET_BACKEND: JVM
// WITH_STDLIB

// MODULE: lib-common
// FILE: libCommon.kt

fun foo(a: String = "") = "OK" // 1 klib
//fun foo(a: String = "") = "OK" // 2 lib-jvm.jar

// MODULE: lib-jvm()()(lib-common)
// FILE: libPlatform.kt

fun foo() = "Dead"

// MODULE: app-common(lib-common)()()
// FILE: appCommon.kt

fun commonBox(): String = foo() // 1

// MODULE: app-jvm(lib-jvm)()(app-common)
// FILE: app.kt

fun box() = commonBox().substring(0, 1) + foo("").substring(1) /* 1 */

