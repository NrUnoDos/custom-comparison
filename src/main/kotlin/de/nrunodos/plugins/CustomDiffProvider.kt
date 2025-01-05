package de.nrunodos.plugins

import com.intellij.diff.contents.DiffContent
import com.intellij.diff.lang.DiffIgnoredRangeProvider
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.fileTypes.FileTypes
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiWhiteSpace
import com.intellij.util.IncorrectOperationException

class CustomDiffProvider : DiffIgnoredRangeProvider {

  override fun getDescription(): String {
    return "Custom comparison"
  }

  override fun accepts(project: Project?, content: DiffContent): Boolean {
    return true
  }

  override fun getIgnoredRanges(project: Project?, text: CharSequence, content: DiffContent): MutableList<TextRange> {
    return ReadAction.compute<MutableList<TextRange>, IncorrectOperationException> {
      val fileType = content.contentType ?: FileTypes.UNKNOWN
      val psiFile = PsiFileFactory.getInstance(project).createFileFromText("", fileType, text)
      val visitor = IgnoredPsiVisitor()
      psiFile.accept(visitor)

      return@compute visitor.textRanges
    }
  }

  internal class IgnoredPsiVisitor : PsiElementVisitor() {

    private val config: CustomDiffConfigState = CustomDiffConfigState.getInstance()
    val textRanges = mutableListOf<TextRange>()

    override fun visitElement(element: PsiElement) {
      if (element.textLength == 0) {
        return
      }
      if (config.ignoreWhitespaces && element is PsiWhiteSpace) {
        textRanges.add(element.textRange)
        return
      }
      if (config.ignorePatterns.any { Regex(it).matches(element.text) }) {
        textRanges.add(element.textRange)
        return
      }
      element.acceptChildren(this)
    }
  }
}