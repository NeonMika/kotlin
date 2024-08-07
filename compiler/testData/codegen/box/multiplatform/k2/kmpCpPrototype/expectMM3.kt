// FIR_IDENTICAL
// LANGUAGE: +MultiPlatformProjects
// TARGET_BACKEND: JVM
// WITH_STDLIB

// MODULE: lib-common
// FILE: libCommon.kt

fun foo(a: String = "") = "common" // 1 klib
//fun foo(a: String = "") = "OK" // 2 lib-jvm.jar

// MODULE: lib-jvm()()(lib-common)
// FILE: libPlatform.kt

fun foo() = "platform"

// MODULE: app-common(lib-common)()()
// FILE: appCommon.kt

fun commonBox(): String = foo() // 1

// MODULE: app-jvm(lib-jvm)()(app-common)
// FILE: app.kt

fun box(): String {
    val commonBox = commonBox()
    val commonDirect = foo("")
    val platformDirect = foo()
    return if (commonBox == "common" && commonDirect == "common" && platformDirect == "platform") "OK"
    else "$commonBox/$commonDirect/$platformDirect"
}

