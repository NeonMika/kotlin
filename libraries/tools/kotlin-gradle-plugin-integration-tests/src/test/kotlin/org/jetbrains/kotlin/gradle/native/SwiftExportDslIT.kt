/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.native

import org.gradle.util.GradleVersion
import org.jetbrains.kotlin.gradle.testbase.*
import org.jetbrains.kotlin.gradle.util.SimpleSwiftExportProperties
import org.jetbrains.kotlin.gradle.util.enableSwiftExport
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.condition.OS
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.io.path.*
import kotlin.test.assertNotNull

@OsCondition(supportedOn = [OS.MAC], enabledOnCI = [OS.MAC])
@DisplayName("Tests for Swift Export DSL")
@SwiftExportGradlePluginTests
@GradleTestVersions(minVersion = TestVersions.Gradle.G_7_4) // DefaultResolvedComponentResult with configuration cache is supported only after 7.4
class SwiftExportDslIT : KGPBaseTest() {

    @DisplayName("embedSwiftExport executes normally when only one target is enabled in Swift Export DSL")
    @GradleTest
    fun testSwiftExportDSLSingleProject(
        gradleVersion: GradleVersion,
        @TempDir testBuildDir: Path,
    ) {
        nativeProject(
            "simpleSwiftExport",
            gradleVersion,
        ) {
            projectPath.enableSwiftExport()

            build(
                ":shared:embedSwiftExportForXcode",
                environmentVariables = swiftExportEmbedAndSignEnvVariables(testBuildDir),
                buildOptions = defaultBuildOptions.copy(
                    configurationCache = BuildOptions.ConfigurationCacheValue.ENABLED,
                )
            ) {
                val buildProductsDir = this@nativeProject.gradleRunner.environment?.get("BUILT_PRODUCTS_DIR")?.let { File(it) }
                assertNotNull(buildProductsDir)

                val exportedKotlinPackagesSwiftModule = buildProductsDir.resolve("ExportedKotlinPackages.swiftmodule")
                val kotlinRuntime = buildProductsDir.resolve("KotlinRuntime")
                val libShared = buildProductsDir.resolve("libShared.a")
                val sharedSwiftModule = buildProductsDir.resolve("Shared.swiftmodule")
                val sharedBridgeShared = buildProductsDir.resolve("SharedBridge_Shared")

                assertDirectoryExists(exportedKotlinPackagesSwiftModule.toPath(), "ExportedKotlinPackages.swiftmodule doesn't exist")
                assertDirectoryExists(kotlinRuntime.toPath(), "KotlinRuntime doesn't exist")
                assertDirectoryExists(sharedSwiftModule.toPath(), "Shared.swiftmodule doesn't exist")
                assertDirectoryExists(sharedBridgeShared.toPath(), "SharedBridge_Shared doesn't exist")
                assertFileExists(libShared.toPath())
            }
        }
    }

    @DisplayName("embedSwiftExport executes normally when export module is defined in Swift Export DSL")
    @GradleTest
    fun testSwiftExportDSLExportModule(
        gradleVersion: GradleVersion,
        @TempDir testBuildDir: Path,
    ) {
        nativeProject(
            "simpleSwiftExport",
            gradleVersion,
        ) {
            projectPath.enableSwiftExport()

            build(
                ":shared:embedSwiftExportForXcode",
                "-P${SimpleSwiftExportProperties.DSL_EXPORT}",
                environmentVariables = swiftExportEmbedAndSignEnvVariables(testBuildDir),
                buildOptions = defaultBuildOptions.copy(
                    configurationCache = BuildOptions.ConfigurationCacheValue.ENABLED,
                )
            ) {
                val buildProductsDir = this@nativeProject.gradleRunner.environment?.get("BUILT_PRODUCTS_DIR")?.let { File(it) }
                assertNotNull(buildProductsDir)

                val exportedKotlinPackagesSwiftModule = buildProductsDir.resolve("ExportedKotlinPackages.swiftmodule")
                val kotlinRuntime = buildProductsDir.resolve("KotlinRuntime")
                val libShared = buildProductsDir.resolve("libShared.a")
                val notGoodLookingProjectSwiftModule = buildProductsDir.resolve("NotGoodLookingProjectName.swiftmodule")
                val sharedBridgeNotGoodLookingProject = buildProductsDir.resolve("SharedBridge_NotGoodLookingProjectName")
                val sharedSwiftModule = buildProductsDir.resolve("Shared.swiftmodule")
                val sharedBridgeShared = buildProductsDir.resolve("SharedBridge_Shared")
                val subprojectSwiftModule = buildProductsDir.resolve("Subproject.swiftmodule")
                val sharedBridgeSubproject = buildProductsDir.resolve("SharedBridge_Subproject")

                assertDirectoryExists(exportedKotlinPackagesSwiftModule.toPath(), "ExportedKotlinPackages.swiftmodule doesn't exist")
                assertDirectoryExists(kotlinRuntime.toPath(), "KotlinRuntime doesn't exist")
                assertDirectoryExists(sharedSwiftModule.toPath(), "Shared.swiftmodule doesn't exist")
                assertDirectoryExists(sharedBridgeShared.toPath(), "SharedBridge_Shared doesn't exist")
                assertDirectoryExists(notGoodLookingProjectSwiftModule.toPath(), "NotGoodLookingProjectName.swiftmodule doesn't exist")
                assertDirectoryExists(sharedBridgeNotGoodLookingProject.toPath(), "SharedBridge_NotGoodLookingProjectName doesn't exist")
                assertDirectoryExists(subprojectSwiftModule.toPath(), "Subproject.swiftmodule doesn't exist")
                assertDirectoryExists(sharedBridgeSubproject.toPath(), "SharedBridge_Subproject doesn't exist")
                assertFileExists(libShared.toPath())
            }
        }
    }

    @DisplayName("embedSwiftExport executes normally when custom module name is defined in Swift Export DSL")
    @GradleTest
    fun testSwiftExportDSLCustomModuleName(
        gradleVersion: GradleVersion,
        @TempDir testBuildDir: Path,
    ) {
        nativeProject(
            "simpleSwiftExport",
            gradleVersion,
        ) {
            projectPath.enableSwiftExport()

            build(
                ":shared:embedSwiftExportForXcode",
                "-P${SimpleSwiftExportProperties.DSL_CUSTOM_NAME}",
                environmentVariables = swiftExportEmbedAndSignEnvVariables(testBuildDir),
                buildOptions = defaultBuildOptions.copy(
                    configurationCache = BuildOptions.ConfigurationCacheValue.ENABLED,
                )
            ) {
                val buildProductsDir = this@nativeProject.gradleRunner.environment?.get("BUILT_PRODUCTS_DIR")?.let { File(it) }
                assertNotNull(buildProductsDir)

                val exportedKotlinPackagesSwiftModule = buildProductsDir.resolve("ExportedKotlinPackages.swiftmodule")
                val kotlinRuntime = buildProductsDir.resolve("KotlinRuntime")
                val libCustomShared = buildProductsDir.resolve("libCustomShared.a")
                val sharedSwiftModule = buildProductsDir.resolve("CustomShared.swiftmodule")
                val sharedBridgeShared = buildProductsDir.resolve("SharedBridge_CustomShared")
                val subprojectSwiftModule = buildProductsDir.resolve("CustomSubproject.swiftmodule")
                val sharedBridgeSubproject = buildProductsDir.resolve("SharedBridge_CustomSubproject")

                assertDirectoryExists(exportedKotlinPackagesSwiftModule.toPath(), "ExportedKotlinPackages.swiftmodule doesn't exist")
                assertDirectoryExists(kotlinRuntime.toPath(), "KotlinRuntime doesn't exist")
                assertDirectoryExists(sharedSwiftModule.toPath(), "Shared.swiftmodule doesn't exist")
                assertDirectoryExists(sharedBridgeShared.toPath(), "SharedBridge_Shared doesn't exist")
                assertDirectoryExists(subprojectSwiftModule.toPath(), "Subproject.swiftmodule doesn't exist")
                assertDirectoryExists(sharedBridgeSubproject.toPath(), "SharedBridge_Subproject doesn't exist")
                assertFileExists(libCustomShared.toPath())
            }
        }
    }

    @DisplayName("embedSwiftExport executes normally when package flatten rule is defined in Swift Export DSL")
    @GradleTest
    fun testSwiftExportDSLWithPackageFlatteringRuleEnabled(
        gradleVersion: GradleVersion,
        @TempDir testBuildDir: Path,
    ) {
        nativeProject(
            "simpleSwiftExport",
            gradleVersion,
        ) {
            projectPath.enableSwiftExport()

            build(
                ":shared:embedSwiftExportForXcode",
                "-P${SimpleSwiftExportProperties.DSL_FLATTEN_PACKAGE}",
                environmentVariables = swiftExportEmbedAndSignEnvVariables(testBuildDir),
                buildOptions = defaultBuildOptions.copy(
                    configurationCache = BuildOptions.ConfigurationCacheValue.ENABLED,
                )
            ) {
                val sharedSwiftPath = projectPath.resolve("shared/build/SwiftExport/iosArm64/Debug/files/Shared/Shared.swift")
                assert(sharedSwiftPath.readText().contains("public extension ExportedKotlinPackages.com.github.jetbrains.swiftexport"))

                val subprojectSwiftPath = projectPath.resolve("shared/build/SwiftExport/iosArm64/Debug/files/Subproject/Subproject.swift")
                assert(subprojectSwiftPath.readText().contains("public extension ExportedKotlinPackages.com.subproject.library"))
            }
        }
    }
}