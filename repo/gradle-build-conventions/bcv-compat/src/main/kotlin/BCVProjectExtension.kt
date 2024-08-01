/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package kotlin_git.gradle_build_conventions.bcv

import kotlin_git.gradle_build_conventions.bcv.internal.adding
import kotlin_git.gradle_build_conventions.bcv.internal.domainObjectContainer
import kotlin_git.gradle_build_conventions.bcv.targets.*
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.model.ReplacedBy
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import javax.inject.Inject

abstract class BCVProjectExtension
@Inject
constructor(
    private val objects: ObjectFactory,
) : BCVTargetSpec, ExtensionAware {

    /** Sets the default [BCVTarget.enabled] value for all [targets]. */
    abstract override val enabled: Property<Boolean>

    /** Sets the default [BCVTarget.ignoredPackages] value for all [targets]. */
    abstract override val ignoredPackages: SetProperty<String>

    /** Sets the default [BCVTarget.publicMarkers] for all [targets] */
    abstract override val publicMarkers: SetProperty<String>

    /** Sets the default [BCVTarget.publicPackages] for all [targets] */
    abstract override val publicPackages: SetProperty<String>

    /** Sets the default [BCVTarget.publicClasses] for all [targets] */
    abstract override val publicClasses: SetProperty<String>

    /** Sets the default [BCVTarget.ignoredMarkers] value for all [targets]. */
    abstract override val ignoredMarkers: SetProperty<String>

    @get:ReplacedBy("ignoredMarkers")
    @Deprecated("renamed to ignoredMarkers", ReplaceWith("ignoredMarkers"))
    abstract val nonPublicMarkers: SetProperty<String>

    /** Sets the default [BCVTarget.ignoredClasses] value for all [targets]. */
    abstract override val ignoredClasses: SetProperty<String>

    /**
     * The directory that contains the API declarations.
     *
     * Defaults to [BCVCompatPlugin.API_DIR].
     */
    abstract val outputApiDir: DirectoryProperty

    abstract val projectName: Property<String>

    abstract val kotlinxBinaryCompatibilityValidatorVersion: Property<String>

    val targets: NamedDomainObjectContainer<BCVTarget> =
        extensions.adding("targets") { objects.domainObjectContainer() }
}
