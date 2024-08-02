/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.plugin.mpp.apple

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.work.DisableCachingByDefault
import java.io.File
import java.io.IOException
import java.nio.file.Files

@DisableCachingByDefault(because = "We are checking only file permissions")
internal abstract class CheckSandboxAndWriteProtectionTask : DefaultTask() {

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val directoryFile: Property<File>

    @get:Input
    abstract val userScriptSandboxingEnabled: Property<Boolean>

    @get:Input
    abstract val frameworkTaskName: Property<String>

    @TaskAction
    fun checkSandboxAndWriteProtection() {
        val dirAccessible = builtProductsDirAccessibility(directoryFile.orNull)
        when (dirAccessible) {
            DirAccessibility.NOT_ACCESSIBLE -> fireSandboxException(frameworkTaskName.get(), userScriptSandboxingEnabled.get())
            DirAccessibility.DOES_NOT_EXIST,
            DirAccessibility.ACCESSIBLE,
            -> if (userScriptSandboxingEnabled.get()) {
                fireSandboxException(frameworkTaskName.get(), true)
            }
        }
    }

    private enum class DirAccessibility {
        ACCESSIBLE,
        NOT_ACCESSIBLE,
        DOES_NOT_EXIST
    }

    private fun builtProductsDirAccessibility(builtProductsDir: File?): DirAccessibility {
        return if (builtProductsDir != null) {
            try {
                Files.createDirectories(builtProductsDir.toPath())
                val tempFile = File.createTempFile("sandbox", ".tmp", builtProductsDir)
                if (tempFile.exists()) {
                    tempFile.delete()
                }
                DirAccessibility.ACCESSIBLE
            } catch (e: IOException) {
                DirAccessibility.NOT_ACCESSIBLE
            }
        } else {
            DirAccessibility.DOES_NOT_EXIST
        }
    }

    private fun fireSandboxException(frameworkTaskName: String, userScriptSandboxingEnabled: Boolean) {
        val message = if (userScriptSandboxingEnabled) "You " else "BUILT_PRODUCTS_DIR is not accessible, probably you "
        throw IllegalStateException(
            message +
                    "have sandboxing for user scripts enabled." +
                    "\nTo make the $frameworkTaskName task pass, disable this feature. " +
                    "\nIn your Xcode project, navigate to \"Build Setting\", " +
                    "and under \"Build Options\" set \"User script sandboxing\" (ENABLE_USER_SCRIPT_SANDBOXING) to \"NO\". " +
                    "\nThen, run \"./gradlew --stop\" to stop the Gradle daemon" +
                    "\nFor more information, see documentation: https://jb.gg/ltd9e6"
        )
    }
}