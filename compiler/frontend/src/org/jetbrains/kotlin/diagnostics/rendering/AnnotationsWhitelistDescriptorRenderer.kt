/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.diagnostics.rendering

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.platform.PlatformSpecificDiagnosticComponents
import org.jetbrains.kotlin.renderer.DescriptorRenderer

data class DeclarationWithDiagnosticComponents(
    val declaration: DeclarationDescriptor,
    val diagnosticComponents: PlatformSpecificDiagnosticComponents
) : Iterable<Any> {
    override fun iterator() =
        sequenceOf(declaration, diagnosticComponents).iterator()
}

class AnnotationsWhitelistDescriptorRenderer(
    private val baseRenderer: DescriptorRenderer,
    private val toParameterRenderer: DescriptorRenderer.() -> DiagnosticParameterRenderer<DeclarationDescriptor>
) : DiagnosticParameterRenderer<DeclarationWithDiagnosticComponents> {
    override fun render(obj: DeclarationWithDiagnosticComponents, renderingContext: RenderingContext): String {
        /** STATISTICS ON DESTRUCTURING - type: Common, destructured variable total amount: 2, destructured variable amount without '_': 2, classId: org/jetbrains/kotlin/diagnostics/rendering/DeclarationWithDiagnosticComponents, actual properties name: declaration, diagnosticComponents  */
        val (descriptor, diagnosticComponents) = obj
        return baseRenderer.withOptions {
            annotationFilter = { annotation ->
                diagnosticComponents.isNullabilityAnnotation(annotation, descriptor)
            }
        }.toParameterRenderer().render(descriptor, renderingContext)
    }
}

fun DescriptorRenderer.withAnnotationsWhitelist(
    toParameterRenderer: DescriptorRenderer.() -> DiagnosticParameterRenderer<DeclarationDescriptor> = DescriptorRenderer::asRenderer
) = AnnotationsWhitelistDescriptorRenderer(this, toParameterRenderer)
