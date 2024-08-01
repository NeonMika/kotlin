/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package kotlin_git.gradle_build_conventions.bcv.targets

import org.gradle.api.Named
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import java.io.Serializable
import javax.inject.Inject

abstract class BCVTarget
@Inject
constructor(
    /**
     * The JVM platform being targeted.
     *
     * Targets with the same [platformType] will be grouped together into a single API declaration.
     */
    @get:Input
    val platformType: String,
) : BCVTargetSpec, Serializable, Named {

    @get:Input
    @get:Optional
    abstract override val enabled: Property<Boolean>

    @get:Classpath
    abstract override val inputClasses: ConfigurableFileCollection

    @get:Classpath
    @get:Optional
    abstract override val inputJar: RegularFileProperty

    /** @see kotlin_git.gradle_build_conventions.bcv.targets.BCVTarget.publicMarkers */
    @get:Input
    @get:Optional
    abstract override val publicMarkers: SetProperty<String>

    /** @see kotlin_git.gradle_build_conventions.bcv.targets.BCVTarget.publicPackages */
    @get:Input
    @get:Optional
    abstract override val publicPackages: SetProperty<String>

    /** @see kotlin_git.gradle_build_conventions.bcv.targets.BCVTarget.publicClasses */
    @get:Input
    @get:Optional
    abstract override val publicClasses: SetProperty<String>

    /** @see kotlin_git.gradle_build_conventions.bcv.targets.BCVTarget.ignoredMarkers */
    @get:Input
    @get:Optional
    abstract override val ignoredMarkers: SetProperty<String>

    /** @see kotlin_git.gradle_build_conventions.bcv.targets.BCVTarget.ignoredPackages */
    @get:Input
    @get:Optional
    abstract override val ignoredPackages: SetProperty<String>

    /** @see kotlin_git.gradle_build_conventions.bcv.targets.BCVTarget.ignoredClasses */
    @get:Input
    @get:Optional
    abstract override val ignoredClasses: SetProperty<String>

    @Internal
    override fun getName(): String = platformType
}
