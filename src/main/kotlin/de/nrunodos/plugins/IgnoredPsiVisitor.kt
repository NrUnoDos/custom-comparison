package de.nrunodos.plugins

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiWhiteSpace

internal class IgnoredPsiVisitor(private val config: CustomDiffConfigState = CustomDiffConfigState.getInstance()) :
    PsiElementVisitor() {
    val textRanges = mutableListOf<TextRange>()

    override fun visitElement(element: PsiElement) {
        if (element.textLength == 0) {
            return
        }
        if (config.ignoreWhitespaces && (element is PsiWhiteSpace || element.text.isBlank())) {
            textRanges.add(element.textRange)
            return
        }
        if (config.ignorePatterns.any { Regex(it).containsMatchIn(element.text) }) {
            textRanges.add(element.textRange)
            return
        }
        val currentPath = PsiPathUtils.retrievePsiPath(element)
        if (config.ignoredPsiPaths.any { currentPath.startsWith(it) }) {
            textRanges.add(element.textRange)
            return
        }
        element.acceptChildren(this)
    }
}
