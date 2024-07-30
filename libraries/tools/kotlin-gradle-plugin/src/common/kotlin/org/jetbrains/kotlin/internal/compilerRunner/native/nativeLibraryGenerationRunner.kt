/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.internal.compilerRunner.native

import org.gradle.api.file.FileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.build.report.metrics.BuildMetricsReporter
import org.jetbrains.kotlin.build.report.metrics.GradleBuildPerformanceMetric
import org.jetbrains.kotlin.build.report.metrics.GradleBuildTime
import org.jetbrains.kotlin.gradle.internal.ClassLoadersCachingBuildService
import org.jetbrains.kotlin.gradle.internal.properties.NativeProperties
import org.jetbrains.kotlin.gradle.targets.native.KonanPropertiesBuildService
import org.jetbrains.kotlin.gradle.utils.listProperty
import org.jetbrains.kotlin.gradle.utils.newInstance
import org.jetbrains.kotlin.gradle.utils.property

internal fun ObjectFactory.KotlinNativeLibraryGenerationRunner(
    metricsReporter: Provider<BuildMetricsReporter<GradleBuildTime, GradleBuildPerformanceMetric>>,
    classLoadersCachingBuildService: Provider<ClassLoadersCachingBuildService>,
    useXcodeMessageStyle: Provider<Boolean>,
    classpath: FileCollection,
    jvmArgs: ListProperty<String>,
    environmentBlacklist: Provider<Set<String>>,
): KotlinNativeToolRunner = newInstance(
    metricsReporter,
    classLoadersCachingBuildService,
    kotlinToolSpec(useXcodeMessageStyle, classpath, jvmArgs, environmentBlacklist)
)

private fun ObjectFactory.kotlinToolSpec(
    useXcodeMessageStyle: Provider<Boolean>,
    classpath: FileCollection,//nativeCompilerClasspath(nativeProperties.kotlinNativeCompilerJar, nativeProperties.actualNativeHomeDirectory),
    jvmArgs: ListProperty<String>,//nativeProperties.jvmArgs
    environmentBlacklist: Provider<Set<String>>,
) = KotlinNativeToolRunner.ToolSpec(
    displayName = property("generatePlatformLibraries"),
    optionalToolName = property("generatePlatformLibraries"),
    mainClass = property("org.jetbrains.kotlin.cli.utilities.MainKt"),
    daemonEntryPoint = useXcodeMessageStyle.nativeDaemonEntryPoint(),
    classpath = classpath,
    jvmArgs = jvmArgs,
    shouldPassArgumentsViaArgFile = property(false),
    systemProperties = nativeExecSystemProperties(useXcodeMessageStyle),
    environment = nativeExecLLVMEnvironment,
    environmentBlacklist = environmentBlacklist.get(),
).enableAssertions()
    .configureDefaultMaxHeapSize()

//private fun Provider<Boolean>.daemonEntryPoint() = map { useXcodeMessageStyle ->
//    if (useXcodeMessageStyle) "daemonMainWithXcodeRenderer" else "daemonMain"
//}
//
//private val NativeProperties.kotlinNativeCompilerJar: Provider<File>
//    get() = isUseEmbeddableCompilerJar.zip(actualNativeHomeDirectory) { useJar, nativeHomeDir ->
//        if (useJar) {
//            nativeHomeDir.resolve("konan/lib/kotlin-native-compiler-embeddable.jar")
//        } else {
//            nativeHomeDir.resolve("konan/lib/kotlin-native.jar")
//        }
//    }
//
//private fun ObjectFactory.nativeCompilerClasspath(
//    nativeProperties: NativeProperties
//) = fileCollection().from(
//    nativeProperties.kotlinNativeCompilerJar,
//    nativeProperties.actualNativeHomeDirectory.map { it.resolve("konan/lib/trove4j.jar") },
//)
//
//private fun execSystemProperties(
//    useXcodeMessageStyle: Provider<Boolean>
//) = useXcodeMessageStyle.map {
//    val messageRenderer = if (it) MessageRenderer.XCODE_STYLE else MessageRenderer.GRADLE_STYLE
//    mapOf(MessageRenderer.PROPERTY_KEY to messageRenderer.name)
//}.get()
//
//private val execLLVMEnvironment by lazy {
//    mutableMapOf<String, String>(
//        "LIBCLANG_DISABLE_CRASH_RECOVERY" to "1"
//    )
//}
