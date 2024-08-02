plugins {
    kotlin("jvm")
    id("jps-compatible")
}

dependencies {
    api(project(":core:compiler.common.jvm"))
    api(project(":compiler:config.jvm"))
    api(libs.asm)
    api(libs.guava)
    compileOnly(intellijCore())
}

sourceSets {
    "main" { projectDefault() }
    "test" {}
}
