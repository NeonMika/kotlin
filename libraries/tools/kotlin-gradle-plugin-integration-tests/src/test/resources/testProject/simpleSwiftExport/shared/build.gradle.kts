import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

plugins {
    kotlin("multiplatform")
}

kotlin {
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    if (properties.containsKey("swiftexport.dsl.export")) {
        @OptIn(org.jetbrains.kotlin.swiftexport.ExperimentalSwiftExportDsl::class)
        swiftexport {
            export(project(":subproject"))
            export(project(":not-good-looking-project-name"))
        }

        sourceSets.commonMain {
            copySubprojectSrc("com/github/jetbrains/swiftexport", "Subproject.kt")
            copySubprojectSrc("com/github/jetbrains/swiftexport", "UglySubproject.kt")
        }
    } else if (properties.containsKey("swiftexport.dsl.customName")) {
        @OptIn(org.jetbrains.kotlin.swiftexport.ExperimentalSwiftExportDsl::class)
        swiftexport {
            moduleName.set("CustomShared")

            export(project(":subproject")) {
                moduleName.set("CustomSubProject")
            }
        }

        sourceSets.commonMain {
            copySubprojectSrc("com/github/jetbrains/swiftexport", "Subproject.kt")
        }
    } else if (properties.containsKey("swiftexport.dsl.flattenPackage")) {
        @OptIn(org.jetbrains.kotlin.swiftexport.ExperimentalSwiftExportDsl::class)
        swiftexport {
            flattenPackage.set("com.github.jetbrains.swiftexport")

            export(project(":subproject")) {
                flattenPackage.set("com.subproject.library")
            }
        }

        sourceSets.commonMain {
            copySubprojectSrc("com/github/jetbrains/swiftexport", "Subproject.kt")
        }
    } else if (properties.containsKey("swiftexport.dsl.fullSample")) {
        @OptIn(org.jetbrains.kotlin.swiftexport.ExperimentalSwiftExportDsl::class)
        swiftexport {
            moduleName.set("Shared")
            flattenPackage.set("com.github.jetbrains.swiftexport")

            export(project(":not-good-looking-project-name"))

            export(project(":subproject")) {
                moduleName.set("Subproject")
                flattenPackage.set("com.subproject.library")
            }
        }

        sourceSets.commonMain {
            copySubprojectSrc("com/github/jetbrains/swiftexport", "Subproject.kt")
            copySubprojectSrc("com/github/jetbrains/swiftexport", "UglySubproject.kt")

            dependencies {
                implementation(project(":subproject"))
                implementation(project(":not-good-looking-project-name"))
            }
        }
    } else {
        @OptIn(org.jetbrains.kotlin.swiftexport.ExperimentalSwiftExportDsl::class)
        swiftexport {}
    }
}

fun KotlinSourceSet.copySubprojectSrc(srcPackage: String, srcName: String) {
    val extraSrcDir = rootProject.rootDir.resolve("extraSrc")
    val rootSrcDir = kotlin.srcDirs.single()
    val packageDir = rootSrcDir.resolve(srcPackage)

    extraSrcDir.resolve(srcName).copyTo(packageDir.resolve(srcName))
}