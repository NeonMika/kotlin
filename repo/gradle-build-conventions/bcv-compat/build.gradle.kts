plugins {
    `kotlin-dsl`
}

description = """
Binary Compatibility Validator - track ABI changes.

This project is a re-implementation of https://github.com/Kotlin/binary-compatibility-validator,
based on https://github.com/adamko-dev/kotlin-binary-compatibility-validator-mu.

bcv-compat supports new features not available in BCV:
 
- Isolated classpath. BCV needs kotlin-compiler-embeddable as a buildscript dependency,
  but this will be removed as a KGP dependency.
  https://youtrack.jetbrains.com/issue/KT-61706
  https://github.com/Kotlin/binary-compatibility-validator/issues/208
- Generate multiple ABI dumps from the same source code. 
"""

repositories {
    mavenCentral()
    gradlePluginPortal()
}

kotlin {
    jvmToolchain(8)
}

dependencies {
    implementation(libs.javaDiffUtils)
    compileOnly(libs.kotlinx.bcv)
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:${project.bootstrapKotlinVersion}")
}

val generateBcvProperties by tasks.registering {
    val generatedSrcDir = layout.buildDirectory.dir("src/generated/kotlin")
    outputs.dir(generatedSrcDir).withPropertyName("generatedSrcDir")
    outputs.cacheIf { true }

    val bcvVersion = libs.versions.kotlinx.bcv
    inputs.property("bcvVersion", bcvVersion)

    doLast {
        val outputDir = generatedSrcDir.get().asFile
        outputDir.mkdirs()
        outputDir.resolve("BcvProperties.kt").writeText(
            """
            |package kotlin_git.gradle_build_conventions.bcv.internal
            |
            |internal object BcvProperties {
            |    const val KOTLINX_BCV_VERSION = "${bcvVersion.get()}"
            |}
            |
            """.trimMargin()
        )
    }
}

kotlin.sourceSets.main {
    kotlin.srcDir(generateBcvProperties)
}

gradlePlugin {
    plugins {
        create("bcvCompat") {
            id = "kotlin-git.gradle-build-conventions.bcv-compat"
            implementationClass = "kotlin_git.gradle_build_conventions.bcv.BCVCompatPlugin"
        }
    }
}

tasks.withType<AbstractArchiveTask>().configureEach {
    // reproducible builds https://docs.gradle.org/8.8/userguide/working_with_files.html#sec:reproducible_archives
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}
