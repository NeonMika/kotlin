import org.gradle.jvm.tasks.Jar

plugins {
    java
    kotlin("jvm")
}

dependencies {
    api(kotlinStdlib())
    compileOnly(intellijCore())
    compileOnly(libs.asm)
}

sourceSets {
    "main" { projectDefault() }
}

tasks {
    "jar" {
        this as Jar
        manifest {
            attributes["Manifest-Version"] = 1.0
            attributes["PreMain-Class"] = "org.jetbrains.kotlin.testFramework.TestInstrumentationAgent"
        }
    }
}