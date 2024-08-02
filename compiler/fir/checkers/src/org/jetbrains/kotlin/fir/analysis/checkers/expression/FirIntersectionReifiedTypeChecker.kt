/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.expression

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.isExplicit
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirErrors
import org.jetbrains.kotlin.fir.expressions.FirQualifiedAccessExpression
import org.jetbrains.kotlin.fir.references.isError
import org.jetbrains.kotlin.fir.references.toResolvedCallableSymbol
import org.jetbrains.kotlin.fir.types.*

object FirIntersectionReifiedTypeChecker : FirQualifiedAccessExpressionChecker(MppCheckerKind.Common) {
    override fun check(expression: FirQualifiedAccessExpression, context: CheckerContext, reporter: DiagnosticReporter) {
        val calleeReference = expression.calleeReference
        if (expression.resolvedType.hasError() || calleeReference.isError()) return

        val callable = calleeReference.toResolvedCallableSymbol() ?: return
        if (callable.typeParameterSymbols.none { it.isReified }) return

        val typeParameters = callable.typeParameterSymbols
        val typeArguments = expression.typeArguments
        val count = minOf(typeArguments.size, typeParameters.size)
        for (i in 0..<count) {
            val argument = typeArguments[i]
            val parameter = typeParameters[i]
            if (parameter.isReified && !argument.isExplicit) {
                val source = argument.source ?: calleeReference.source
                val type = argument.toConeTypeProjection().type
                when {
                    type == null -> {} // Star projection -> do nothing
                    type.hasError() -> {} // Has error already -> do nothing
                    type is ConeIntersectionType -> {
                        reporter.reportOn(source, FirErrors.TYPE_INTERSECTION_AS_REIFIED, parameter, type.intersectedTypes, context)
                    }
                }
            }
        }
    }
}
