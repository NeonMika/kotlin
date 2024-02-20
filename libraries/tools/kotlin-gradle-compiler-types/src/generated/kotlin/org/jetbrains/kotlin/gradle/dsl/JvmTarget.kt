// DO NOT EDIT MANUALLY!
// Generated by org/jetbrains/kotlin/generators/arguments/GenerateGradleOptions.kt
// To regenerate run 'generateGradleOptions' task
@file:Suppress("RemoveRedundantQualifierName", "Deprecation", "Deprecation_Error", "DuplicatedCode")

package org.jetbrains.kotlin.gradle.dsl

enum class JvmTarget(val target: String) {
    JVM_1_8("1.8"),
    JVM_9("9"),
    JVM_10("10"),
    JVM_11("11"),
    JVM_12("12"),
    JVM_13("13"),
    JVM_14("14"),
    JVM_15("15"),
    JVM_16("16"),
    JVM_17("17"),
    JVM_18("18"),
    JVM_19("19"),
    JVM_20("20"),
    JVM_21("21"),
    ;

    companion object {
        @JvmStatic
        fun fromTarget(target: String): JvmTarget =
            JvmTarget.values().firstOrNull { it.target == target }
                ?: throw IllegalArgumentException("Unknown Kotlin JVM target: $target")

        @JvmStatic
        val DEFAULT = JVM_1_8
    }
}
