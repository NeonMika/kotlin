/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.native.checkers

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.*
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirTypeOperatorCallChecker
import org.jetbrains.kotlin.fir.analysis.diagnostics.native.FirNativeErrors
import org.jetbrains.kotlin.fir.expressions.*
import org.jetbrains.kotlin.fir.resolve.lookupSuperTypes
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.coneType

object FirNativeForwardDeclarationTypeOperatorChecker : FirTypeOperatorCallChecker() {
    override fun check(expression: FirTypeOperatorCall, context: CheckerContext, reporter: DiagnosticReporter) {
        val targetTypeRef = expression.conversionTypeRef
        val declarationToCheck = targetTypeRef.toRegularClassSymbol(context.session) ?: return
        val fwdKind = declarationToCheck.forwardDeclarationKindOrNull() ?: return

        when (expression.operation) {
            FirOperation.AS, FirOperation.SAFE_AS -> {
                val sourceTypeRef = expression.argument.typeRef
                val sourceClass = sourceTypeRef.toRegularClassSymbol(context.session)
                // It can make sense to avoid warning if sourceClass is subclass of class with such property,
                // but for the sake of simplicity, we don't do it now.
                if (sourceClass != null && sourceClass.classKind == fwdKind.classKind && sourceClass.name == declarationToCheck.name) {
                    val supers = lookupSuperTypes(
                        symbol = sourceClass,
                        lookupInterfaces = true,
                        deep = true,
                        useSiteSession = context.session,
                    )
                    if (supers.any { it.classId == fwdKind.matchSuperClassId }) {
                        return
                    }
                }
                reporter.reportOn(
                    expression.source,
                    FirNativeErrors.UNCHECKED_CAST_TO_FORWARD_DECLARATION,
                    expression.argument.typeRef.coneType,
                    sourceTypeRef.coneType,
                    context,
                )
            }
            FirOperation.IS, FirOperation.NOT_IS -> reporter.reportOn(
                expression.source,
                FirNativeErrors.CANNOT_CHECK_FOR_FORWARD_DECLARATION,
                targetTypeRef.coneType,
                context,
            )
            else -> {}
        }
    }
}