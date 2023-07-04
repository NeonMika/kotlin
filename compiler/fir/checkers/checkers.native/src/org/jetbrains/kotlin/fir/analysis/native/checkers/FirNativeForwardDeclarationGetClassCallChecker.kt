/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirGetClassCallChecker
import org.jetbrains.kotlin.fir.analysis.checkers.toRegularClassSymbol
import org.jetbrains.kotlin.fir.analysis.diagnostics.native.FirNativeErrors
import org.jetbrains.kotlin.fir.analysis.native.checkers.forwardDeclarationKindOrNull
import org.jetbrains.kotlin.fir.expressions.*
import org.jetbrains.kotlin.fir.types.coneType


object FirNativeForwardDeclarationGetClassCallChecker : FirGetClassCallChecker() {
    override fun check(expression: FirGetClassCall, context: CheckerContext, reporter: DiagnosticReporter) {
        val declarationToCheck = expression.argument.typeRef.toRegularClassSymbol(context.session) ?: return

        if (expression.arguments.firstOrNull() !is FirResolvedQualifier) {
            return
        }

        if (declarationToCheck.forwardDeclarationKindOrNull() != null) {
            reporter.reportOn(
                expression.source,
                FirNativeErrors.FORWARD_DECLARATION_AS_CLASS_LITERAL,
                expression.argument.typeRef.coneType,
                context,
            )
        }
    }
}