package de.nrunodos.plugins

import com.intellij.diff.DiffContext
import com.intellij.diff.DiffExtension
import com.intellij.diff.FrameDiffTool
import com.intellij.diff.requests.DiffRequest
import com.intellij.diff.tools.util.base.TextDiffSettingsHolder
import com.intellij.diff.tools.util.side.TwosideTextDiffViewer
import com.intellij.openapi.util.ActionCallback
import java.awt.BorderLayout

class ComparisonExtension : DiffExtension() {
    override fun onViewerCreated(
        viewer: FrameDiffTool.DiffViewer,
        context: DiffContext,
        request: DiffRequest,
    ) {
        if (viewer.javaClass.name == "com.intellij.diff.tools.dir.DirDiffViewer") {
            return
        }

        val gui = IntegratedConfigurationPanel(context, viewer)

        val settings = context.getUserData(TextDiffSettingsHolder.TextDiffSettings.KEY)
        settings?.addListener(object : TextDiffSettingsHolder.TextDiffSettings.Listener {
            override fun ignorePolicyChanged() {
                gui.updateVisibility()
            }
        }, viewer)

        if (viewer is TwosideTextDiffViewer) {
            viewer.editors.forEach { editor ->
                editor.addEditorMouseMotionListener(PsiPathHintMouseMotionListener())
                editor.addEditorMouseListener(PsiElementIgnoreMouseListener(viewer) {
                    gui.refreshConfiguration()
                })
            }
        }

        val component = viewer.component
        if (component.layout is BorderLayout) {
            component.add(gui.rootPanel, BorderLayout.SOUTH)
        } else {
            component.add(gui.rootPanel)
            component.revalidate()
        }
    }
}
