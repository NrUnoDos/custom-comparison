package de.nrunodos.plugins

import com.intellij.diff.contents.DiffContent
import com.intellij.diff.lang.DiffIgnoredRangeProvider
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.fileTypes.FileTypes
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFileFactory
import com.intellij.util.IncorrectOperationException

class CustomDiffProvider : DiffIgnoredRangeProvider {
    override fun getDescription(): String = "Custom comparison"

    override fun accepts(
        project: Project?,
        content: DiffContent,
    ): Boolean = true

    override fun getIgnoredRanges(
        project: Project?,
        text: CharSequence,
        content: DiffContent,
    ): MutableList<TextRange> {
        return ReadAction.compute<MutableList<TextRange>, IncorrectOperationException> {
            val fileType = content.contentType ?: FileTypes.UNKNOWN
            val psiFile = PsiFileFactory.getInstance(project).createFileFromText("", fileType, text)
            val visitor = IgnoredPsiVisitor()
            psiFile.accept(visitor)

            return@compute visitor.textRanges
        }
    }
}
