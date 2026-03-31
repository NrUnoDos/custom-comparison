package de.nrunodos.plugins

import com.intellij.diff.tools.util.base.DiffViewerBase
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseListener
import com.intellij.psi.PsiDocumentManager

class PsiElementIgnoreMouseListener(
    private val viewer: DiffViewerBase,
    private val refreshCallback: () -> Unit
) : EditorMouseListener {
    override fun mouseClicked(e: EditorMouseEvent) {
        if (e.mouseEvent.isAltDown) {
            val editor = e.editor
            val project = editor.project ?: return
            val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document)
            val element = psiFile?.findElementAt(e.offset)

            if (element != null) {
                val psiPath = PsiPathUtils.retrievePsiPath(element)
                toggleIgnoreElement(psiPath)
                refreshCallback()
                viewer.scheduleRediff()
            }
        }
    }

    private fun toggleIgnoreElement(psiPath: String) {
        val config = CustomDiffConfigState.getInstance()
        if (config.ignoredPsiPaths.contains(psiPath)) {
            config.ignoredPsiPaths.remove(psiPath)
        } else {
            config.ignoredPsiPaths.add(psiPath)
        }
    }
}
