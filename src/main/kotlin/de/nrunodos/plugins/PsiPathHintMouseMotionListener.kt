package de.nrunodos.plugins

import com.intellij.codeInsight.hint.HintManager
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseMotionListener
import com.intellij.psi.PsiDocumentManager

class PsiPathHintMouseMotionListener : EditorMouseMotionListener {
    override fun mouseMoved(e: EditorMouseEvent) {
        if (e.area != null) {
            val editor = e.editor
            val project = editor.project ?: return

            runReadActionBlocking {
                val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document)
                val element = psiFile?.findElementAt(e.offset)

                if (element != null) {
                    HintManager.getInstance().showInformationHint(
                        editor,
                        "PSI: ${PsiPathUtils.retrievePsiPath(element)}"
                    )
                }
            }
        }
    }
}
